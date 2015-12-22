package org.cld.trade.evt;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.OrderFilled;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.StockOrder;
import org.cld.stock.strategy.TradeStrategy;
import org.cld.stock.strategy.StockOrder.ActionType;
import org.cld.stock.strategy.StockOrder.OrderType;
import org.cld.trade.AutoTrader;
import org.cld.trade.StockOrderType;
import org.cld.trade.TradeKingConnector;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.TradeMsgType;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.OrderResponse;
import org.cld.trade.response.Quote;

public class MonitorSellPriceTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MonitorSellPriceTrdMsg.class);
	private String symbol;
	private float price;
	
	public MonitorSellPriceTrdMsg(String symbol, float price, SelectCandidateResult scr, String bsName, Map<String, StockOrder> somap){
		super(TradeMsgType.monitorSellLimitPrice, scr, bsName, somap);
		this.symbol = symbol;
		this.price = price;
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
				StockOrder sellstop = getSomap().get(StockOrderType.sellstop.name());
				at.getTm().cancelOrder(sellstop.getOrderId(), ActionType.sell, sellstop.getSymbol(), sellstop.getQuantity());//send cancel order
				StockOrder selllimit = getSomap().get(StockOrderType.selllimit.name());
				//change this into a market order
				selllimit.setOrderType(OrderType.market);
				OrderResponse or = at.getTm().trySubmit(selllimit, at.isPreview());
				selllimit.setOrderId(or.getClientorderid());
				OrderFilled of = new OrderFilled(symbol, selllimit.getQuantity(), selllimit.getLimitPrice(), ActionType.sell, OrderType.limit);
				SelectStrategy bs = at.getBs(scr.getSymbol(), bsName);
				if (bs!=null){
					if (OrderResponse.SUCCESS.equals(or.getError())){
						bs.tradeCompleted(of, true);
						StockPosition sp = TradePersistMgr.getStockPositionByOrderId(at.getDbConf(), sellstop.getOrderId());
						logger.info(String.format("limit sell order %s submitted successfully.", or.getClientorderid()));
						if (sp!=null){
							TradePersistMgr.updateLimitSellOrderId(at.getDbConf(), sp.getBuyOrderId(), or.getClientorderid());
						}
					}else{
						bs.tradeCompleted(of, false);
						logger.error(String.format("sell market order error: sell order: %s, response: %s", selllimit, or));
					}
				}else{
					logger.error(String.format("SYSTEM error, bs can't be found for name:%s", bsName));
				}
				return tmpr;
			}
		}else{
			logger.error(String.format("quote does not from symbol %s", getSymbol()));
		}
		return tmpr;
	}
}
