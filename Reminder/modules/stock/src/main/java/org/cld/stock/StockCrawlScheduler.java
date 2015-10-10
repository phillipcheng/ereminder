package org.cld.stock;

import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.nasdaq.NasdaqCrawlJob;
import org.cld.stock.sina.SinaCrawlJob;
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
	        JobDetail nasdaqStockCrawl = JobBuilder.newJob(NasdaqCrawlJob.class).withIdentity(NasdaqCrawlJob.class.getSimpleName(), "group1").build();
	        Trigger nasdaqStockTrigger = TriggerBuilder.newTrigger().
	        		withIdentity("trigger1", "group1").
	        		withSchedule(CronScheduleBuilder.cronSchedule("0 10 17 ? * 2-6").inTimeZone(TimeZone.getTimeZone("PST"))).
	        		build();
	        scheduler.scheduleJob(nasdaqStockCrawl, nasdaqStockTrigger);
	        
	        JobDetail sinaStockCrawl = JobBuilder.newJob(SinaCrawlJob.class).withIdentity(SinaCrawlJob.class.getSimpleName(), "group2").build();
	        Trigger sinaStockTrigger = TriggerBuilder.newTrigger().
	        		withIdentity("trigger2", "group2").
	        		withSchedule(CronScheduleBuilder.cronSchedule("0 10 15 ? * 2-6").inTimeZone(TimeZone.getTimeZone("CTT"))).
	        		build();
	        scheduler.scheduleJob(sinaStockCrawl, sinaStockTrigger);
	        
	        scheduler.start();
		}catch(Exception e){
			logger.error("", e);
		}
	}
}
