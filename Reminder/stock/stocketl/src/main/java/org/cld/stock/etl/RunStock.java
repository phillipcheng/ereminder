package org.cld.stock.etl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.StockUtil;
import org.cld.stock.etl.base.HKStockBase;
import org.cld.stock.etl.base.NasdaqStockBase;
import org.cld.stock.etl.base.SinaStockBase;

public class RunStock {
	protected static Logger logger =  LogManager.getLogger(RunStock.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//x.properties nasdaq ALL run_all_cmd - 2015-09-10
	//x.properties nasdaq ALL_2015-09-09 run_cmd - - nasdaq-quote-tick
	//x.properties nasdaq ALL run_special - - run_merge
	//x.properties sina hs_a run_special - - genNdLable x:xvalue,y:yvalue
	//x.properties sina hs_a run_special 2015-08-29 2015-09-22 run_merge
	//x.properties sina hs_a run_special 2015-08-29 2015-09-22 
	public static String getDefaultCmdLine(){
		StringBuffer sb = new StringBuffer("for updateAll:\n");
		for (int i=0; i<StockBase.cmds.length; i++){
			sb.append(i).append(":").append(StockBase.cmds[i]).append("\n");
		}
		return "propFile stock_base marketId cmd startDate endDate method params" + sb.toString();
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
			if (StockUtil.SINA_STOCK_BASE.equals(stockBase)){
				sb = new SinaStockBase(propFile, marketId, startDate, endDate);
			}else if (StockUtil.NASDAQ_STOCK_BASE.equals(stockBase)){
				sb = new NasdaqStockBase(propFile, marketId, startDate, endDate);
			}else if (StockUtil.HK_STOCK_BASE.equals(stockBase)){
				sb = new HKStockBase(propFile, marketId, startDate, endDate);
			}else{
				logger.error(String.format("stockBase %s not supported.", stockBase));
				return;
			}
			
			int argIdx = 6;
			if ("run_all_cmd".equals(cmd)){
				try {
					sb.runAllCmd(null);
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
					String specialParam = null;
					if (args.length>=argIdx+2){
						specialParam = args[argIdx+1];
					}
					sb.runSpecial(method, specialParam);
				}else{
					System.out.println(getDefaultCmdLine());
				}
			}else{
				logger.error("unknown command." + getDefaultCmdLine());
			}
		}else{
			System.out.println(getDefaultCmdLine());
		}
	}
}
