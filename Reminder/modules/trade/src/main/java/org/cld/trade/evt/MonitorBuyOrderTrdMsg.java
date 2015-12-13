package org.cld.trade.evt;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.trade.StockOrder;
import org.cld.trade.AutoTrader;
import org.cld.trade.StockOrderType;
import org.cld.trade.TradeKingConnector;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.TradeMsgType;
import org.cld.trade.response.OrderStatus;

public class MonitorBuyOrderTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MonitorBuyOrderTrdMsg.class);
	private StockOrderType orderType;
	private String orderId;
	
	public MonitorBuyOrderTrdMsg(){
		super(TradeMsgType.monitorBuyLimitOrder);
	}
	
	public MonitorBuyOrderTrdMsg(StockOrderType orderType, String orderId, Map<StockOrderType, StockOrder> somap){
		this();
		this.orderType = orderType;
		this.orderId = orderId;
		this.setSomap(somap);
	}
	
	public String toString(){
		return String.format("SM:%s,%s,%s", this.getMsgType(), orderType, orderId);
	}


	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public StockOrderType getOrderType() {
		return orderType;
	}
	public void setOrderType(StockOrderType orderType) {
		this.orderType = orderType;
	}
	
	
	/**
	 * [monitor buy order]
	 * 		|__ executed. 1 sell stop trailing order submitted (GTC), 2 msg added (monitor stop trailing order, monitor price cross). <open position>
	 *      |__ cancelled, remove me
	 */
	@Override
	public TradeMsgPR process(AutoTrader at) {
		Map<String, OrderStatus> map = at.getTm().getOrderStatus();
		OrderStatus os = map.get(getOrderId());
		TradeMsgPR tmpr = new TradeMsgPR();
		if (os!=null){
			if (OrderStatus.FILLED.equals(os.getStat())){
				tmpr = BuyOrderFilledTrdMsg.process(at, this.getSomap());
			}else if (OrderStatus.CANCELED.equals(os.getStat())){
				logger.info(String.format("order %s cancelled", os));
				tmpr.setExecuted(true);
				return tmpr;
			}else{
				logger.info(String.format("status is %s for buy order %s", os.getStat(), os.getOrderId()));
			}
		}else{
			logger.info(String.format("%s not found in the recent orders.", getOrderId()));
		}
		return tmpr;
	}
}
