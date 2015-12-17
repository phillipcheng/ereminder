package org.cld.trade.evt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.StockOrder;
import org.cld.trade.AutoTrader;
import org.cld.trade.StockOrderType;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.TradeMsgType;
import org.cld.trade.response.OrderStatus;

public class MonitorSellStopOrderTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MonitorSellStopOrderTrdMsg.class);
	private String orderId;
	
	public MonitorSellStopOrderTrdMsg(){
		super(TradeMsgType.monitorSellStopOrder);
	}
	
	public MonitorSellStopOrderTrdMsg(String orderId, Map<String, StockOrder> somap){
		this();
		this.orderId = orderId;
		this.setSomap(somap);
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
		Map<String, OrderStatus> map = at.getTm().getOrderStatus();
		StockOrder sellstorder = getSomap().get(StockOrderType.sellstop.name());
		OrderStatus os = map.get(getOrderId());
		TradeMsgPR tmpr = new TradeMsgPR();
		if (os!=null){
			List<String> rmMsgList = new ArrayList<String>();
			if (OrderStatus.FILLED.equals(os.getStat())){
				logger.info(String.format("sell stop order filled. %s", os));
				String mpMsgId = String.format("%s", TradeMsgType.monitorSellLimitPrice);
				rmMsgList.add(mpMsgId);
				tmpr.setExecuted(true);
				tmpr.setRmMsgs(rmMsgList);
				return tmpr;
			}else if (OrderStatus.CANCELED.equals(os.getStat())){
				//do not monitor any more
				tmpr.setExecuted(true);
				return tmpr;
			}else{
				logger.info(String.format("status is %s for sell stop order %s", os.getStat(), os.getOrderId()));
			}
		}else{
			logger.info(String.format("%s not found in the recent orders.", getOrderId()));
		}
		return tmpr;
	}
}
