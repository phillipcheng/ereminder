package org.cld.stock.sina;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.etl.csv.CsvReformatMapredLauncher;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.TabularCSVConvertTask;
import org.cld.stock.ETLUtil;
import org.cld.stock.StockBase;
import org.cld.stock.StockConfig;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.stock.sina.task.GenNdLable;
import org.cld.stock.sina.task.MergeTask;
import org.cld.stock.sina.task.TradeDetailCheckDownload;
import org.cld.stock.sina.task.TradeDetailPostProcessTask;

public class SinaStockBase extends StockBase{
	private StockConfig sc = new SinaStockConfig();
	
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
	
	public void run_merge(){
		String datePart;
		String strEndDate = sdf.format(this.endDate);
		if (this.startDate==null){
			datePart = null + "_" + strEndDate;
		}else{
			datePart = sdf.format(this.startDate) + "_" + strEndDate;
		}
		MergeTask.launch(this.propFile, cconf, datePart, specialParam, true);
	}
	
	//sina-stock-market-fq
	public void splitByStock(){
		HadoopTaskLauncher.executeTasks(getCconf().getNodeConf(), null, 
				new String[]{"/reminder/items/merge/"+this.specialParam}, 3072, true, 
				"/reminder/items/mlinput/"+this.specialParam, 
				false, "org.cld.stock.sina.jobs.SplitByStockMapper", "org.cld.stock.sina.jobs.SplitByStockReducer", false);
	}
	
	public void genNdLable(){
		GenNdLable.launch(this.propFile, getCconf(), this.specialParam, true);
	}
	
	//crawl financial report history by market to hdfs
	public void run_browse_fr_history() throws ParseException{//till running time
		//set output to null, needs follow up etl
		ETLUtil.runTaskByCmd(sc, marketId, cconf, this.getPropFile(), SinaStockConfig.SINA_STOCK_FR_HISTORY, null);
	}
	//fr history convert to csv/hive
	public void run_convert_fr_history_tabular_to_csv() throws Exception {
		String[] ids = ETLUtil.getStockIdByMarketId(sc, marketId, cconf);
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<ids.length; i++){
			String sid = ids[i];
			String stockid = sid.substring(2);
			for (String subFR: SinaStockConfig.subFR){
				TabularCSVConvertTask ct = new TabularCSVConvertTask(stockid, 
						SinaStockConfig.SINA_STOCK_FR_HISTORY + "/" + subFR, 
						SinaStockConfig.SINA_STOCK_FR_HISTORY_OUT+ "/" + subFR);
				tlist.add(ct);
			}
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist, null, false);
	}
	//fr history reformat from split by stockid to split by quarter
	public void run_fr_reformat() throws Exception{
		for (String subFR: SinaStockConfig.subFR){
			CsvReformatMapredLauncher.format(this.getPropFile(), 
					"/" + SinaStockConfig.SINA_STOCK_FR_HISTORY_OUT + "/" + subFR, 
					1, 
					"/" + SinaStockConfig.SINA_STOCK_FR_HISTORY_QUARTER_OUT + "/" + subFR);
		}
	}


}
