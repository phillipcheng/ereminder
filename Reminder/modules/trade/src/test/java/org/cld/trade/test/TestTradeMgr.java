package org.cld.trade.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.trade.StockOrder;
import org.cld.stock.trade.StockOrder.ActionType;
import org.cld.stock.trade.StockOrder.OrderType;
import org.cld.stock.trade.StockOrder.TimeInForceType;
import org.cld.trade.TradeMgr;
import org.cld.trade.TutoArader;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.response.Balance;
import org.cld.trade.response.Holding;
import org.cld.trade.response.OrderResponse;
import org.cld.trade.response.OrderStatus;
import org.cld.trade.response.Quote;
import org.junit.Test;

public class TestTradeMgr {
	private static Logger logger =  LogManager.getLogger(TestTradeMgr.class);
	TradeMgr tm = new TradeMgr("tradeking.properties");
	
	@Test
	public void testPreviewOrder(){
		StockOrder so = new StockOrder();
		so.setAction(ActionType.buy);
		so.setQuantity(100);
		so.setStockid("GLUU");
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
		so.setStockid("GLUU");
		so.setTif(TimeInForceType.DayOrder);
		so.setOrderType(OrderType.limit);
		so.setLimitPrice(3);
		tm.makeOrder(so);
	}
	
	@Test
	public void testMakeLimtSellOrder(){
		StockOrder so = new StockOrder();
		so.setAction(ActionType.sell);
		so.setQuantity(10);
		so.setStockid("AAPL");
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
		so.setStockid("GLUU");
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
		so.setStockid("GLUU");
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
	public void testGetQuotes(){
		tm.getQuotes(new String[]{"GLUU", "FIT"});
	}
	
	@Test
	public void testGetAllQuotes(){
		List<Quote> ql = tm.getMarketAllQuotes("nasdaq", "ALL");
		logger.info("ql:" + ql);
	}
	
	@Test
	public void testCancelOrder(){
		OrderResponse or = tm.cancelOrder("SVI-6007191439");
		logger.info(or);
	}
	
	@Test
	public void testGetOrderStatus(){
		Map<String, OrderStatus> map = tm.getOrderStatus();
		logger.info(map);
	}
	///
	@Test
	public void realTutorAraderTryBuy(){
		TutoArader ta = new TutoArader();
		boolean useLast=true;
		ta.tryBuy(false,useLast);
	}
	
	///
	@Test
	public void testTutorAraderTrySell(){
		TutoArader ta = new TutoArader();
		SellStrategy ss = new SellStrategy(1, 2, 4, 1);
		ta.setSs(ss);
		StockPosition sos = new StockPosition(new Date(), 120, 8.40f, "FTK", 1);
		ta.trySell(true,sos);
	}

}
