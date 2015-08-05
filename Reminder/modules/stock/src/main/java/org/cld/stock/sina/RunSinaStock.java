package org.cld.stock.sina;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunSinaStock {
	protected static Logger logger =  LogManager.getLogger(RunSinaStock.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getDefaultCmdLine(){
		return "propFile marketId cmd startDate endDate";
	}
	
	public static void main(String[] args) throws Exception {
		String propFile="";
		String marketId ="";
		String cmd="";
		String strStartDate = null;
		String strEndDate = null;
		if (args.length>=5){
			propFile = args[0]; //
			marketId = args[1]; // 
			SinaStockBase ssb = new SinaStockBase(propFile, marketId);
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
			if (strStartDate!=null){
				startDate = sdf.parse(strStartDate);
			}
			if (strEndDate!=null){
				endDate = sdf.parse(strEndDate);
			}
			int argIdx = 5;
			if ("run_task".equals(cmd)){//run already generated task
				if (args.length>=argIdx+1){
					String taskName = args[argIdx];
					String[] taskNames = taskName.split(",");
					ssb.run_task(taskNames);
				}else{
					System.out.println(getDefaultCmdLine() + " taskName");
				}
			}else if ("run_all_cmd".equals(cmd)){
				ssb.runAllCmd(startDate, endDate);
			}else if ("run_cmd".equals(cmd)){
				if (args.length>=argIdx+1){
					String cmdName = args[argIdx];
					ssb.runCmd(cmdName, marketId, strStartDate, strEndDate);
				}else{
					System.out.println(getDefaultCmdLine() + " cmdName");
				}
			}else if ("run_special".equals(cmd)){
				Method m = ssb.getClass().getMethod(cmd);
				m.invoke(ssb);
			}else{
				logger.error("unknown command.");
			}
		}else{
			System.out.println("at least: propFile marketId allHistory cmd");
		}
	}
}
