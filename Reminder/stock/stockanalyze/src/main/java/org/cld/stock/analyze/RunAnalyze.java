package org.cld.stock.analyze;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.TaskUtil;

public class RunAnalyze {
	protected static Logger logger =  LogManager.getLogger(RunAnalyze.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//x.properties baseMarketId startDate endDate sn:strategyName1_strategyName2,step:1,th:normal
	public static String getDefaultCmdLine(){
		return "propFile baseMarketId startDate endDate strategyName step tradehour";
	}
	
	public static void main(String[] args) {
		String propFile="";
		String stockBase = "";
		String strStartDate = null;
		String strEndDate = null;
		if (args.length>=5){
			propFile = args[0]; //
			stockBase = args[1]; //
			String sd = args[2];
			if (!"-".equals(sd)){
				strStartDate = sd;
			}
			String ed = args[3];
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
			AnalyzeConf aconf = (AnalyzeConf) TaskUtil.getTaskConf(propFile);
			String params = args[4];
			AnalyzeBase.validateAllStrategyByStock(propFile, aconf, stockBase, startDate, endDate, params);
		}else{
			System.out.println(getDefaultCmdLine());
		}
	}
}
