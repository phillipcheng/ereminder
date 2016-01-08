package org.cld.trade.test;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.trade.TradeKingConnector;
import org.cld.stock.strategy.StockOrder;
import org.cld.stock.strategy.StockOrder.ActionType;
import org.cld.stock.strategy.StockOrder.OrderType;
import org.cld.stock.strategy.StockOrder.TimeInForceType;
import org.cld.trade.AutoTrader;
import org.cld.trade.response.Balance;
import org.cld.trade.response.Holding;
import org.cld.trade.response.OrderResponse;
import org.cld.trade.response.OrderStatus;
import org.junit.Test;

public class TestTradeConnector {
	private static Logger logger =  LogManager.getLogger(TestTradeConnector.class);
	TradeKingConnector tm = new TradeKingConnector("tradeking.properties");
	
	@Test
	public void testPreviewOrder(){
		StockOrder so = new StockOrder();
		so.setAction(ActionType.buy);
		so.setQuantity(100);
		so.setSymbol("GLUU");
		so.setTif(TimeInForceType.DayOrder);
		so.setOrderType(OrderType.limit);
		so.setLimitPrice(3);
		tm.previewOrder(so);
	}
	
	@Test
	public void testMakeLimitBuyOrder(){
		StockOrder so = new StockOrder();
		so.setAction(ActionType.buy);
		so.setQuantity(100);
		so.setSymbol("GLUU");
		so.setTif(TimeInForceType.DayOrder);
		so.setOrderType(OrderType.limit);
		so.setLimitPrice(2);
		tm.makeOrder(so);
	}
	
	@Test
	public void testCancelOrder(){
		OrderResponse or = tm.cancelOrder("SVI-6009123085", ActionType.sell, "CLVS", 36);
		logger.info(or);
	}
	
	@Test
	public void testMakeLimtSellOrder(){
		StockOrder so = new StockOrder();
		so.setAction(ActionType.sell);
		so.setQuantity(10);
		so.setSymbol("AAPL");
		so.setTif(TimeInForceType.GTC);
		so.setOrderType(OrderType.limit);
		so.setLimitPrice(130f);
		logger.info(tm.makeOrder(so));
	}
	
	@Test
	public void testMakeMarketOnCloseSellOrder(){
		StockOrder so = new StockOrder();
		so.setAction(ActionType.sell);
		so.setQuantity(2000);
		so.setSymbol("GLUU");
		so.setTif(TimeInForceType.MarktOnClose);
		so.setOrderType(OrderType.limit);
		so.setLimitPrice(3.29f);
		logger.info(tm.makeOrder(so));
	}
	
	@Test
	public void testMakeTrailStopSellOrder(){
		StockOrder so = new StockOrder();
		so.setAction(ActionType.sell);
		so.setQuantity(2000);
		so.setSymbol("GLUU");
		so.setTif(TimeInForceType.DayOrder);
		so.setOrderType(OrderType.stoptrailingpercentage);
		so.setIncrementPercent(3);//FOR SELL 3 PERCENT MEANS BELOW 3% OF LAST PRICE
		logger.info(tm.makeOrder(so));
	}
	
	@Test
	public void testGetAccount(){
		tm.getAccount();
	}
	
	@Test
	public void testGetBalance(){
		Balance b = tm.getBalance();
		logger.info("b:" + b);
	}
	
	@Test
	public void testGetHoldings(){
		List<Holding> hl = tm.getHolding();
		logger.info("hl:" + hl);
	}
	
	@Test
	public void testGetHistory(){
		List<Holding> hl = tm.getHolding();
		logger.info("hl:" + hl);
	}
	
	@Test
	public void testGetQuotesRegular(){
		tm.getQuotes(new String[]{"GLUU", "FIT"}, null, false);
	}

	@Test
	public void testGetOrderStatus(){
		Map<String, OrderStatus> map = tm.getOrderStatus();
		logger.info(map);
	}

}
