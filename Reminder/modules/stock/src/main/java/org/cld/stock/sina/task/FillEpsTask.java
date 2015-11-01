package org.cld.stock.sina.task;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.cld.hadooputil.NullKeyCopyTextReducer;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.sina.SinaStockPersistMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.util.DateTimeUtil;
import org.cld.util.jdbc.DBConnConf;

public class FillEpsTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(FillEpsTask.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat detailSdf = new SimpleDateFormat("yyyyMMddhhmmss");
	
	private Date startDt;
	private Date endDt;
	private Date dt;
	private String marketBaseId;
	private DBConnConf dbconf;
	private String outputDir;
	
	public FillEpsTask(){
	}
	
	public FillEpsTask(Date dt, String marketBaseId, DBConnConf dbconf, Date startDt, Date endDt, String outputDir){
		this.dt = dt;
		this.marketBaseId = marketBaseId;
		this.setDbconf(dbconf);
		this.startDt = startDt;
		this.endDt = endDt;
		this.outputDir = outputDir;
		genId();
	}
	
	@Override
	public String genId(){
		String id = String.format("%s_%s", FillEpsTask.class.getSimpleName(), sdf.format(dt));
		this.setId(id);
		return this.getId();
	}

	@Override
	public boolean hasOutput(){
		return true;
	}
	
	@Override
	public boolean hasMultipleOutput(){
		return false;
	}
	
	@Override
	public String getOutputDir(Map<String, Object> paramMap){
		return outputDir;
	}
	
	@Override
	public void runMyselfAndOutput(Map<String, Object> params, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		try{
			StockConfig sc = StockUtil.getStockConfig(this.marketBaseId);
			List<List<Object>> lol = SinaStockPersistMgr.getObservedEpsByDate(sc, dbconf, dt);
			logger.info(String.format("get %d observed eps by %s with dbconf:%s", lol.size(), sdf.format(dt), dbconf.toString()));
			Map<String, Float> annualEpsMap = new HashMap<String, Float>();
			Map<Date, List<String>> endDtStockListMap = new HashMap<Date, List<String>>();
			for (List<Object> lo:lol){
				String stockId = (String) lo.get(0);
				Date quarterEndDt = (Date)lo.get(1);
				if (endDtStockListMap.containsKey(quarterEndDt)){
					endDtStockListMap.get(quarterEndDt).add(stockId);
				}else{
					List<String> sl = new ArrayList<String>();
					sl.add(stockId);
					endDtStockListMap.put(quarterEndDt, sl);
				}
				double eps = (double) lo.get(2);
				Calendar cal = Calendar.getInstance();
				cal.setTime(quarterEndDt);
				int month = cal.get(Calendar.MONTH)+1;
				int quarter =  month/3;
				if (quarter>0){
					float annualEps = (float) (eps*4/quarter);
					annualEpsMap.put(stockId, annualEps);
				}else{
					logger.error(String.format("quarter is not positive for stock:%s, quarterEndDt:%s", stockId, sdf.format(quarterEndDt)));
				}
			}
			Map<String, Double> todayFqIdxMap = SinaStockPersistMgr.getFQIdx(sc, dbconf, dt);
			Map<String, Double> thatdayFqIdxMap = new HashMap<String, Double>();
			for (Date ed:endDtStockListMap.keySet()){
				List<String> sl = endDtStockListMap.get(ed);
				Map<String, Double> thatdayPartialFqIdxMap = SinaStockPersistMgr.getFQIdx(sc, dbconf, ed);
				thatdayPartialFqIdxMap.keySet().retainAll(sl);
				thatdayFqIdxMap.putAll(thatdayPartialFqIdxMap);
			}
			for (String stockId: annualEpsMap.keySet()){
				float annualEps = annualEpsMap.get(stockId);
				double todayFqIdx = todayFqIdxMap.get(stockId);
				double thatdayFqIdx = thatdayFqIdxMap.get(stockId);
				float adjAnnualEps = (float) (annualEps * thatdayFqIdx / todayFqIdx);
				String updateSql = String.format("update SinaMarketFQ set obsEps = %.3f where stockid='%s' and dt='%s';", adjAnnualEps, stockId, sdf.format(dt));
				context.write(new Text(sdf.format(dt)), new Text(updateSql));
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	private static String submitTasks(String taskName, String propfile, List<Task> tl, CrawlConf cconf){
		int mbMem = 512;
		String optValue = "-Xmx" + mbMem + "M";
		Map<String, String> hadoopJobParams = new HashMap<String, String>();
		hadoopJobParams.put("mapreduce.map.speculative", "false");
		hadoopJobParams.put("mapreduce.map.memory.mb", mbMem + "");
		hadoopJobParams.put("mapreduce.map.java.opts", optValue);
		hadoopJobParams.put(NLineInputFormat.LINES_PER_MAP, "1");
		return CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tl, taskName, false, 
				CrawlTaskMapper.class, NullKeyCopyTextReducer.class, hadoopJobParams);
	}
	
	public static String[] launch(String propfile, CrawlConf cconf, String marketBaseId,  
			String marketId, Date startDate, Date endDate, DBConnConf dbconf, String outputDir) {
		StockConfig sc = StockUtil.getStockConfig(marketBaseId);
		Date dt = startDate;
		if (dt==null){
			dt = SinaStockPersistMgr.getLatestFilledEps(sc, dbconf);
			dt = DateTimeUtil.tomorrow(dt);
		}
		if (!StockUtil.isOpenDay(dt, sc.getHolidays())){
			dt = StockUtil.getNextOpenDay(dt, sc.getHolidays());
		}
		List<Task> tl = new ArrayList<Task>();
		while (dt.before(endDate)){
			FillEpsTask t = new FillEpsTask(dt, marketBaseId, dbconf, startDate, endDate, outputDir);
			tl.add(t);
			dt = StockUtil.getNextOpenDay(dt, sc.getHolidays());
		}
		
		String taskName = String.format("%s_%s", FillEpsTask.class.getSimpleName(), sdf.format(dt));
		String jobId = submitTasks(taskName, propfile, tl, cconf);
		return new String[]{jobId};
	}

	//
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

	public Date getDt() {
		return dt;
	}

	public void setDt(Date dt) {
		this.dt = dt;
	}

	public Date getStartDt() {
		return startDt;
	}

	public void setStartDt(Date startDt) {
		this.startDt = startDt;
	}

	public Date getEndDt() {
		return endDt;
	}

	public void setEndDt(Date endDt) {
		this.endDt = endDt;
	}
}
