package org.cld.trade;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.TradeTick;
import org.cld.stock.strategy.StockOrder;
import org.cld.stock.strategy.StockOrder.ActionType;
import org.cld.stock.strategy.StockOrder.StatusType;
import org.cld.trade.response.Balance;
import org.cld.trade.response.BuyingPower;
import org.cld.trade.response.Holding;
import org.cld.trade.response.OrderResponse;
import org.cld.trade.response.OrderStatus;
import org.cld.trade.response.Quote;

public class TradeSimulatorConnector implements TradeApi, Runnable {
	private static Logger logger =  LogManager.getLogger(TradeSimulatorConnector.class);
	Map<String, StockOrder> somap = new HashMap<String, StockOrder>(); //id to stockorder
	Map<String, TradeTick> quoteMap = new HashMap<String, TradeTick>();//symbol to quote
	private String inputFolder;
	private TradeDataMgr tdm;
	
	public TradeSimulatorConnector(String inputFolder, TradeDataMgr tdm){
		this.inputFolder = inputFolder;
		this.tdm = tdm;
	}
	
	@Override
	public OrderResponse cancelOrder(String clientOrderId, ActionType at,
			String symbol, int quantity) {
		logger.error("cancelOrder not implemented yet");
		return null;
	}

	@Override
	public List<Holding> getHolding() {
		logger.error("getHolding not implemented yet");
		return null;
	}
	@Override
	public Balance getBalance() {
		//i have lots of money
		BuyingPower bp = new BuyingPower(50000f);
		return new Balance(50000f, 50000f, bp);
	}
	
	@Override
	public OrderResponse previewOrder(StockOrder so) {
		logger.error("previewOrder not implemented yet");
		return null;
	}

	@Override
	public OrderResponse trySubmit(StockOrder sobuy, boolean submit) {
		return makeOrder(sobuy);
	}
	
	@Override
	public OrderResponse makeOrder(StockOrder so) {
		so.setStatus(StatusType.open);
		OrderResponse or = new OrderResponse(so.getOrderId(), OrderResponse.SUCCESS);
		somap.put(so.getOrderId(), so);
		return or;
	}

	@Override
	public OrderStatus getTheOrderStatus(String orderId) {
		StockOrder so = somap.get(orderId);
		if (so!=null){
			//String orderId, String symbol, int cumQty, float avgPrice, String stat, String side, String typ
			String stat = OrderStatus.toStatus(so.getStatus());
			String side = TradeKingConnector.fromActionType(so.getAction());
			String typ = TradeKingConnector.fromOrderType(so.getOrderType());
			return new OrderStatus(orderId, so.getSymbol(), so.getQuantity(), so.getExecutedPrice(), stat, side, typ);
		}else{
			logger.error(String.format("order %s not found in map %s", orderId, somap));
			return null;
		}
	}
	
	@Override
	public Map<String, OrderStatus> getOrderStatus() {
		Map<String, OrderStatus> osMap = new HashMap<String, OrderStatus>();
		for (String orderId:somap.keySet()){
			OrderStatus os = getTheOrderStatus(orderId);
			osMap.put(orderId, os);
		}
		return osMap;
	}

	@Override
	public List<Quote> getQuotes(String[] stockids, String[] fids, boolean extendedHour){
		List<Quote> ql = new ArrayList<Quote>();
		for (String symbol:stockids){
			if (!quoteMap.containsKey(symbol)){
				logger.error(String.format("quote not found for %s", symbol));
			}else{
				Quote q = new Quote(symbol, quoteMap.get(symbol));
				ql.add(q);
			}
		}
		return ql;
	}

	public File[] findFiles( String dirName){
    	File dir = new File(dirName);
    	return dir.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(".csv"); }
    	} );

    }
	
	public void updateOrderStatus(TradeTick tt, String symbol){
		for (StockOrder so: somap.values()){
			if (so.getSymbol().equals(symbol) && so.getStatus()==StatusType.open){
				if (so.getAction()==ActionType.buy && so.getLimitPrice()>tt.getLast() ||
						(so.getAction()==ActionType.sell && so.getLimitPrice()<tt.getLast())){
					so.setExecuteTime(tt.getDatetime());
					so.setExecutedPrice(tt.getLast());
					so.setStatus(StatusType.executed);
				}
			}
		}
	}
	
	public void updateQuotes(TradeTick tt, String symbol){
		quoteMap.put(symbol, tt);
	}
	
	public static final int PLAY_SPEED = 10; //tick per second
	
	@Override
	public void run() {
		try{
			File[] files = findFiles(inputFolder);
			for (File f: files){
				String fname = f.getName();
				String symbol = fname.substring(0, fname.lastIndexOf("."));
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				String line = null;
				int cnt=0;
				while ((line=br.readLine())!=null){
					logger.debug(String.format("get line:%s", line));
					cnt++;
					TradeTick tt = StreamHandler.processCsvData(symbol, line, tdm);
					updateOrderStatus(tt, symbol);
					updateQuotes(tt, symbol);
					if (cnt % PLAY_SPEED == 0){
						Thread.sleep(1000);
					}
				}
				br.close();
			}
		}catch(Exception e){
			logger.error("", e);
		}
		
	}
}
