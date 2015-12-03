package org.cld.stock.strategy;

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
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.hadoop.CrawlTaskMapper;
import org.cld.stock.ETLUtil;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.util.DataMapper;

public class SelectStrategyByStockTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(SelectStrategyByStockTask.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private SelectStrategy[] scsl;

	private String marketBaseId;
	private String marketId;
	private String stockId;
	private Date startDate;
	private Date endDate;
	private String outputDir;
	
	public SelectStrategyByStockTask(){
	}
	
	public SelectStrategyByStockTask(SelectStrategy[] scsl, String marketBaseId, String marketId, String stockId, 
			Date startDate, Date endDate, String outputDir){
		this.scsl = scsl;
		this.marketBaseId = marketBaseId;
		this.setMarketId(marketId);
		this.stockId = stockId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.setOutputDir(outputDir);
		genId();
	}
	
	@Override
	public String genId(){
		String id = String.format("%s_%s_%s_%s", SelectStrategyByStockTask.class.getSimpleName(), stockId, 
				sdf.format(startDate), sdf.format(endDate));
		this.setId(id);
		return this.getId();
	}
	
	public static List<Object[]> getKVL(CrawlConf cconf, SelectStrategy[] bsl, String stockId, Date startDate, Date endDate){
		try{
			Map<DataMapper, List<Object>> tableResults = new HashMap<DataMapper, List<Object>>();
			for (SelectStrategy bs:bsl){
				bs.init();
				for (DataMapper jmap:bs.getDataMappers()){
					tableResults.put(jmap, null);
				}
			}
			for (DataMapper jmap:tableResults.keySet()){
				List<Object> lo = StockPersistMgr.getDataByStockDate(cconf, jmap, stockId, startDate, endDate);
				tableResults.put(jmap, lo);
			}
			List<Object[]> kvl = new ArrayList<Object[]>();
			for (SelectStrategy bs:bsl){
				Map<DataMapper, List<Object>> resultMap = new HashMap<DataMapper, List<Object>>();
				for (DataMapper jmap:bs.getDataMappers()){
					List<Object> lo = tableResults.get(jmap);
					resultMap.put(jmap, lo);
				}
				List<SelectCandidateResult> scrl = bs.selectByHistory(resultMap);
				for (SelectCandidateResult scr:scrl){
					Object[] scrss= new Object[2];
					scrss[0] = scr;
					scrss[1] = bs;
					kvl.add(scrss);
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
		try{
			CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
			List<Object[]> kvl = getKVL(cconf, this.scsl, stockId, startDate, endDate);
			for (Object[] kv:kvl){
				SelectCandidateResult scr = (SelectCandidateResult) kv[0];
				SelectStrategy ss = (SelectStrategy) kv[1];
				String key = String.format("%s,%s,%s,%s", ss.getName(), msdf.format(scr.getDt()), 
						ss.getOrderDirection(), ss.paramsToString());
				String value = String.format("%s,%.4f,%.3f", stockId, scr.getValue(), scr.getBuyPrice());
				context.write(new Text(key), new Text(value));
			}
		}catch(Exception e){
			logger.error("", e);
		}
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
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public SelectStrategy[] getScsl() {
		return scsl;
	}

	public void setScsl(SelectStrategy[] scsl) {
		this.scsl = scsl;
	}
}
