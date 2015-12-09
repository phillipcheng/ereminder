package org.cld.stock.strategy;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.hadoop.CrawlTaskMapper;
import org.cld.stock.CandleQuote;
import org.cld.stock.ETLUtil;
import org.cld.stock.HdfsReader;
import org.cld.stock.CqIndicators;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.indicator.Indicator;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.util.DataMapper;
import org.cld.util.FileDataMapper;

public class SelectStrategyByStockTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(SelectStrategyByStockTask.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private SelectStrategy[] bsl;

	private String marketBaseId;
	private String marketId;
	private String stockId;
	private Date startDt;
	private Date endDt;
	private String outputDir;
	
	public SelectStrategyByStockTask(){
	}
	
	public SelectStrategyByStockTask(SelectStrategy[] bsl, String marketBaseId, String marketId, String stockId, 
			Date startDate, Date endDate, String outputDir){
		this.bsl = bsl;
		this.marketBaseId = marketBaseId;
		this.setMarketId(marketId);
		this.stockId = stockId;
		this.startDt = startDate;
		this.endDt = endDate;
		this.setOutputDir(outputDir);
		genId();
	}
	
	@Override
	public String genId(){
		String id = String.format("%s_%s_%s_%s", SelectStrategyByStockTask.class.getSimpleName(), stockId, 
				sdf.format(startDt), sdf.format(endDt));
		this.setId(id);
		return this.getId();
	}
	
	private static List<Object[]> getKV(SelectStrategy bs, Map<DataMapper, List<? extends Object>> resultMap, String stockId, 
			MapContext<Object, Text, Text, Text> context) throws Exception{
		List<Object[]> kvl = new ArrayList<Object[]>();
		List<SelectCandidateResult> scrl = bs.selectByHistory(resultMap);
		if (scrl!=null){
			for (SelectCandidateResult scr:scrl){
				if (context!=null){
					String key = String.format("%s,%s,%s,%s", bs.getName(), msdf.format(scr.getDt()), 
							bs.getOrderDirection(), bs.paramsToString());
					String value = String.format("%s,%.4f,%.3f", stockId, scr.getValue(), scr.getBuyPrice());
					context.write(new Text(key), new Text(value));
				}else{
					Object[] kv = new Object[]{scr, bs};
					kvl.add(kv);
				}
			}
		}
		return kvl;
	}
	
	//
	public static List<Object[]> getKVL(CrawlConf cconf, SelectStrategy[] bsl, String stockId, Date startDate, Date endDate,
			MapContext<Object, Text, Text, Text> context){
		try{
			List<Object[]> kvl = new ArrayList<Object[]>(); //key value result returned if not in mapreduce
			Map<DataMapper, List<? extends Object>> resultMap = new HashMap<DataMapper, List<? extends Object>>();//shared for all bs
			List<CandleQuote> cqlist = null;
			FileDataMapper cqMapper = null;
			List<CqIndicators> cqilist = null;
			for (SelectStrategy bs:bsl){
				bs.init();
				for (DataMapper dm: bs.getDataMappers()){
					if (dm.oneFetch() && !resultMap.containsKey(dm)){
						List<? extends Object> lo = StockPersistMgr.getDataByStockDate(cconf, dm, stockId, startDate, endDate);
						if (dm instanceof FileDataMapper && ((FileDataMapper)dm).isCqMapper()){
							cqlist = (List<CandleQuote>) lo;
							cqMapper = (FileDataMapper)dm;
						}else{
							resultMap.put(dm, lo);
						}
					}
				}
			}
			List<SelectStrategy> sbsFetchBsl = new ArrayList<SelectStrategy>();//step by step
			for (SelectStrategy bs:bsl){
				if (bs.allOneFetch()){
					cqilist = CqIndicators.addIndicators(null, null, cqlist, bs);
					resultMap.put(cqMapper, cqilist);
					bs.initData(resultMap);
					List<Object[]> skvl = getKV(bs, null, stockId, context);
					kvl.addAll(skvl);
				}else{
					sbsFetchBsl.add(bs);
					for (DataMapper dm:bs.getDataMappers()){
						if (!dm.oneFetch()){
							cqMapper = (FileDataMapper) dm;
							break;
						}
					}
				}
			}
			int fetchSize=600;//fetch interval size, larger then all indicators' periods
			HdfsReader sbsCqReader = null;
			Map<DataMapper, List<? extends Object>> sbsResults = new HashMap<DataMapper, List<? extends Object>>();
			Date lastDt = startDate;
			if (sbsFetchBsl.size()>0){
				try {
					sbsCqReader = StockPersistMgr.getReader(cconf, cqMapper, stockId);
					StockPersistMgr.getBTDDate(sbsCqReader.getBr(), cqMapper, startDate, startDate);
					List<CandleQuote> prevCqlist = null;
					Map<SelectStrategy, CqIndicators> prevCqiMap = new HashMap<SelectStrategy, CqIndicators>();
					while (lastDt.before(endDate)){
						cqlist = (List<CandleQuote>) StockPersistMgr.getBTDDate(sbsCqReader.getBr(), cqMapper, fetchSize);
						if (cqlist.size()>0){
							lastDt = ((CandleQuote)cqlist.get(cqlist.size()-1)).getStartTime();
							for (SelectStrategy bs: sbsFetchBsl){
								cqilist = CqIndicators.addIndicators(prevCqiMap.get(bs), prevCqlist, cqlist, bs);
								CqIndicators pCqi = cqilist.get(cqilist.size()-1);
								prevCqiMap.put(bs, pCqi);
								sbsResults.put(cqMapper, cqilist);
								List<Object[]> skvl = getKV(bs, sbsResults, stockId, context);
								kvl.addAll(skvl);
							}
						}else{
							break;
						}
						prevCqlist = cqlist;
					}
				}finally{
					sbsCqReader.close();
				}
			}
			return kvl;
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
	/**
	 * input: select strategies
	 * output key: ss.name, dt, orderDir, params
	 * output value: stockid, value, buyPrice
	 */
	@Override
	public void runMyselfAndOutput(Map<String, Object> params, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		getKVL(cconf, bsl, stockId, startDt, endDt, context);
	}
	
	private static String submitTasks(String taskName, String propfile, List<Task> tl, CrawlConf cconf, int mbMem, int maxSelectNumber){
		String optValue = "-Xmx" + mbMem + "M";
		Map<String, String> hadoopJobParams = new HashMap<String, String>();
		hadoopJobParams.put("mapreduce.map.speculative", "false");
		hadoopJobParams.put("mapreduce.map.memory.mb", mbMem + "");
		hadoopJobParams.put("mapreduce.map.java.opts", optValue);
		hadoopJobParams.put(NLineInputFormat.LINES_PER_MAP, "20");
		hadoopJobParams.put("mapreduce.job.reduces", "10");
		hadoopJobParams.put(SelectStrategyByStockReducer.MaxSelectNumber, maxSelectNumber+"");
		return CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tl, taskName, true, 
				CrawlTaskMapper.class, SelectStrategyByStockReducer.class, hadoopJobParams);
	}
	
	public static String[] launch(String propfile, CrawlConf cconf, String marketBaseId, String marketId, String outputDir, 
			SelectStrategy[] bsl, Date startDate, Date endDate, int maxSelectNumber) {
		StockConfig sc = StockUtil.getStockConfig(marketBaseId);
		List<Task> tl = new ArrayList<Task>();
		String[] stockids = ETLUtil.getStockIdByMarketId(sc, marketId, cconf, null);
		for (String stockid: stockids){
			Task t = new SelectStrategyByStockTask(bsl, marketBaseId, marketId, stockid, startDate, endDate, outputDir);
			tl.add(t);
		}
		String sb = "";
		for (SelectStrategy bs:bsl){
			if (!sb.contains(bs.getName())){
				sb+=bs.getName();
				sb+=("_");	
			}
		}
		String taskName = String.format("%s_%s_%s_%s", SelectStrategyByStockTask.class.getSimpleName(), sdf.format(startDate), 
				sdf.format(endDate), sb.toString());
		String jobId = submitTasks(taskName, propfile, tl, cconf, 1024, maxSelectNumber);
		return new String[]{jobId};
	}
	
	//
	@Override
	public boolean hasOutput(){
		return true;
	}
	@Override
	public boolean hasMultipleOutput(){
		return true;
	}
	@Override
	public String getOutputDir(Map<String, Object> paramMap){
		return this.getOutputDir();
	}
	//
	public Date getStartDate() {
		return startDt;
	}
	public void setStartDate(Date startDate) {
		this.startDt = startDate;
	}
	public String getMarketBaseId() {
		return marketBaseId;
	}
	public void setMarketBaseId(String marketBaseId) {
		this.marketBaseId = marketBaseId;
	}

	public String getMarketId() {
		return marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public Date getEndDate() {
		return endDt;
	}

	public void setEndDate(Date endDate) {
		this.endDt = endDate;
	}

	public SelectStrategy[] getScsl() {
		return bsl;
	}

	public void setScsl(SelectStrategy[] scsl) {
		this.bsl = scsl;
	}
}
