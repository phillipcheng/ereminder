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
		Date d = DateTimeUtil.tomorrow(new Date());
		//convert to specific market timezone string representation, then to server timezone date
		StockBase nsb = new SinaStockBase(propFile, marketId, null, d);
		d = DateTimeUtil.convertDateToServerTZFromTZ(d, nsb.getStockConfig().getTimeZone());
		nsb.setEndDate(d);
		try{
			logger.info("start to run ..." + nsb);
			nsb.updateAll(null);
		}catch(Throwable t){
			logger.error("", t);
		}
	}
}
