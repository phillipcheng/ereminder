package org.cld.stock.analyze;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.SafeSimpleDateFormat;

public class RunAnalyze {
	protected static Logger logger =  LogManager.getLogger(RunAnalyze.class);
	private static SafeSimpleDateFormat sdf = new SafeSimpleDateFormat("yyyy-MM-dd");

	//x.properties baseMarketId startDate endDate sn:strategyName1_strategyName2,step:1,th:normal
	public static String getDefaultCmdLine(){
		return "propFile baseMarketId startDate endDate 'sn:strategyName,step:,th:normal'";
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
			String params = args[4];
			AnalyzeBase.validateStrategies(propFile, stockBase, strStartDate, strEndDate, params);
		}else{
			System.out.println(getDefaultCmdLine());
		}
	}
}
