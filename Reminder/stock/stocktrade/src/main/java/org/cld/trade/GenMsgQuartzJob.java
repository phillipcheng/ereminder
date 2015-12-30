package org.cld.trade;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.trade.evt.MarketOpenCloseTrdMsg;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class GenMsgQuartzJob implements Job {
	
	protected static Logger logger =  LogManager.getLogger(GenMsgQuartzJob.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
	
	public GenMsgQuartzJob(){
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info(String.format("triggered at: %s", sdf.format(new Date())));
		AutoTrader at = (AutoTrader) context.getMergedJobDataMap().get(AutoTrader.JDM_KEY_AT);
		String triggerName=context.getTrigger().getKey().getName();
		TradeMsg trdMsg = new MarketOpenCloseTrdMsg(triggerName);
		at.addMsg(trdMsg);
		logger.info(String.format("msg added %s", trdMsg));
	}
}
