package org.cld.trade.evt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.stock.etl.ETLUtil;
import org.cld.stock.etl.base.ETLConfig;
import org.cld.stock.etl.base.NasdaqETLConfig;
import org.cld.stock.mapper.ext.NasdaqSplitJDBCMapper;
import org.cld.stock.mapper.ext.NasdaqUpcomingDivMapper;
import org.cld.trade.AutoTrader;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.util.DateTimeUtil;
import org.cld.util.jdbc.JDBCMapper;
import org.cld.util.jdbc.SqlUtil;

public class MarketOpenCloseTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MarketOpenCloseTrdMsg.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static{
		sdf.setTimeZone(TimeZone.getTimeZone("EST"));
	}
	private MarketOpenCloseEvtType ocType;
	
	public MarketOpenCloseTrdMsg() {
		super(TradeMsgType.marketOpenClose, null, null, null);
	}
	
	public MarketOpenCloseTrdMsg(String triggerName){
		this();
		ocType = MarketOpenCloseEvtType.valueOf(triggerName);
	}
	
	@Override
	public String toString(){
		return String.format("msgId:%s, octype:%s", getMsgId(), ocType);
	}

	private static final String[] crawlCmds = new String[]{NasdaqETLConfig.ISSUE_UPCOMING_DIVIDEND, NasdaqETLConfig.ISSUE_UPCOMING_SPLIT};
	private static Map<String, JDBCMapper> mapper = new HashMap<String, JDBCMapper>();
	static{
		mapper.put(NasdaqETLConfig.ISSUE_UPCOMING_DIVIDEND, NasdaqUpcomingDivMapper.getInstance());
		mapper.put(NasdaqETLConfig.ISSUE_UPCOMING_SPLIT, NasdaqSplitJDBCMapper.getInstance());
	}
	
	public static void crawlUpdate(AutoTrader at){
		logger.info("crawlUpdate!");
		String marketId="unused";
		String startDate= sdf.format(DateTimeUtil.tomorrow(new Date()));
		String endDate = sdf.format(DateTimeUtil.getDay(new Date(),2));
		
		ETLConfig sc = ETLConfig.getETLConfig(at.getBaseMarketId());
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(AbstractCrawlItemToCSV.FN_STARTDATE, startDate);
		paramMap.put(AbstractCrawlItemToCSV.FN_ENDDATE, endDate);
		paramMap.put(AbstractCrawlItemToCSV.FN_MARKETID, marketId);
		paramMap.put(AbstractCrawlItemToCSV.FN_BASEMARKETID, at.getBaseMarketId());
		for (String cmd:crawlCmds){
			String[] csvs = ETLUtil.runTaskByCmd(sc, marketId, at.getCconf(), at.getCrawlconfProperties(), cmd, paramMap, false);
			SqlUtil.insertCsvs(at.getCconf().getSmalldbconf(), mapper.get(cmd), csvs);
		}
	}
	/**
	 *  [market will open]
	 * 		|__ 
	 *      |__
	 */
	@Override
	public TradeMsgPR process(AutoTrader at) {
		TradeMsgPR tmpr = new TradeMsgPR();
		MarketStatusType mst = AutoTrader.getMarketStatus(at);
		logger.info(String.format("market status is %s", mst));
		at.startStreamMgr(mst);
		if (ocType == MarketOpenCloseEvtType.preMarketOpen){
			//find all impacted symbols by ExDiv
			at.applySplitDiv(new Date());
		}else if (ocType==MarketOpenCloseEvtType.afterMarketClose){
			//crawl the new data
			crawlUpdate(at);
		}
		tmpr.setExecuted(true);
		return tmpr;
	}
}
