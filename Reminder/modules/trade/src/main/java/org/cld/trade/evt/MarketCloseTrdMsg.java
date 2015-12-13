package org.cld.trade.evt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.trade.StockOrder;
import org.cld.stock.trade.StockOrder.ActionType;
import org.cld.trade.AutoTrader;
import org.cld.trade.TradeKingConnector;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.TradeMsgType;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.OrderResponse;
import org.cld.trade.response.OrderStatus;

public class MarketCloseTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MarketCloseTrdMsg.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public MarketCloseTrdMsg() {
		super(TradeMsgType.marketCloseSoon);
	}

	/**
	 *  [market will close]
	 * 		|__ has position: cancel stop order, submit sell on close order, clean msgs
	 *      |__ no position, do nothing
	 */
	@Override
	public TradeMsgPR process(AutoTrader at) {
		Date dt= new Date();
		List<StockPosition> lp = TradePersistMgr.getOpenPosition(at.getCconf().getSmalldbconf(), dt);
		TradeMsgPR tmpr = new TradeMsgPR();
		if (lp.size()==1){
			StockPosition sp = lp.get(0);
			Map<String, OrderStatus> map = at.getTm().getOrderStatus();
			if (map.containsKey(sp.getOrderId())){
				OrderResponse or = at.getTm().cancelOrder(sp.getOrderId(), ActionType.sell, sp.getSymbol(), sp.getOrderQty());
				if (OrderResponse.SUCCESS.equals(or.getError())){
					//submit sell on close order
					StockOrder so = SellStrategy.makeForceSellOrder(sp.getSymbol(), sp.getOrderQty());
					OrderResponse sor = at.getTm().trySubmit(so, true);
					if (OrderResponse.SUCCESS.equals(sor.getError())){
						logger.info(String.format("submitted sell at market close: %s", so));
					}else{
						logger.error(String.format("submit sell market on close order failed. %s", sor));
					}
				}else{
					logger.error(String.format("cancel order %s failed", sp.getOrderId()));
				}
			}else{
				logger.error(String.format("order %s to cancel not found.", sp.getOrderId()));
			}
		}else if (lp.size()>1){
			logger.warn("more than 1 open position.");
		}else{
			logger.info("no open position.");
		}
		tmpr.setExecuted(true);
		tmpr.setCleanAllMsgs(true);
		return tmpr;
	}
}
