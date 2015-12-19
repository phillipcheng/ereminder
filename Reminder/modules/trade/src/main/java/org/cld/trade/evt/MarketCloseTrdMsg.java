package org.cld.trade.evt;

import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.trade.AutoTrader;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.TradeMsgType;

public class MarketCloseTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MarketCloseTrdMsg.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public MarketCloseTrdMsg() {
		super(TradeMsgType.marketCloseSoon, null, null, null);
	}

	/**
	 *  [market will close]
	 * 		|__ has position: cancel stop order, submit sell on close order, clean msgs
	 *      |__ no position, do nothing
	 */
	@Override
	public TradeMsgPR process(AutoTrader at) {
		TradeMsgPR tmpr = new TradeMsgPR();
		tmpr.setExecuted(true);
		return tmpr;
	}
}
