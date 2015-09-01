package org.cld.stock.sina;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.StringUtil;

public class RunSinaStock {
	protected static Logger logger =  LogManager.getLogger(RunSinaStock.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getDefaultCmdLine(){
		return "propFile marketId cmd startDate endDate";
	}
	
	public static void main(String[] args) {
		String propFile="";
		String marketId ="";
		String cmd="";
		String strStartDate = null;
		String strEndDate = null;
		if (args.length>=5){
			propFile = args[0]; //
			marketId = args[1]; // 
			cmd = args[2];  //
			String sd = args[3];
			if (!"-".equals(sd)){
				strStartDate = sd;
			}
			String ed = args[4];
			if (!"-".equals(ed)){
				strEndDate = ed;
			}
			Date startDate = null;
			Date endDate = null;
			try {
				if (strStartDate!=null){
					startDate = sdf.parse(strStartDate);
				}
				if (strEndDate!=null){
					endDate = sdf.parse(strEndDate);
				}
			} catch (ParseException e) {
				logger.error("", e);
			}
			SinaStockBase ssb = new SinaStockBase(propFile, marketId, startDate, endDate);
			int argIdx = 5;
			if ("run_task".equals(cmd)){//run already generated task
				if (args.length>=argIdx+1){
					String taskName = args[argIdx];
					String[] taskNames = taskName.split(",");
					Map<String, String> hadoopParams = new HashMap<String, String>();
					if (args.length>=argIdx+2){
						String params = args[argIdx+1];//mapreduce.map.memory.mb:3072,mapreduce.map.java.opts:-Xmx3072M
						hadoopParams = StringUtil.parseMapParams(params);
					}
					ssb.run_task(taskNames, hadoopParams);
				}else{
					System.out.println(getDefaultCmdLine() + " taskName");
				}
			}else if ("run_all_cmd".equals(cmd)){
				try {
					ssb.runAllCmd(startDate, endDate);
				} catch (InterruptedException e) {
					logger.error("", e);
				}
			}else if ("run_cmd".equals(cmd)){
				if (args.length>=argIdx+1){
					String cmdName = args[argIdx];
					ssb.runCmd(cmdName, marketId, strStartDate, strEndDate);
				}else{
					System.out.println(getDefaultCmdLine() + " cmdName");
				}
			}else if ("run_special".equals(cmd)){
				ssb.setMarketId(marketId);
				if (args.length>=argIdx+1){
					String method = args[argIdx];
					if (args.length>=argIdx+2){
						String specialParam = args[argIdx+1];
						ssb.setSpecialParam(specialParam);
					}
					Method m;
					try {
						m = ssb.getClass().getMethod(method);
						m.invoke(ssb);
					} catch (Exception e) {
						logger.error("", e);
					}
				}else{
					System.out.println(getDefaultCmdLine() + " methodName");
				}
			}else{
				logger.error("unknown command.");
			}
		}else{
			System.out.println("at least: propFile marketId allHistory cmd");
		}
	}
}
