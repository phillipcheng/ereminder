package org.cld.trade.evt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.trade.StockOrder;
import org.cld.trade.AutoTrader;
import org.cld.trade.StockOrderType;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.TradeMsgType;
import org.cld.trade.response.OrderStatus;

public class MonitorBuyOrderTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MonitorBuyOrderTrdMsg.class);
	private String orderId;
	
	public MonitorBuyOrderTrdMsg(){
		super(TradeMsgType.monitorBuyLimitOrder);
	}
	
	public MonitorBuyOrderTrdMsg(String buyOrderId, Map<StockOrderType, StockOrder> somap){
		this();
		this.orderId = buyOrderId;
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
	 * [monitor buy order]
	 * 		|__ executed. 1 buy order filled msg generated
	 *      |__ cancelled, remove me
	 */
	@Override
	public TradeMsgPR process(AutoTrader at) {
		Map<String, OrderStatus> map = at.getTm().getOrderStatus();
		OrderStatus os = map.get(getOrderId());
		TradeMsgPR tmpr = new TradeMsgPR();
		List<TradeMsg> tml = new ArrayList<TradeMsg>();
		if (os!=null){
			if (OrderStatus.FILLED.equals(os.getStat())){
				BuyOrderFilledTrdMsg bof = new BuyOrderFilledTrdMsg(this.getSomap(), orderId);
				tml.add(bof);
				tmpr.setNewMsgs(tml);
				tmpr.setExecuted(true);
				return tmpr;
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
