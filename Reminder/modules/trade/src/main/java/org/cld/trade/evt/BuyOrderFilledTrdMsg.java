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
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.OrderResponse;

public class BuyOrderFilledTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(BuyOrderFilledTrdMsg.class);
	
	String buyOrderId;
	
	public BuyOrderFilledTrdMsg(){
		super(TradeMsgType.buyOrderFilled);
	}
	
	public BuyOrderFilledTrdMsg(Map<String, StockOrder> somap, String buyOrderId){
		this();
		this.setSomap(somap);
		this.buyOrderId = buyOrderId;
	}
	
	public String toString(){
		return String.format("SM:%s", this.getMsgType());
	}
	
	/**
	 * [monitor buy order]
	 * 		|__ executed. 1 sell stop trailing order submitted (GTC), 2 msg added (monitor stop trailing order, monitor price cross). <open position>
	 *      |__ cancelled, remove me
	 */
	@Override
	public TradeMsgPR process(AutoTrader at) {
		List<TradeMsg> tml = new ArrayList<TradeMsg>();
		TradeMsgPR tmpr = new TradeMsgPR();
		//submit 1 sell stop trailing order, 1 monitor order msg and 1 monitor price msg
		StockOrder selllimit = somap.get(StockOrderType.selllimit);
		StockOrder sellstop = somap.get(StockOrderType.sellstop);
		OrderResponse or = at.getTm().trySubmit(sellstop, true); //submit stop order
		if (OrderResponse.SUCCESS.equals(or.getError())){
			logger.info(String.format("sellstop order filled. %s", sellstop));
			StockPosition sp = TradePersistMgr.getStockPositionByOrderId(at.getDbConf(), buyOrderId);
			if (sp!=null){
				sp.setStopSellOrderId(or.getClientorderid());
				TradePersistMgr.updatePosition(at.getDbConf(), sp);
			}
			MonitorSellStopOrderTrdMsg msso = new MonitorSellStopOrderTrdMsg(or.getClientorderid(), somap);
			sellstop.setOrderId(or.getClientorderid());//set this client id into the stock order context
			tml.add(msso);//
			MonitorSellPriceTrdMsg msp = new MonitorSellPriceTrdMsg(sellstop.getSymbol(), selllimit.getLimitPrice(), somap);
			tml.add(msp);//
			tmpr.setExecuted(true);
			tmpr.setNewMsgs(tml);
			return tmpr;
		}else{
			//TODO error handling
			logger.error(String.format("sell stop order error: %s, response: %s", sellstop, or));
		}
		return tmpr;
	}
}
