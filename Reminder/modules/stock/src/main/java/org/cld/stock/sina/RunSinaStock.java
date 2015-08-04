package org.cld.stock.sina;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

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
		String startDate = null;
		String endDate = null;
		if (args.length>=5){
			propFile = args[0]; //
			marketId = args[1]; // 
			SinaStockBase ssb = new SinaStockBase(propFile, marketId);
			cmd = args[2];  //
			String sd = args[3];
			if (!"-".equals(sd)){
				startDate = sd;
			}
			String ed = args[4];
			if (!"-".equals(ed)){
				endDate = ed;
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
			}else if ("run_cmd".equals(cmd)){
				if (args.length>=argIdx+1){
					String cmdName = args[argIdx];
					ssb.runCmd(cmdName, marketId, startDate, endDate);
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
