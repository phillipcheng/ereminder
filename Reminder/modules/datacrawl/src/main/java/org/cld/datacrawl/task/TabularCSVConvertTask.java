package org.cld.datacrawl.task;

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
import org.etl.csv.TabularCSVConverter;

@Entity
@DiscriminatorValue("org.cld.stock.load.TabularCSVConvertTask")
public class TabularCSVConvertTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(TabularCSVConvertTask.class);
	
	public static String datetimeformat="yyyy-MM-dd-hh-mm-ss-SSS";
	public static SimpleDateFormat sdf = new SimpleDateFormat(datetimeformat);
	
	public static String encoding="GBK";
	private boolean needHeader = false;
	
	private String tableId;

	private String inputFolder;
	private String outputFolder;
	//need to generate getter and setter for task serialization, if not this will be null
	public String getInputFolder() {
		return inputFolder;
	}

	public void setInputFolder(String inputFolder) {
		this.inputFolder = inputFolder;
	}

	private CrawlConf cconf;

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public TabularCSVConvertTask(){	
	}
	
	public TabularCSVConvertTask(String tableId, String inputFolder, String outputFolder){
		this.tableId = tableId;
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder;
		genId();
	}
	
	public TabularCSVConvertTask(String tableId, boolean needHeader){
		this.tableId = tableId;
		this.needHeader = needHeader;
		genId();
	}
	
	@Override
	public String genId(){
		String inputId = tableId + "-" + sdf.format(new Date());
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
			logger.info("process convert task: " + tableId);
			String inF = cconf.getTaskMgr().getHadoopCrawledItemFolder() + "/" + inputFolder + "/";
			String outF = cconf.getTaskMgr().getHadoopCrawledItemFolder() + "/" + outputFolder + "/";
			String ifname = inF + tableId;
			String ofname = outF + tableId;
			Path ip = new Path(ifname);
			BufferedReader isr= new BufferedReader(new InputStreamReader(fs.open(ip), encoding));
			Path op = new Path(ofname);
			BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(fs.create(op,true), encoding));
			TabularCSVConverter.convert(tableId, isr, osw, needHeader);
			isr.close();
			osw.close();
			
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
}
