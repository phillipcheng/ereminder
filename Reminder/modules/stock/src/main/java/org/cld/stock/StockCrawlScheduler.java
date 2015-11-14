package org.cld.stock;

import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class StockCrawlScheduler {
	protected static Logger logger =  LogManager.getLogger(StockCrawlScheduler.class);
	public static void main(String[] args){
		try{
			//Create & start the scheduler.
	        StdSchedulerFactory factory = new StdSchedulerFactory();
	        factory.initialize("quartz.properties");
	        Scheduler scheduler = factory.getScheduler();
	        //
	        JobDetail stockCrawlJob = JobBuilder.newJob(StockCrawlJob.class).
	        		withIdentity(StockCrawlJob.class.getSimpleName(), "group1").storeDurably().build();
	        Trigger nasdaqStockTrigger = TriggerBuilder.newTrigger().
	        		withIdentity("nasdaq", "group1").
	        		withSchedule(CronScheduleBuilder.cronSchedule("0 30 19 ? * 2-6").inTimeZone(TimeZone.getTimeZone("EST"))).
	        		forJob(stockCrawlJob).build();
	        
	        Trigger sinaStockTrigger = TriggerBuilder.newTrigger().
	        		withIdentity("sina", "group2").
	        		withSchedule(CronScheduleBuilder.cronSchedule("0 10 23 ? * 2-6").inTimeZone(TimeZone.getTimeZone("CTT"))).
	        		forJob(stockCrawlJob).build();
	        scheduler.addJob(stockCrawlJob, true);
	        scheduler.scheduleJob(nasdaqStockTrigger);
	        //scheduler.scheduleJob(sinaStockTrigger);
	        scheduler.start();
		}catch(Exception e){
			logger.error("", e);
		}
	}
}
