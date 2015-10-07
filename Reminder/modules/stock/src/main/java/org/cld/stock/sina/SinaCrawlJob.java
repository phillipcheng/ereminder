package org.cld.stock.sina;

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockBase;
import org.cld.util.DateTimeUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SinaCrawlJob implements Job {
	
	protected static Logger logger =  LogManager.getLogger(SinaCrawlJob.class);
	
	public SinaCrawlJob(){
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String propFile = "cld-stock-cluster.properties";
		String marketId = SinaStockConfig.MarketId_HS_A;
		Date now = new Date();
		StockBase nsb = new SinaStockBase(propFile, marketId, null, now);
		try{
			logger.info("start to run ..." + nsb);
			nsb.updateAll(null);
		}catch(Throwable t){
			logger.error("", t);
		}
	}
}
