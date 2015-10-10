package org.cld.stock.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.stock.ETLUtil;
import org.cld.stock.StockConfig;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;

public class PerStockTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String stockId;
	private String whichDay;
	
	public PerStockTask(String stockId, String whichDay){
		this.stockId = stockId;
		this.whichDay = whichDay;
	}
	
	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		//
		return null;
	}


	public static String[] launch(StockConfig sc, String marketId, CrawlConf cconf, String propfile, String whichDay){
		List<String> allIds = Arrays.asList(ETLUtil.getStockIdByMarketId(sc, marketId, cconf, ""));
		List<Task> tl = new ArrayList<Task>();
		for (String stockid: allIds){
			Task t = new PerStockTask(stockid, whichDay);
			tl.add(t);
		}
		int mbMem = 256;
		String optValue = "-Xmx" + mbMem + "M";
		Map<String, String> hadoopJobParams = new HashMap<String, String>();
		hadoopJobParams.put("mapreduce.map.speculative", "false");//since we do not allow same map multiple instance
		hadoopJobParams.put("mapreduce.map.memory.mb", mbMem+"");
		hadoopJobParams.put("mapreduce.map.java.opts", optValue);
		hadoopJobParams.put(NLineInputFormat.LINES_PER_MAP, "10");
		String jobId = CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tl, PerStockTask.class.getSimpleName(), false, hadoopJobParams);
		if (jobId!=null){
			return new String[]{jobId};
		}else{
			return new String[]{};
		}
	}
	//
	public String getStockId() {
		return stockId;
	}
	public void setStockId(String stockId) {
		this.stockId = stockId;
	}
	public String getWhichDay() {
		return whichDay;
	}
	public void setWhichDay(String whichDay) {
		this.whichDay = whichDay;
	}
}
