package org.cld.trade.evt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.OrderFilled;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.StockOrder;
import org.cld.stock.strategy.TradeStrategy;
import org.cld.trade.AutoTrader;
import org.cld.trade.StockOrderType;
import org.cld.trade.TradeKingConnector;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.TradeMsgType;
import org.cld.trade.response.OrderStatus;

public class MonitorSellStopOrderTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MonitorSellStopOrderTrdMsg.class);
	private String orderId;
	
	public MonitorSellStopOrderTrdMsg(String orderId, SelectCandidateResult scr, String bsName, Map<String, StockOrder> somap){
		super(TradeMsgType.monitorSellStopOrder, scr, bsName, somap);
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
	 * 	[monitor stop trailing order]
	 * 		|__ executed. remove monitor price msg. <close position>
	 *      |__ cancelled.
	 */
	@Override
	public TradeMsgPR process(AutoTrader at) {
		OrderStatus os = at.getTm().getTheOrderStatus(getOrderId());
		logger.debug(String.format("os got from monitor limit sell order:%s", os.toString()));
		TradeMsgPR tmpr = new TradeMsgPR();
		if (os!=null){
			List<String> rmMsgList = new ArrayList<String>();
			if (OrderStatus.FILLED.equals(os.getStat())){
				//callback
				OrderFilled of = TradeKingConnector.toOrderFilled(os);
				SelectStrategy bs = at.getBs(scr.getSymbol(), bsName);
				if (bs!=null){
					bs.tradeCompleted(of);
				}
				logger.info(String.format("sell stop order filled. %s", os));
				StockOrder limitSellOrder = somap.get(StockOrderType.selllimit.name());
				MonitorSellPriceTrdMsg mspMsg = new MonitorSellPriceTrdMsg(limitSellOrder.getSymbol(), limitSellOrder.getLimitPrice(), scr, bsName, somap);
				rmMsgList.add(mspMsg.getMsgId());
				tmpr.setExecuted(true);
				tmpr.setRmMsgs(rmMsgList);
				return tmpr;
			}else if (OrderStatus.CANCELED.equals(os.getStat())){
				logger.info(String.format("sell stop order cancelled %s", os.getOrderId()));
				//do not monitor any more
				//try to cancel the monitor sell price msg
				tmpr.setExecuted(true);
				return tmpr;
			}else if (OrderStatus.OPEN.equals(os.getStat())){
				logger.debug(String.format("sell stop order %s in open state.", os.getOrderId()));
			}else{
				logger.info(String.format("status is %s for sell stop order %s", os.getStat(), os.getOrderId()));
			}
		}else{
			logger.info(String.format("%s not found in the recent orders.", getOrderId()));
		}
		return tmpr;
	}
}
