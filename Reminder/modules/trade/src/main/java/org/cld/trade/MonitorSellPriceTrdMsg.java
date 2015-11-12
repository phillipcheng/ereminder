package org.cld.trade;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.trade.StockOrder;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.OrderResponse;
import org.cld.trade.response.Quote;

public class MonitorSellPriceTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MonitorSellPriceTrdMsg.class);
	private String symbol;
	private float price;
	
	public MonitorSellPriceTrdMsg(){
		super(TradeMsgType.monitorSellLimitPrice);
	}
	
	public MonitorSellPriceTrdMsg(String symbol, float price, Map<StockOrderType, StockOrder> somap){
		this();
		this.symbol = symbol;
		this.price = price;
		this.setSomap(somap);
	}

	public String toString(){
		return String.format("SM:%s,%s,%.2f", this.getMsgType(), symbol, price);
	}

	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}

	/**
	 *  [monitor sell limit price cross]
	 *      |__ crossed. cancel stop trailing order, submit market sell order. <close position>
	 */
	@Override
	public TradeMsgPR process(TradeMgr tm) {
		List<Quote> ql = tm.getQuotes(new String[]{getSymbol()});
		TradeMsgPR tmpr = new TradeMsgPR();
		if (ql!=null && ql.size()==1){
			Quote q = ql.get(0);
			if (q.getLast()>=getPrice()){
				tmpr.setExecuted(true);
				//send 1 cancel order (succeeded), send 1 market order
				logger.info(String.format("price %s crossed sell limit %s.", q, this));
				StockOrder sellstorder = getSomap().get(StockOrderType.sellstoptrail);
				tm.cancelOrder(sellstorder.getOrderId());//send cancel order
				StockOrder selllmorder = getSomap().get(StockOrderType.selllimit);
				OrderResponse or = TutoArader.trySubmit(tm, selllmorder, true);
				if (OrderResponse.SUCCESS.equals(or.getError())){
					StockPosition trySp = new StockPosition(selllmorder, StockPosition.close, selllmorder.getOrderId());
					TradePersistMgr.closePosition(tm.getCconf().getSmalldbconf(), trySp);//
				}else{
					//TODO error handling
					logger.error(String.format("buy error: buy order: %s, response: %s", selllmorder, or));
				}
				return tmpr;
			}
		}else{
			logger.error(String.format("quote does not from symbol %s", getSymbol()));
		}
		return tmpr;
	}
}
