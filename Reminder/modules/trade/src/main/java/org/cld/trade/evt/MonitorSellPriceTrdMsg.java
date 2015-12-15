package org.cld.trade.evt;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.trade.StockOrder;
import org.cld.stock.trade.StockOrder.ActionType;
import org.cld.stock.trade.StockOrder.OrderType;
import org.cld.trade.AutoTrader;
import org.cld.trade.StockOrderType;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.TradeMsgType;
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
	public TradeMsgPR process(AutoTrader at) {
		List<Quote> ql = at.getTm().getQuotes(new String[]{getSymbol()});
		TradeMsgPR tmpr = new TradeMsgPR();
		if (ql!=null && ql.size()==1){
			Quote q = ql.get(0);
			if (q.getLast()>=getPrice()){
				tmpr.setExecuted(true);
				//send 1 cancel order (succeeded), send 1 market order
				logger.info(String.format("price %s crossed sell limit %s.", q, this));
				StockOrder sellstop = getSomap().get(StockOrderType.sellstop);
				at.getTm().cancelOrder(sellstop.getOrderId(), ActionType.sell, sellstop.getStockid(), sellstop.getQuantity());//send cancel order
				StockOrder selllimit = getSomap().get(StockOrderType.selllimit);
				//change this into a market order
				selllimit.setOrderType(OrderType.market);
				OrderResponse or = at.getTm().trySubmit(selllimit, true);
				selllimit.setOrderId(or.getClientorderid());
				if (OrderResponse.SUCCESS.equals(or.getError())){
					logger.info(String.format("market sell order %s submitted successfully.", or.getClientorderid()));
				}else{
					//TODO error handling
					logger.error(String.format("sell market order error: sell order: %s, response: %s", selllimit, or));
				}
				return tmpr;
			}
		}else{
			logger.error(String.format("quote does not from symbol %s", getSymbol()));
		}
		return tmpr;
	}
}
