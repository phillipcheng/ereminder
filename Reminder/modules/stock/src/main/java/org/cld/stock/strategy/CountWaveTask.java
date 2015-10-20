package org.cld.stock.strategy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.DateTimeUtil;

public class CountWaveTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(StrategyValidationTask.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat detailSdf = new SimpleDateFormat("yyyyMMddhhmmss");
	public static SimpleDateFormat timeSdf = new SimpleDateFormat("hh:mm:ss");
	
	private String inputHdfsDefault;
	private String outputDir;
	private String stockid;
	private Date date;
	private float waveHeight; //2%, meaning low to high, in % unit, can be 1.5 meaning 1.5%
	private String marketBaseId;
	
	public CountWaveTask(){
	}
	
	public CountWaveTask(String inputHdfsDefault, String outputDir, String marketBaseId, String stockid, Date date, float waveHeight){
		this.inputHdfsDefault = inputHdfsDefault;
		this.outputDir = outputDir;
		this.marketBaseId = marketBaseId;
		this.stockid = stockid;
		this.date = date;
		this.waveHeight = waveHeight;
	}
	@Override
	public String genId(){
		String id = String.format("%s_%s_%s", CountWaveTask.class.getSimpleName(), stockid, sdf.format(date));
		setId(id);
		return getId();
	}
	@Override
	public boolean hasOutput(){
		return true;
	}
	@Override
	public void runMyselfAndOutput(Map<String, Object> params, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		StockConfig sc = StockUtil.getStockConfig(marketBaseId);
		String fileName="";
		try {
			CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
			Configuration hadoopConf = HadoopTaskLauncher.getHadoopConf(cconf.getNodeConf());
			hadoopConf.set("fs.default.name", inputHdfsDefault);
			FileSystem fs = FileSystem.get(hadoopConf);
			Date td = DateTimeUtil.tomorrow(date);
			fileName = String.format("%s/reminder/items/raw/ALL_%s/nasdaq-quote-tick/%s_%s", inputHdfsDefault, 
					sdf.format(td), stockid, sdf.format(date));
			BufferedReader isr= new BufferedReader(new InputStreamReader(fs.open(new Path(fileName))));
			String content = isr.readLine();
			Date lastTime = null;
			float lastPrice = 0;
			float totalPrice = 0;
			int upWaveCount=0;
			int downWaveCount=0;
			while (content!=null){
				//process content
				String v[] = content.split(",");
				Date dt = timeSdf.parse(v[1]);
				String p = v[2];
				if (p.endsWith("X")){
					p = p.substring(0, p.length()-1);
				}
				float price = Float.parseFloat(p);
				if (lastPrice==0){//init
					lastTime = dt;
					lastPrice = price;
				}else if (price > lastPrice*(1+waveHeight/100)){//upper limit
					upWaveCount++;
					lastPrice = price;
					totalPrice+=price;
				}else if (price < lastPrice*(1-waveHeight/100)){//lower limit
					downWaveCount++;
					lastPrice = price;
					totalPrice+=price;
				}
				content = isr.readLine();
			}
			isr.close();
			int totalWave = upWaveCount + downWaveCount;
			float avgPrice = 0;
			if (totalWave>0){
				avgPrice = totalPrice/totalWave;
			}
			//value: stockid,date,upWaveCount,downWaveCount,totalWave
			String value = String.format("%s,%d,%d,%s,%.3f", sdf.format(date), upWaveCount, downWaveCount, totalWave, avgPrice);
			context.write(new Text(stockid), new Text(value));
		} catch (Exception e) {
			logger.error("error processing file:" + fileName, e);
		}
	}
	@Override
	public String getOutputDir(Map<String, Object> paramMap){
		return String.format("%s/reduce/", this.outputDir);
	}
	//HCK stands for hadoop configure key
	public static final String HCK_STOREID="hck.storeid";
	public static final String HCK_MARKETID="hck.marketid";
	private static String submitTasks(String taskName, String propfile, List<Task> tl, CrawlConf cconf, String marketId){
		int mbMem = 512;
		String optValue = "-Xmx" + mbMem + "M";
		Map<String, String> hadoopJobParams = new HashMap<String, String>();
		hadoopJobParams.put("mapreduce.map.speculative", "false");
		hadoopJobParams.put("mapreduce.map.memory.mb", mbMem + "");
		hadoopJobParams.put("mapreduce.map.java.opts", optValue);
		hadoopJobParams.put(NLineInputFormat.LINES_PER_MAP, "200");
		hadoopJobParams.put(HCK_MARKETID, marketId);
		hadoopJobParams.put(HCK_STOREID, "nasdaq-ids");
		
		return CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tl, taskName, false, 
				CrawlTaskMapper.class.getName(), CountWaveReducer.class.getName(), hadoopJobParams);
	}
	
	public static String[] launch(String propfile, CrawlConf cconf, String marketBaseId, String marketId, 
			Date startDate, Date endDate, String inputHdfs, String outputDir, float waveHeight) {
		StockConfig sc = StockUtil.getStockConfig(marketBaseId);
		Date sd = startDate;
		List<Task> tl = new ArrayList<Task>();
		if (!StockUtil.isOpenDay(startDate, sc.getHolidays())){
			sd = StockUtil.getNextOpenDay(startDate, sc.getHolidays());
		}
		String[] stockids = ETLUtil.getStockIdByMarketId(sc, marketId, cconf, null);
		while(sd.before(endDate)){
			sd = StockUtil.getNextOpenDay(sd, sc.getHolidays());
			for (String stockid:stockids){
				Task t = new CountWaveTask(inputHdfs, outputDir, marketBaseId, stockid, sd, waveHeight);
				tl.add(t);
			}
		}
		String taskName = String.format("%s_%s", StrategyValidationTask.class.getSimpleName(), sdf.format(startDate));
		String jobId = submitTasks(taskName, propfile, tl, cconf, marketId);
		return new String[]{jobId};
	}
	
	public String getOutputDir() {
		return outputDir;
	}
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	public String getStockid() {
		return stockid;
	}
	public void setStockid(String stockid) {
		this.stockid = stockid;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public float getWaveHeight() {
		return waveHeight;
	}
	public void setWaveHeight(float waveHeight) {
		this.waveHeight = waveHeight;
	}

	public String getMarketBaseId() {
		return marketBaseId;
	}

	public void setMarketBaseId(String marketBaseId) {
		this.marketBaseId = marketBaseId;
	}

	public String getInputHdfsDefault() {
		return inputHdfsDefault;
	}

	public void setInputHdfsDefault(String inputHdfsDefault) {
		this.inputHdfsDefault = inputHdfsDefault;
	}

}
