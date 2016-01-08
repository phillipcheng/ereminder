package org.cld.trade.evt;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.OrderFilled;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.StockOrder;
import org.cld.trade.AutoTrader;
import org.cld.trade.TradeKingConnector;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.response.OrderStatus;

public class MonitorSellLimitOrderTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MonitorSellLimitOrderTrdMsg.class);
	private String orderId;
	
	public MonitorSellLimitOrderTrdMsg(String orderId, SelectCandidateResult scr, String bsName, Map<String, StockOrder> somap){
		super(TradeMsgType.monitorSellLimitOrder, scr, bsName, somap);
		this.orderId = orderId;
	}
	
	public String toString(){
		return String.format("SM:%s,%s", this.getMsgType(), orderId);
	}

	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	/**
	 * 	[monitor sell limit order]
	 * 		|__ executed. notify strategy. <close position>
	 *      |__ cancelled.
	 */
	@Override
	public TradeMsgPR process(AutoTrader at) {
		OrderStatus os = at.getTm().getTheOrderStatus(getOrderId());
		TradeMsgPR tmpr = new TradeMsgPR();
		if (os!=null){
			logger.debug(String.format("os got from monitor limit sell order:%s", os.toString()));
			OrderFilled of = TradeKingConnector.toOrderFilled(os);
			SelectStrategy bs = at.getBs(scr.getSymbol(), bsName);
			if (bs!=null){
				if (OrderStatus.FILLED.equals(os.getStat())){
					bs.tradeCompleted(of, true);
					logger.info(String.format("sell limit order filled. %s", os));
					tmpr.setExecuted(true);
					return tmpr;
				}else if (OrderStatus.CANCELED.equals(os.getStat())){
					bs.tradeCompleted(of, false);
					logger.info(String.format("sell limit order cancelled %s", os.getOrderId()));
					//do not monitor any more
					//try to cancel the monitor sell price msg
					tmpr.setExecuted(true);
					return tmpr;
				}else if (OrderStatus.OPEN.equals(os.getStat())){
					logger.debug(String.format("sell limit order %s in open state.", os.getOrderId()));
				}else{
					logger.info(String.format("status is %s for sell limit order %s", os.getStat(), os.getOrderId()));
				}
			}else{
				logger.error(String.format("SYSTEM error, bs can't be found for name:%s", bsName));
			}
		}else{
			logger.info(String.format("%s not found in the recent orders.", getOrderId()));
		}
		return tmpr;
	}
}
