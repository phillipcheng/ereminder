package org.cld.stock.nasdaq;

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockBase;
import org.cld.util.DateTimeUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class NasdaqCrawlJob implements Job {
	
	protected static Logger logger =  LogManager.getLogger(NasdaqCrawlJob.class);
	
	public NasdaqCrawlJob(){
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String propFile = "cld-stock-cluster.properties";
		String marketId = NasdaqStockConfig.MarketId_ALL;
		Date d = DateTimeUtil.tomorrow(new Date());
		StockBase nsb = new NasdaqStockBase(propFile, marketId, null, d);
		try{
			logger.info("start to run ..." + nsb);
			nsb.updateAll(null);
		}catch(Throwable t){
			logger.error("", t);
		}
	}
}
