package org.cld.trade.evt;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.trade.AutoTrader;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;

public class MarketOpenCloseTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MarketOpenCloseTrdMsg.class);
	
	private MarketOpenCloseEvtType ocType;
	
	public MarketOpenCloseTrdMsg() {
		super(TradeMsgType.marketOpenClose, null, null, null);
	}
	
	public MarketOpenCloseTrdMsg(String triggerName){
		this();
		ocType = MarketOpenCloseEvtType.valueOf(triggerName);
	}
	

	/**
	 *  [market will open]
	 * 		|__ 
	 *      |__
	 */
	@Override
	public TradeMsgPR process(AutoTrader at) {
		TradeMsgPR tmpr = new TradeMsgPR();
		MarketStatusType mst = AutoTrader.getMarketStatus();
		at.startStreamMgr(mst);
		if (ocType == MarketOpenCloseEvtType.preMarketOpen){
			//find all impacted symbols by ExDiv
			at.applySplitDiv(new Date());
		}
		tmpr.setExecuted(true);
		return tmpr;
	}
}
