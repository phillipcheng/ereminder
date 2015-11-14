package org.cld.stock;

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.nasdaq.NasdaqStockBase;
import org.cld.stock.nasdaq.NasdaqStockConfig;
import org.cld.stock.sina.SinaStockBase;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.util.DateTimeUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class StockCrawlJob implements Job {
	
	protected static Logger logger =  LogManager.getLogger(StockCrawlJob.class);
	
	public StockCrawlJob(){
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String triggerName=context.getTrigger().getKey().getName();
		String marketId;
		StockBase sb = null;
		String propFile = "cld-stock-cluster.properties";
		if ("nasdaq".equals(triggerName)){
			marketId = NasdaqStockConfig.MarketId_ALL;
			sb = new NasdaqStockBase(propFile, marketId, null, null);
		}else if ("sina".equals(triggerName)){
			marketId = SinaStockConfig.MarketId_HS_A;
			sb = new SinaStockBase(propFile, marketId, null, null);
		}else{
			logger.error("unknown triggerName:" + triggerName);
			return;
		}
		Date tomorrowDate = DateTimeUtil.tomorrow(new Date());
		tomorrowDate = DateTimeUtil.convertDateToServerTZFromTZ(tomorrowDate, sb.getStockConfig().getTimeZone());
		sb.setEndDate(tomorrowDate);
		Date todayDate = DateTimeUtil.yesterday(tomorrowDate);
		try{
			logger.info("start to run ..." + sb);
			while (!sb.readyToCrawl(todayDate)){
				Thread.sleep(20000);
			}
			Thread.sleep(20000);
			logger.info("start to update All" + sb);
			sb.updateAll(null);
		}catch(Throwable t){
			logger.error("", t);
		}
	}
}
