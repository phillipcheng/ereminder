package org.cld.trade.evt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.trade.AutoTrader;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.TradeMsgType;

public class MarketOpenTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MarketOpenTrdMsg.class);
	
	public MarketOpenTrdMsg() {
		super(TradeMsgType.marketOpenSoon);
	}
	

	/**
	 *  [market will open]
	 * 		|__ no position, apply alg a buy order submitted (Day), 1 msg added (monitor buy order)
	 *      |__ has position, do nothing.
	 */
	@Override
	public TradeMsgPR process(AutoTrader at) {
		TradeMsgPR tmpr = new TradeMsgPR();
		tmpr.setExecuted(true);
		return tmpr;
	}
}
