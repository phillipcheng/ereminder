package org.cld.stock.load;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;

@Entity
@DiscriminatorValue("org.cld.stock.load.ConvertTask")
public class TabularCSVConvertTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(TabularCSVConvertTask.class);
	
	public static final String[] inputFilePrefix= new String[]{"BalanceSheet", "ProfitStatement", "CashFlow"}; 
	
	public static String datetimeformat="yyyy-MM-dd-hh-mm-ss-SSS";
	public static SimpleDateFormat sdf = new SimpleDateFormat(datetimeformat);
	
	public static String encoding="GBK";
	private boolean needHeader = false;
	
	private String stockId;
	private String inputFolder;
	private CrawlConf cconf;

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public TabularCSVConvertTask(){	
	}
	
	public TabularCSVConvertTask(String stockId, String inputFolder){
		this.stockId = stockId;
		this.inputFolder = inputFolder;
		genId();
	}
	
	public TabularCSVConvertTask(String stockId, boolean needHeader){
		this.stockId = stockId;
		this.needHeader = needHeader;
		genId();
	}
	
	@Override
	public String genId(){
		String inputId = stockId + "-" + sdf.format(new Date());
		inputId = inputId.replace(":", "-");
		inputId = inputId.replace("/", "-");
		inputId = inputId.replace(".", "-");
		this.setId(inputId);
		return this.getId();
	}

	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		try{
			cconf = (CrawlConf) params.get(CrawlClientNode.TASK_RUN_PARAM_CCONF);
			FileSystem fs = FileSystem.get(HadoopTaskUtil.getHadoopConf(cconf.getNodeConf()));
			logger.info("process convert task: " + stockId);
			String inF = cconf.getTaskMgr().getHadoopCrawledItemFolder() + "/" + inputFolder + "/";
			for (String prefix:inputFilePrefix){
				String ifname = inF + prefix + "_" + stockId;
				String ofname = inF + prefix + "/" + stockId;
				Path ip = new Path(ifname);
				BufferedReader isr= new BufferedReader(new InputStreamReader(fs.open(ip), encoding));
				Path op = new Path(ofname);
				BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(fs.create(op,true), encoding));
				TabularCSVConverter.convert(stockId, isr, osw, needHeader);
				isr.close();
				osw.close();
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
}
