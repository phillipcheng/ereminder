package org.cld.stock.sina;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.stock.StockBase;
import org.cld.stock.StockConfig;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.stock.sina.task.TradeDetailCheckDownload;
import org.cld.stock.sina.task.TradeDetailPostProcessTask;
import org.cld.stock.task.GenNdLable;

public class SinaStockBase extends StockBase{
	private static StockConfig sc = new SinaStockConfig();
	
	@Override
	public StockConfig getStockConfig() {
		return sc;
	}
	
	public SinaStockBase(String propFile, String marketId, Date sd, Date ed){
		super(propFile, marketId, sd, ed);
	}

	public void run_tradedetail_checkdownload(){
		String datePart;
		String strEndDate = sdf.format(this.endDate);
		if (this.startDate==null){
			datePart = SinaStockConfig.HS_A_FIRST_DATE_DETAIL_TRADE + "_" + strEndDate;
		}else{
			datePart = sdf.format(this.startDate) + "_" + strEndDate;
		}
		TradeDetailCheckDownload.launch(cconf, datePart);
	}
	
	public void run_tradedetail_postprocess(){
		String datePart;
		String strEndDate = sdf.format(this.endDate);
		if (this.startDate==null){
			datePart = SinaStockConfig.HS_A_FIRST_DATE_DETAIL_TRADE + "_" + strEndDate;
		}else{
			datePart = sdf.format(this.startDate) + "_" + strEndDate;
		}
		TradeDetailPostProcessTask.launch(this.propFile, cconf, datePart);
	}
	
	//sina-stock-market-fq
	public void splitByStock(){
		Map<String, String> hadoopParams = new HashMap<String, String>();
		HadoopTaskLauncher.updateHadoopParams(3072, hadoopParams);
		HadoopTaskLauncher.executeTasks(getCconf().getNodeConf(), hadoopParams, 
				new String[]{"/reminder/items/merge/"+this.specialParam}, true, 
				"/reminder/items/mlinput/"+this.specialParam, 
				false, "org.cld.stock.sina.jobs.SplitByStockMapper", "org.cld.stock.sina.jobs.SplitByStockReducer", false);
	}
	
	public void genNdLable(){
		GenNdLable.launch(this.propFile, getCconf(), this.specialParam, true);
	}
}
