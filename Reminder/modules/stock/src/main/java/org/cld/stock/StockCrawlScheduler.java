package org.cld.stock;

import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class StockCrawlScheduler {
	protected static Logger logger =  LogManager.getLogger(StockCrawlScheduler.class);
	
	public static final String NASDAQ_TZ="nasdaq.timezone";
	public static final String NASDAQ_MARKET_CRON_COMMON="nasdaq.market.cron.common";
	public static final String NASDAQ_MARKET_CRON_FQ="nasdaq.market.cron.fq";
	public static final String NASDAQ_MARKET_CRON_QH="nasdaq.market.cron.qh";//quote history kicks in if fq failed
	public static final String NASDAQ_NONE_MARKET_CRON="nasdaq.none.market.cron";//
	
	public static final String HS_MARKET_CRON_COMMON="hs.market.cron.common";
	
	public static void main(String[] args){
		try{
			String nasdaqTZ = "EST";
			String nasdaqMarketCronCommon=null;
			String nasdaqMarketCronFq=null;
			String nasdaqMarketCronQh=null;
			String nasdaqNoneMarketCron=null;
			try{
				PropertiesConfiguration pc = new PropertiesConfiguration("crawlstock.properties");
				nasdaqTZ = pc.getString(NASDAQ_TZ);
				nasdaqMarketCronCommon = pc.getString(NASDAQ_MARKET_CRON_COMMON);
				nasdaqMarketCronFq = pc.getString(NASDAQ_MARKET_CRON_FQ);
				nasdaqMarketCronQh = pc.getString(NASDAQ_MARKET_CRON_QH);
				nasdaqNoneMarketCron = pc.getString(NASDAQ_NONE_MARKET_CRON);
			}catch(Exception e){
				logger.error("", e);
			}
			
			//Create & start the scheduler.
	        StdSchedulerFactory factory = new StdSchedulerFactory();
	        factory.initialize("quartz.properties");
	        Scheduler scheduler = factory.getScheduler();
	        //
	        JobDetail stockCrawlJob = JobBuilder.newJob(StockCrawlJob.class).
	        		withIdentity(StockCrawlJob.class.getSimpleName(), "group1").storeDurably().build();
	        
	        Trigger nasdaqTradingDayCommonCrawlTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(StockUtil.NASDAQ_STOCK_BASE, NASDAQ_MARKET_CRON_COMMON).
	        		withSchedule(CronScheduleBuilder.cronSchedule(nasdaqMarketCronCommon).inTimeZone(TimeZone.getTimeZone(nasdaqTZ))).
	        		forJob(stockCrawlJob).build();
	        CronExpression ce = new CronExpression(nasdaqMarketCronCommon);
	        ce.setTimeZone(TimeZone.getTimeZone(nasdaqTZ));
	        Date nextValid = ce.getNextValidTimeAfter(new Date());
	        logger.info("nasdaqMarketCronCommon:" + nasdaqMarketCronCommon);
	        logger.info("timezone:" + nasdaqTZ);
	        logger.info("now:" + new Date());
	        logger.info("next valid time:" + nextValid);
	        Trigger nasdaqTradingDayFqCrawlTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(StockUtil.NASDAQ_STOCK_BASE, NASDAQ_MARKET_CRON_FQ).
	        		withSchedule(CronScheduleBuilder.cronSchedule(nasdaqMarketCronFq).inTimeZone(TimeZone.getTimeZone(nasdaqTZ))).
	        		forJob(stockCrawlJob).build();
	        Trigger nasdaqTradingDayQhCrawlTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(StockUtil.NASDAQ_STOCK_BASE, NASDAQ_MARKET_CRON_QH).
	        		withSchedule(CronScheduleBuilder.cronSchedule(nasdaqMarketCronQh).inTimeZone(TimeZone.getTimeZone(nasdaqTZ))).
	        		forJob(stockCrawlJob).build();
	        Trigger nasdaqNonTradingDayCrawlTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(StockUtil.NASDAQ_STOCK_BASE, NASDAQ_NONE_MARKET_CRON).
	        		withSchedule(CronScheduleBuilder.cronSchedule(nasdaqNoneMarketCron).inTimeZone(TimeZone.getTimeZone(nasdaqTZ))).
	        		forJob(stockCrawlJob).build();
	        
	        Trigger sinaStockTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(StockUtil.SINA_STOCK_BASE, HS_MARKET_CRON_COMMON).
	        		withSchedule(CronScheduleBuilder.cronSchedule("0 10 23 ? * 2-6").inTimeZone(TimeZone.getTimeZone("CTT"))).
	        		forJob(stockCrawlJob).build();
	        scheduler.addJob(stockCrawlJob, true);
	        scheduler.scheduleJob(nasdaqTradingDayCommonCrawlTrigger);
	        scheduler.scheduleJob(nasdaqTradingDayFqCrawlTrigger);
	        scheduler.scheduleJob(nasdaqTradingDayQhCrawlTrigger);
	        scheduler.scheduleJob(nasdaqNonTradingDayCrawlTrigger);
	        //scheduler.scheduleJob(sinaStockTrigger);
	        scheduler.start();
		}catch(Exception e){
			logger.error("", e);
		}
	}
}
