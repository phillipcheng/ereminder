package org.cld.stock.analyze;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.CandleQuote;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.common.StockUtil;
import org.cld.stock.common.TradeHour;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.taskmgr.TaskConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskResult;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.TaskMapper;
import org.cld.util.DataMapper;
import org.cld.util.FileDataMapper;

public class SelectStrategyByStockTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(SelectStrategyByStockTask.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private List<SelectStrategy> bsl;
	private String stockId;
	private Date startDt;
	private Date endDt;
	private String outputDir;
	private TradeHour th;
	
	public SelectStrategyByStockTask(){
	}
	
	public SelectStrategyByStockTask(List<SelectStrategy> bsl, String stockId, 
			Date startDate, Date endDate, String outputDir, TradeHour th){
		this.bsl = bsl;
		this.stockId = stockId;
		this.startDt = startDate;
		this.endDt = endDate;
		this.setOutputDir(outputDir);
		this.th = th;
		genId();
	}
	
	@Override
	public String genId(){
		String id = String.format("%s_%s_%s_%s", SelectStrategyByStockTask.class.getSimpleName(), stockId, 
				sdf.format(startDt), sdf.format(endDt));
		this.setId(id);
		return this.getId();
	}
	
	private static List<Object[]> getKV(SelectStrategy bs, Map<String, List<? extends Object>> resultMap, String cqKey, List<CandleQuote> cqlist, 
			String stockId, MapContext<Object, Text, Text, Text> context) throws Exception{
		if (bs.allOneFetch()){
			bs.initHistoryData(resultMap);
		}
		
		List<Object[]> kvl = new ArrayList<Object[]>();
		
		CandleQuote preCq = null;
		for (CandleQuote cq:cqlist){
			if (bs.getLookupUnit()!=IntervalUnit.unspecified && bs.getLookupUnit()!=IntervalUnit.day){
				if (StockUtil.crossMarketStart(preCq, cq)){
					//logger.debug(String.format("cleanup needed. preCq:%s, cq:%s", preCq, cq));
					bs.cleanup();
				}
			}
			CqIndicators cqi = CqIndicators.addIndicators(cq, bs);
			SelectCandidateResult scr = bs.selectByStream(cqi);
			preCq = cqi.getCq();
			if (scr!=null){
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
	public static List<Object[]> getBuyOppList(AnalyzeConf aconf, List<SelectStrategy> bsl, String stockId, Date startDate, Date endDate,
			TradeHour th, MapContext<Object, Text, Text, Text> context){
		try{
			List<Object[]> kvl = new ArrayList<Object[]>(); //key value result returned if not in mapreduce
			Map<String, List<? extends Object>> resultMap = new HashMap<String, List<? extends Object>>();//shared for all bs
			List<CandleQuote> cqlist = null;
			FileDataMapper cqMapper = null;
			String cqKey = null;
			List<CqIndicators> cqilist = null;
			for (SelectStrategy bs:bsl){
				bs.init();
				for (String key: bs.getDataMappers().keySet()){
					DataMapper dm = bs.getDataMappers().get(key);
					if (dm.oneFetch() && !resultMap.containsKey(key)){
						List<? extends Object> lo = StockAnalyzePersistMgr.getDataByStockDate(aconf, dm, stockId, startDate, endDate, th);
						if (dm instanceof FileDataMapper && ((FileDataMapper)dm).isCqMapper()){
							cqlist = (List<CandleQuote>) lo;
							cqMapper = (FileDataMapper)dm;
							cqKey = key;
						}else{
							resultMap.put(key, lo);
						}
					}
				}
			}
			List<SelectStrategy> sbsFetchBsl = new ArrayList<SelectStrategy>();//step by step
			for (SelectStrategy bs:bsl){
				if (bs.allOneFetch()){
					List<Object[]> skvl = getKV(bs, null, cqKey, cqlist, stockId, context);
					kvl.addAll(skvl);
				}else{
					sbsFetchBsl.add(bs);
					for (String key:bs.getDataMappers().keySet()){
						DataMapper dm = bs.getDataMappers().get(key);
						if (!dm.oneFetch()){
							cqMapper = (FileDataMapper) dm;
							cqKey = key;
							break;
						}
					}
				}
			}
			int fetchSize=600;//fetch interval size, larger then all indicators' periods
			CqCachedReader sbsCqReader = null;
			Map<String, List<? extends Object>> sbsResults = new HashMap<String, List<? extends Object>>();
			Date lastDt = startDate;
			if (sbsFetchBsl.size()>0){
				try {
					sbsCqReader = StockAnalyzePersistMgr.getReader(aconf, cqMapper, stockId);
					StockAnalyzePersistMgr.getBTDDate(sbsCqReader.getBr(), cqMapper, startDate, startDate, th);
					while (lastDt.before(endDate)){
						cqlist = (List<CandleQuote>) StockAnalyzePersistMgr.getBTDDate(sbsCqReader.getBr(), cqMapper, fetchSize, endDate, th);
						if (cqlist.size()>0){
							lastDt = ((CandleQuote)cqlist.get(cqlist.size()-1)).getStartTime();
							for (SelectStrategy bs: sbsFetchBsl){
								List<Object[]> skvl = getKV(bs, sbsResults, cqKey, cqlist, stockId, context);
								kvl.addAll(skvl);
							}
						}else{
							break;
						}
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
	public TaskResult runMyself(Map<String, Object> params, boolean addDB,
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		AnalyzeConf cconf = (AnalyzeConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		getBuyOppList(cconf, bsl, stockId, startDt, endDt, th, context);
		return null;
	}
	
	private static String submitTasks(String taskName, String propfile, List<Task> tl, AnalyzeConf aconf, int mbMem, int maxSelectNumber){
		String optValue = "-Xmx" + mbMem + "M";
		Map<String, String> hadoopJobParams = new HashMap<String, String>();
		hadoopJobParams.put("mapreduce.map.speculative", "false");
		hadoopJobParams.put("mapreduce.map.memory.mb", mbMem + "");
		hadoopJobParams.put("mapreduce.map.java.opts", optValue);
		hadoopJobParams.put(NLineInputFormat.LINES_PER_MAP, "20");
		hadoopJobParams.put("mapreduce.job.reduces", "10");
		hadoopJobParams.put(SelectStrategyByStockReducer.MaxSelectNumber, maxSelectNumber+"");
		return TaskUtil.hadoopExecuteCrawlTasksWithReducer(propfile, aconf, tl, taskName, true, 
				TaskMapper.class, SelectStrategyByStockReducer.class, hadoopJobParams);
	}
	
	public static String[] launch(String propfile, AnalyzeConf aconf, String outputDir, 
			Map<String, List<SelectStrategy>> strategyMap, Date startDate, Date endDate, int maxSelectNumber, TradeHour th) {
		List<Task> tl = new ArrayList<Task>();
		for (String stockid: strategyMap.keySet()){
			List<SelectStrategy> bsl = strategyMap.get(stockid);
			if (bsl!=null){
				Task t = new SelectStrategyByStockTask(bsl, stockid, startDate, endDate, outputDir, th);
				tl.add(t);
			}
		}
		String sb = "";
		for (SelectStrategy bs:strategyMap.values().iterator().next()){//first select strategy list
			if (!sb.contains(bs.getName())){
				sb+=bs.getName();
				sb+=("_");	
			}
		}
		String taskName = String.format("%s_%s_%s_%s", SelectStrategyByStockTask.class.getSimpleName(), sdf.format(startDate), 
				sdf.format(endDate), sb.toString());
		String jobId = submitTasks(taskName, propfile, tl, aconf, 1024, maxSelectNumber);
		return new String[]{jobId};
	}
	
	@Override
	public boolean hasMultipleOutput(){
		return true;
	}

	@Override
	public String getOutputDir(Map<String, Object> paramMap, TaskConf tconf){
		return getOutputDir();
	}
	//
	public Date getStartDate() {
		return startDt;
	}
	public void setStartDate(Date startDate) {
		this.startDt = startDate;
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
	public List<SelectStrategy> getBsl() {
		return bsl;
	}
	public void setBsl(List<SelectStrategy> bsl) {
		this.bsl = bsl;
	}
	public TradeHour getTh() {
		return th;
	}
	public void setTh(TradeHour th) {
		this.th = th;
	}
}
