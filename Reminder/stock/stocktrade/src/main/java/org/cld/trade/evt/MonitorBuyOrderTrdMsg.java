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
import org.cld.trade.AutoTrader;
import org.cld.trade.TradeKingConnector;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.response.OrderStatus;

public class MonitorBuyOrderTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MonitorBuyOrderTrdMsg.class);
	private String orderId;
	
	public MonitorBuyOrderTrdMsg(String buyOrderId, SelectCandidateResult scr, String bsName, Map<String, StockOrder> somap){
		super(TradeMsgType.monitorBuyLimitOrder, scr, bsName, somap);
		this.orderId = buyOrderId;
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
		OrderStatus os = at.getTm().getTheOrderStatus(getOrderId());
		TradeMsgPR tmpr = new TradeMsgPR();
		List<TradeMsg> tml = new ArrayList<TradeMsg>();
		if (os!=null){
			OrderFilled of = TradeKingConnector.toOrderFilled(os);
			SelectStrategy bs = at.getBs(scr.getSymbol(), bsName);
			if (bs!=null){
				if (OrderStatus.FILLED.equals(os.getStat())){
					//callback
					bs.tradeCompleted(of, true);
					BuyOrderFilledTrdMsg bof = new BuyOrderFilledTrdMsg(orderId, this.scr, bsName, this.getSomap());
					tml.add(bof);
					tmpr.setNewMsgs(tml);
					tmpr.setExecuted(true);
					return tmpr;
				}else if (OrderStatus.CANCELED.equals(os.getStat())){
					logger.info(String.format("order %s cancelled", os));
					bs.tradeCompleted(of, false);
					tmpr.setExecuted(true);
					return tmpr;
				}else{
					logger.info(String.format("status is %s for buy order %s", os.getStat(), os.getOrderId()));
				}
			}else{
				logger.error(String.format("SYSTEM error, bs not found for name %s", bsName));
			}
		}else{
			logger.info(String.format("%s not found in the recent orders.", getOrderId()));
		}
		return tmpr;
	}
}
