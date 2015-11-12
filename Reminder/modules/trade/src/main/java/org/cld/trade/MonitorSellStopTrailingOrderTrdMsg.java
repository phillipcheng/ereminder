package org.cld.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.trade.StockOrder;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.OrderStatus;

public class MonitorSellStopTrailingOrderTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MonitorSellStopTrailingOrderTrdMsg.class);
	private String orderId;
	
	public MonitorSellStopTrailingOrderTrdMsg(){
		super(TradeMsgType.monitorSellStopTrailingOrder);
	}
	
	public MonitorSellStopTrailingOrderTrdMsg(String orderId, Map<StockOrderType, StockOrder> somap){
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
	public TradeMsgPR process(TradeMgr tm) {
		Map<String, OrderStatus> map = tm.getOrderStatus();
		StockOrder sellstorder = getSomap().get(StockOrderType.sellstoptrail);
		OrderStatus os = map.get(getOrderId());
		TradeMsgPR tmpr = new TradeMsgPR();
		if (os!=null){
			List<String> rmMsgList = new ArrayList<String>();
			if (OrderStatus.FILLED.equals(os.getStat())){
				logger.info(String.format("sell stop trailing order filled. %s", os));
				StockPosition trySp = new StockPosition(sellstorder, StockPosition.close, sellstorder.getOrderId());
				TradePersistMgr.closePosition(tm.getCconf().getSmalldbconf(), trySp);//
				String mpMsgId = String.format("%s", TradeMsgType.monitorSellLimitPrice);
				rmMsgList.add(mpMsgId);
				tmpr.setExecuted(true);
				tmpr.setRmMsgs(rmMsgList);
				return tmpr;
			}else{
				logger.info(String.format("status is %s for sell stop trailing order %s", os.getStat(), os.getOrderId()));
			}
		}else{
			logger.info(String.format("%s not found in the recent orders.", getOrderId()));
		}
		return tmpr;
	}
}
