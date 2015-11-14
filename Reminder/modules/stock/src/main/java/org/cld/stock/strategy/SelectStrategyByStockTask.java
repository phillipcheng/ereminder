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
import org.cld.taskmgr.entity.Task;
import org.cld.util.jdbc.DBConnConf;
import org.cld.util.jdbc.JDBCMapper;

public class SelectStrategyByStockTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(SelectStrategyByStockTask.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat detailSdf = new SimpleDateFormat("yyyyMMddhhmmss");
	
	private SelectStrategy[] scsl;

	private String marketBaseId;
	private String marketId;
	private String stockId;
	private Date startDate;
	private Date endDate;
	private DBConnConf dbconf;
	private String outputDir;
	
	public SelectStrategyByStockTask(){
	}
	
	public SelectStrategyByStockTask(SelectStrategy[] scsl, String marketBaseId, String marketId, String stockId, 
			Date startDate, Date endDate, String outputDir, DBConnConf dbconf){
		this.scsl = scsl;
		this.marketBaseId = marketBaseId;
		this.setMarketId(marketId);
		this.stockId = stockId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.setOutputDir(outputDir);
		this.setDbconf(dbconf);
		genId();
	}
	
	@Override
	public String genId(){
		String id = String.format("%s_%s_%s_%s", SelectStrategyByStockTask.class.getSimpleName(), stockId, 
				sdf.format(startDate), sdf.format(endDate));
		this.setId(id);
		return this.getId();
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
			Map<JDBCMapper, List<Object>> tableResults = new HashMap<JDBCMapper, List<Object>>();
			for (SelectStrategy ss:scsl){
				ss.init();
				for (JDBCMapper jmap:ss.getTableMappers()){
					tableResults.put(jmap, null);
				}
			}
			for (JDBCMapper jmap:tableResults.keySet()){
				List<Object> lo = StockPersistMgr.getDataByStockDate(dbconf, jmap, stockId, startDate, endDate);
				tableResults.put(jmap, lo);
			}
			for (SelectStrategy ss:scsl){
				Map<JDBCMapper, List<Object>> resultMap = new HashMap<JDBCMapper, List<Object>>();
				for (JDBCMapper jmap:ss.getTableMappers()){
					List<Object> lo = tableResults.get(jmap);
					resultMap.put(jmap, lo);
				}
				List<SelectCandidateResult> scrl = ss.selectByHistory(resultMap);
				if (scrl!=null){
					for (SelectCandidateResult scr:scrl){
						String key = String.format("%s,%s,%s,%s", ss.getName(), scr.getDt(), ss.getOrderDirection(), ss.paramsToString());
						String value = String.format("%s,%.4f,%.3f", stockId, scr.getValue(), scr.getBuyPrice());
						context.write(new Text(key), new Text(value));
					}
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	private static String submitTasks(String taskName, String propfile, List<Task> tl, CrawlConf cconf, int mbMem){
		String optValue = "-Xmx" + mbMem + "M";
		Map<String, String> hadoopJobParams = new HashMap<String, String>();
		hadoopJobParams.put("mapreduce.map.speculative", "false");
		hadoopJobParams.put("mapreduce.map.memory.mb", mbMem + "");
		hadoopJobParams.put("mapreduce.map.java.opts", optValue);
		hadoopJobParams.put(NLineInputFormat.LINES_PER_MAP, "20");
		return CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tl, taskName, true, 
				CrawlTaskMapper.class, SelectStrategyByStockReducer.class, hadoopJobParams);
	}
	
	public static String[] launch(String propfile, CrawlConf cconf, String marketBaseId, String marketId, String outputDir, 
			SelectStrategy[] bsl, Date startDate, Date endDate, DBConnConf dbconf) {
		StockConfig sc = StockUtil.getStockConfig(marketBaseId);
		List<Task> tl = new ArrayList<Task>();
		String[] stockids = ETLUtil.getStockIdByMarketId(sc, marketId, cconf, null);
		for (String stockid: stockids){
			Task t = new SelectStrategyByStockTask(bsl, marketBaseId, marketId, stockid, startDate, endDate, outputDir, dbconf);
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
		String jobId = submitTasks(taskName, propfile, tl, cconf, 1024);
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

	public DBConnConf getDbconf() {
		return dbconf;
	}

	public void setDbconf(DBConnConf dbconf) {
		this.dbconf = dbconf;
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
