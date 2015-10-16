package org.cld.stock.trade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.stock.CandleQuote;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.trade.StockOrder.*;


public class TradeSimulator {
	private static Logger logger =  LogManager.getLogger(TradeSimulator.class);
	
	/**
	 * @param sol: put the stock order in the order of preference, for example, sell limit should before sell stop
	 * @param qlist
	 * @return
	 */
	private static Iterator<CandleQuote> tryExecuteOrder(List<StockOrder> sol, Iterator<CandleQuote> qlist, Map<String, StockOrder> executedOrders){
		while (qlist.hasNext()){
			boolean lastCq = false;
			CandleQuote cq = qlist.next();
			if (!qlist.hasNext()){
				lastCq = true;
			}
			for (StockOrder so:sol){
				if (so.getAction() == ActionType.buy){
					if (so.getOrderType() == OrderType.market){
						so.executedPrice = cq.getOpen();
						so.status = StatusType.executed;
						so.setExecuteTime(cq.getStartTime());
						executedOrders.put(so.getOrderId(), so);
						return qlist;
					}else if (so.getOrderType() == OrderType.limit){
						if (cq.getLow()<=so.getLimitPrice()){
							//executed
							so.executedPrice = so.getLimitPrice();
							so.status = StatusType.executed;
							so.setExecuteTime(cq.getStartTime());
							executedOrders.put(so.getOrderId(), so);
							return qlist;
						}
					}else{
						logger.error(String.format("order type %s for action type %s not supported.", so.getOrderId(), so.getAction()));
					}
				}else if (so.getAction() == ActionType.sell){
					if (so.getOrderType() == OrderType.limit){
						float limitPrice = 0;
						if (so.getLimitPrice()==0){
							//use limitPercentage
							StockOrder pairedBuyOrder = executedOrders.get(so.getPairOrderId());
							if (pairedBuyOrder!=null && pairedBuyOrder.executedPrice>0){
								float ratio = 1+ so.getLimitPercentage()/100;
								limitPrice = pairedBuyOrder.executedPrice * ratio;
							}else{
								logger.error(String.format("paired buy order of sell order %s not found.", so.getOrderId()));
							}
						}else{
							limitPrice = so.getLimitPrice();
						}
						if (cq.getHigh()>=limitPrice){
							//executed
							so.executedPrice = limitPrice;
							so.status = StatusType.executed;
							so.setExecuteTime(cq.getStartTime());
							executedOrders.put(so.getOrderId(), so);
							return qlist;
						}
					}else if (so.getOrderType() == OrderType.stoplimit){
						if (cq.getLow()<=so.getStopPrice()){
							so.executedPrice = so.getStopPrice();
							so.status = StatusType.executed;
							so.setExecuteTime(cq.getStartTime());
							executedOrders.put(so.getOrderId(), so);
							return qlist;
						}
					}else if (so.getOrderType() == OrderType.stoptrailingpercentage){
						float lastPrice = cq.getOpen();
						float stopPrice = lastPrice * (1-so.getIncrementPercent()/100);
						//pick low as current price, since we are not using tick data, we are using candle data, can be 1,5,15,30 minute, etc.
						if (cq.getLow()<stopPrice){
							so.executedPrice = stopPrice;
							so.status = StatusType.executed;
							so.setExecuteTime(cq.getStartTime());
							executedOrders.put(so.getOrderId(), so);
							return qlist;
						}
					}else if (so.getOrderType() == OrderType.forceclean && lastCq){
						so.executedPrice = cq.getClose();
						so.status = StatusType.executed;
						so.setExecuteTime(cq.getStartTime());
						executedOrders.put(so.getOrderId(), so);
						return qlist;
					}
					else{
						logger.error(String.format("order type %s for action type %s not supported.", so.getOrderId(), so.getAction()));
					}
				}else{
					logger.error(String.format("action type %s not supported.", so.getAction()));
					return qlist;
				}
			}
		}
		return qlist;
	}
	/**
	 * @param soMap
	 * @param cconf
	 * @return
	 */
	public static void submitDailyOrder(Map<String, List<StockOrder>> soMap, CrawlConf cconf, StockConfig sc){
		int i=0;
		StockOrder soSample = null;
		List<String> sidlist = new ArrayList<String>();
		for (String stockid: soMap.keySet()){
			sidlist.add(stockid);
			if (i==0){
				soSample = soMap.get(stockid).get(0);
			}
			i++;
		}
		if (soSample!=null){
			Date submitTime = soSample.getSubmitTime();//submit at open of the trading day
			Date ed = StockUtil.getNextOpenDay(submitTime, sc.getHolidays(), soSample.getDuration());
			Map<String, List<CandleQuote>> cqMap = StockPersistMgr.getFQDailyQuote(sc, cconf, sidlist, submitTime, ed);
			Map<String, StockOrder> executedOrder = new HashMap<String, StockOrder>(); //
			List<String> removeStockIds = new ArrayList<String>();
			for (String stockid:soMap.keySet()){
				List<StockOrder> sol = soMap.get(stockid);
				List<CandleQuote> quotes = cqMap.get(stockid);
				if (quotes!=null){
					List<StockOrder> buySOs = new ArrayList<StockOrder>();
					List<StockOrder> sellSOs = new ArrayList<StockOrder>();
					for (StockOrder so:sol){
						if (so.getAction()==ActionType.buy){
							buySOs.add(so);
						}else if (so.getAction() == ActionType.sell){
							sellSOs.add(so);
						}
					}
					Iterator<CandleQuote> afterBuyCQI = tryExecuteOrder(buySOs, quotes.iterator(), executedOrder);
					tryExecuteOrder(sellSOs, afterBuyCQI, executedOrder);
				}else{
					logger.info(String.format("no quotes found for stock:%s, remove it.", stockid));
					removeStockIds.add(stockid);
				}
			}
			for (String stockid:removeStockIds){
				soMap.remove(stockid);
			}
		}
	}
}
