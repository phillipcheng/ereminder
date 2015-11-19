package org.cld.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.trade.StockOrder;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.OrderResponse;

public class BuyOrderFilledTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(BuyOrderFilledTrdMsg.class);
	
	
	public BuyOrderFilledTrdMsg(){
		super(TradeMsgType.buyOrderFilled);
	}
	
	public BuyOrderFilledTrdMsg(Map<StockOrderType, StockOrder> somap){
		this();
		this.setSomap(somap);
	}
	
	public String toString(){
		return String.format("SM:%s", this.getMsgType());
	}
	
	public static TradeMsgPR process(TradeMgr tm, Map<StockOrderType, StockOrder> somap){
		List<TradeMsg> tml = new ArrayList<TradeMsg>();
		TradeMsgPR tmpr = new TradeMsgPR();
		//submit 1 sell stop trailing order, 1 monitor order msg and 1 monitor price msg
		StockOrder selllimit = somap.get(StockOrderType.selllimit);
		StockOrder sellstop = somap.get(StockOrderType.sellstop);
		OrderResponse or = AutoTrader.trySubmit(tm, sellstop, true); //submit stop order
		if (OrderResponse.SUCCESS.equals(or.getError())){
			logger.info(String.format("sellstop order filled. %s", sellstop));
			MonitorSellStopOrderTrdMsg mbo = new MonitorSellStopOrderTrdMsg(or.getClientorderid(), somap);
			StockPosition trySp = new StockPosition(sellstop, StockPosition.open, or.getClientorderid());
			sellstop.setOrderId(or.getClientorderid());//set this client id into the stock order context
			TradePersistMgr.openPosition(tm.getCconf().getSmalldbconf(), trySp);//
			tml.add(mbo);//
			MonitorSellPriceTrdMsg mp = new MonitorSellPriceTrdMsg(sellstop.getStockid(), selllimit.getLimitPrice(), somap);
			tml.add(mp);//
			tmpr.setExecuted(true);
			tmpr.setNewMsgs(tml);
			return tmpr;
		}else{
			//TODO error handling
			logger.error(String.format("sell stop order error: %s, response: %s", sellstop, or));
		}
		return tmpr;
	}
	/**
	 * [monitor buy order]
	 * 		|__ executed. 1 sell stop trailing order submitted (GTC), 2 msg added (monitor stop trailing order, monitor price cross). <open position>
	 *      |__ cancelled, remove me
	 */
	@Override
	public TradeMsgPR process(TradeMgr tm) {
		return process(tm, this.getSomap());
	}
}
