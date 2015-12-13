package org.cld.trade.evt;

import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.trade.AutoTrader;
import org.cld.trade.TradeKingConnector;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.TradeMsgType;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.util.DateTimeUtil;

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
		StockConfig sc = StockUtil.getStockConfig(at.getBaseMarketId());
		Date checkDate = StockUtil.getLastOpenDay(DateTimeUtil.yesterday(new Date()), sc.getHolidays());
		List<StockPosition> sp = TradePersistMgr.getOpenPosition(at.getCconf().getSmalldbconf(), checkDate);
		TradeMsgPR tmpr = new TradeMsgPR();
		if (sp.size()>0){
			logger.info(String.format("has opened position %s.", sp.get(0)));
			tmpr.setExecuted(true);
			return tmpr;
		}else{
			
			return tmpr;//opp not found
		}
	}
}
