package org.cld.trade;

import java.util.List;
import java.util.Map;

import org.cld.stock.strategy.StockOrder;
import org.cld.stock.strategy.StockOrder.ActionType;
import org.cld.trade.response.Balance;
import org.cld.trade.response.Holding;
import org.cld.trade.response.OrderResponse;
import org.cld.trade.response.OrderStatus;
import org.cld.trade.response.Quote;

public class TradeSimulatorConnector implements TradeApi{

	@Override
	public OrderResponse previewOrder(StockOrder so) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderResponse makeOrder(StockOrder so) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderResponse cancelOrder(String clientOrderId, ActionType at,
			String symbol, int quantity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Balance getBalance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Holding> getHolding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, OrderStatus> getOrderStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Quote> getQuotes(String[] stockids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderResponse trySubmit(StockOrder sobuy, boolean submit) {
		// TODO Auto-generated method stub
		return null;
	}

}
