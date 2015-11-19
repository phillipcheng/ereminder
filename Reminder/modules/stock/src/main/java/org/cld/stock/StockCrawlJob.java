package org.cld.stock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	public static Map<String, Boolean> runGuard = new ConcurrentHashMap<String, Boolean>();
	
	public StockCrawlJob(){
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		runGuard.put(StockCrawlScheduler.NASDAQ_MARKET_CRON_FQ, true);
		runGuard.put(StockCrawlScheduler.NASDAQ_MARKET_CRON_QH, true);
		String triggerName=context.getTrigger().getKey().getName();
		String triggerGrpName = context.getTrigger().getKey().getGroup();
		String marketId;
		StockBase sb = null;
		String propFile = "cld-stock-cluster.properties";
		if (StockUtil.NASDAQ_STOCK_BASE.equals(triggerName)){
			marketId = NasdaqStockConfig.MarketId_ALL;
			sb = new NasdaqStockBase(propFile, marketId, null, null);
		}else if (StockUtil.SINA_STOCK_BASE.equals(triggerName)){
			marketId = SinaStockConfig.MarketId_HS_A;
			sb = new SinaStockBase(propFile, marketId, null, null);
		}else{
			logger.error("unknown triggerName:" + triggerName);
			return;
		}
		
		logger.info(String.format("start to run %s...", triggerGrpName));
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(sb.getStockConfig().getTimeZone());
			String strToday = sdf.format(new Date());
			String strTomorrow = sdf.format(DateTimeUtil.tomorrow(new Date()));
			String strYesterday = sdf.format(DateTimeUtil.yesterday(new Date()));
			logger.info(String.format("today:%s, tomorrow:%s", strToday, strTomorrow));
			SimpleDateFormat localSdf = new SimpleDateFormat("yyyy-MM-dd");
			Date today = localSdf.parse(strToday);
			Date tomorrow = localSdf.parse(strTomorrow);
			Date yesterday = localSdf.parse(strYesterday);
			if (StockUtil.NASDAQ_STOCK_BASE.equals(triggerName)){
				if (triggerGrpName.equals(StockCrawlScheduler.NASDAQ_MARKET_CRON_COMMON)){//triggered at the 19PM today
					//enable all
					sb.setEndDate(tomorrow);
					String params = String.format("cmd:|%s|%s", NasdaqStockConfig.QUOTE_FQ_HISTORY, NasdaqStockConfig.QUOTE_HISTORY);
					sb.updateAll(params);
				}else if (triggerGrpName.equals(StockCrawlScheduler.NASDAQ_MARKET_CRON_FQ)){//triggered at 0AM
					sb.setEndDate(today);
					String params = String.format("cmd:+%s", NasdaqStockConfig.QUOTE_FQ_HISTORY);
					while (!sb.fqReady(yesterday) && runGuard.get(triggerGrpName)){
						Thread.sleep(20000);
					}
					Thread.sleep(20000);
					runGuard.put(StockCrawlScheduler.NASDAQ_MARKET_CRON_QH, false);//no need qh
					sb.updateAll(params);
				}else if (triggerGrpName.equals(StockCrawlScheduler.NASDAQ_MARKET_CRON_QH)){//triggered at 6AM
					if (runGuard.get(StockCrawlScheduler.NASDAQ_MARKET_CRON_QH)!=null && runGuard.get(StockCrawlScheduler.NASDAQ_MARKET_CRON_QH)==true){
						//stop fq
						runGuard.put(StockCrawlScheduler.NASDAQ_MARKET_CRON_FQ, false);
						sb.setEndDate(today);
						String params = String.format("cmd:+%s", NasdaqStockConfig.QUOTE_HISTORY);
						sb.updateAll(params);
					}
				}else if (triggerGrpName.equals(StockCrawlScheduler.NASDAQ_NONE_MARKET_CRON)){
					sb.updateAll("cmd:" + CrawlCmdGroupType.nonequote.toString());
				}else{
					logger.error(String.format("triggerGrp:%s unknown.", triggerGrpName));
				}
			}
		}catch(Throwable t){
			logger.error("", t);
		}
	}
}
