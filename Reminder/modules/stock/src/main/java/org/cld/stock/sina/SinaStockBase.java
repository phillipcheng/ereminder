package org.cld.stock.sina;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockBase;
import org.cld.stock.StockConfig;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.stock.sina.task.TradeDetailCheckDownload;
import org.cld.stock.sina.task.TradeDetailPostProcessTask;
import org.cld.stock.task.GenNdLable;

public class SinaStockBase extends StockBase{
	protected static Logger logger =  LogManager.getLogger(SinaStockBase.class);
	private static StockConfig sc = new SinaStockConfig();
	
	@Override
	public StockConfig getStockConfig() {
		return sc;
	}
	
	public SinaStockBase(String propFile, String marketId, Date sd, Date ed){
		super(propFile, marketId, sd, ed);
	}

	public String[] run_tradedetail_checkdownload(){
		String datePart;
		String strEndDate = sdf.format(this.endDate);
		if (this.startDate==null){
			datePart = SinaStockConfig.HS_A_FIRST_DATE_DETAIL_TRADE + "_" + strEndDate;
		}else{
			datePart = sdf.format(this.startDate) + "_" + strEndDate;
		}
		return new String[]{TradeDetailCheckDownload.launch(cconf, datePart)};
	}
	
	//sina-stock-market-fq
	public String[] splitByStock(){
		Map<String, String> hadoopParams = new HashMap<String, String>();
		HadoopTaskLauncher.updateHadoopMemParams(3072, hadoopParams);
		return new String[] {
				HadoopTaskLauncher.executeTasks(getCconf().getNodeConf(), hadoopParams, 
						new String[]{"/reminder/items/merge/"+this.specialParam}, 
						true, "/reminder/items/mlinput/"+this.specialParam, false, 
						"org.cld.stock.sina.jobs.SplitByStockMapper", 
						"org.cld.stock.sina.jobs.SplitByStockReducer", false)};
	}
	
	public String[] genNdLable(){
		return GenNdLable.launch(this.propFile, getCconf(), this.specialParam, true);
	}
}
