package org.cld.stock;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.nasdaq.NasdaqStockBase;
import org.cld.stock.sina.SinaStockBase;
import org.cld.util.StringUtil;

public class RunStock {
	protected static Logger logger =  LogManager.getLogger(RunStock.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String SINA_STOCK_BASE="sina";
	public static final String NASDAQ_STOCK_BASE="nasdaq";
	
	//x.properties nasdaq NASDAQ_2015-09-09 run_cmd - - nasdaq-quote-tick
	//x.properties sina hs_a run_special - - genNdLable xxx,x:xx,xx
	//x.properties nasdaq NYSE run_all_cmd - 2015-09-10
	//x.properties nasdaq NASDAQ_2015-09-09 run_cmd - - nasdaq-quote-tick
	public static String getDefaultCmdLine(){
		return "propFile stock_base marketId cmd startDate endDate method params";
	}
	
	public static void main(String[] args) {
		String propFile="";
		String stockBase = "";
		String marketId ="";
		String cmd="";
		String strStartDate = null;
		String strEndDate = null;
		if (args.length>=6){
			propFile = args[0]; //
			stockBase = args[1]; //
			marketId = args[2]; // 
			cmd = args[3];  //
			String sd = args[4];
			if (!"-".equals(sd)){
				strStartDate = sd;
			}
			String ed = args[5];
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
			StockBase sb = null;
			if (SINA_STOCK_BASE.equals(stockBase)){
				sb = new SinaStockBase(propFile, marketId, startDate, endDate);
			}else{
				sb = new NasdaqStockBase(propFile, marketId, startDate, endDate);
			}
			
			int argIdx = 6;
			if ("run_task".equals(cmd)){//run already generated task
				if (args.length>=argIdx+1){
					String taskName = args[argIdx];
					String[] taskNames = taskName.split(",");
					Map<String, String> hadoopParams = new HashMap<String, String>();
					if (args.length>=argIdx+2){
						String params = args[argIdx+1];//mapreduce.map.memory.mb:3072,mapreduce.map.java.opts:-Xmx3072M
						hadoopParams = StringUtil.parseMapParams(params);
					}
					sb.run_task(taskNames, hadoopParams);
				}else{
					System.out.println(getDefaultCmdLine() + " taskName");
				}
			}else if ("run_all_cmd".equals(cmd)){
				try {
					sb.runAllCmd(startDate, endDate);
				} catch (InterruptedException e) {
					logger.error("", e);
				}
			}else if ("run_cmd".equals(cmd)){
				if (args.length>=argIdx+1){
					String cmdName = args[argIdx];
					sb.runCmd(cmdName, marketId, strStartDate, strEndDate);
				}else{
					System.out.println(getDefaultCmdLine() + " cmdName");
				}
			}else if ("run_special".equals(cmd)){
				//sample usage for run_special - - genNdLable /reminder/items/mlinput/sina-stock-market-fq,1,0:1:8,2
				sb.setMarketId(marketId);
				if (args.length>=argIdx+1){
					String method = args[argIdx];
					if (args.length>=argIdx+2){
						String specialParam = args[argIdx+1];
						sb.setSpecialParam(specialParam);
					}
					Method m;
					try {
						m = sb.getClass().getMethod(method);
						m.invoke(sb);
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
