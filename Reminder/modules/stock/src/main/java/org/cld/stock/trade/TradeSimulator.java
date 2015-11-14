package org.cld.stock.trade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.stock.CandleQuote;
import org.cld.stock.StockConfig;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.trade.StockOrder.*;
import org.cld.util.DateTimeUtil;


public class TradeSimulator {
	private static Logger logger =  LogManager.getLogger(TradeSimulator.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * @param sol: put the stock order in the order of preference, for example, sell limit should before sell stop
	 * @param qlist: has quote data 1 before
	 * @return
	 */
	private static List<CandleQuote> tryExecuteOrder(List<StockOrder> sol, List<CandleQuote> qlist, 
			Map<String, StockOrder> executedOrders, StockConfig sc){
		int idx=1;//leave 0 as the prevCq
		while (idx<qlist.size()){
			CandleQuote cq = qlist.get(idx);
			//clock is cq's trading day
			for (StockOrder so:sol){
				CandleQuote prevCq = qlist.get(idx-1);
				if (so.getAction() == ActionType.buy){
					if (sc.getDailyLimit()>0 &&
							cq.getHigh()==cq.getLow() && 
								cq.getHigh()>(1+sc.getDailyLimit()/100-0.005)*prevCq.getClose()){
							//all day is at the limit, can't buy
						logger.info(String.format("can't buy stock:%s on day:%s because daily limit. prev close:%.2f, today price:%.2f", 
								so.getStockid(), sdf.format(cq.getStartTime()), prevCq.getClose(), cq.getHigh()));
						return null;//failed to buy
					}else{
						if (so.getTif()==TimeInForceType.MarktOnClose){
							if (so.getSubmitTime().equals(cq.getStartTime())){//
								so.setExecutedPrice(cq.getClose());
								so.setStatus(StatusType.executed);
								so.setExecuteTime(cq.getStartTime());//TODO this should be the endTime
								executedOrders.put(so.getOrderId(), so);
								return qlist.subList(idx-1, qlist.size());
							}
						}else{
							if (so.getOrderType() == OrderType.market){
								so.setExecutedPrice(cq.getOpen());
								so.setStatus(StatusType.executed);
								so.setExecuteTime(cq.getStartTime());
								executedOrders.put(so.getOrderId(), so);
								return qlist.subList(idx-1, qlist.size());
							}else if (so.getOrderType() == OrderType.limit){
								if (cq.getLow()<=so.getLimitPrice()){
									//executed
									so.setExecutedPrice(so.getLimitPrice());
									so.setStatus(StatusType.executed);
									so.setExecuteTime(cq.getStartTime());//TODO
									executedOrders.put(so.getOrderId(), so);
									return qlist.subList(idx-1, qlist.size());
								}else{
									return null;//failed to buy
								}
							}else{
								logger.error(String.format("order type %s for action type %s not supported.", so.getOrderType(), so.getAction()));
							}
						}
					}
				}else if (so.getAction() == ActionType.sell){
					if (sc.getDailyLimit()>0 &&
							cq.getHigh()==cq.getLow() && 
								cq.getLow()<(1-sc.getDailyLimit()/100+0.005)*prevCq.getClose()){
							//all day is at the limit, can't buy
						logger.info(String.format("can't sell stock:%s on day:%s because daily limit. prev close:%.2f, today price:%.2f", 
								so.getStockid(), sdf.format(cq.getStartTime()), prevCq.getClose(), cq.getLow()));
					}else{
						if (so.getTif()==TimeInForceType.MarktOnClose){
							if (idx==qlist.size()-1){//on the last day
								so.setExecutedPrice(cq.getClose());
								so.setStatus(StatusType.executed);
								so.setExecuteTime(cq.getStartTime());//TODO this should be the endTime
								executedOrders.put(so.getOrderId(), so);
								return qlist.subList(idx-1, qlist.size());
							}
						}else{
							if (so.getOrderType() == OrderType.limit){
								float limitPrice = 0;
								if (so.getLimitPrice()==0){
									//use limitPercentage
									StockOrder pairedBuyOrder = executedOrders.get(so.getPairOrderId());
									if (pairedBuyOrder!=null && pairedBuyOrder.getExecutedPrice()>0){
										float ratio = 1+ so.getLimitPercentage()/100;
										limitPrice = pairedBuyOrder.getExecutedPrice() * ratio;
									}else{
										logger.error(String.format("paired buy order of sell order %s not found.", so.getOrderId()));
									}
								}else{
									limitPrice = so.getLimitPrice();
								}
								if (cq.getHigh()>=limitPrice){
									//executed
									so.setExecutedPrice(limitPrice);
									so.setStatus(StatusType.executed);
									so.setExecuteTime(cq.getStartTime());
									executedOrders.put(so.getOrderId(), so);
									return qlist.subList(idx-1, qlist.size());
								}
							}else if (so.getOrderType() == OrderType.stoplimit){
								float stopPrice = 0;
								if (so.getStopPrice()==0){
									//use limitPercentage
									StockOrder pairedBuyOrder = executedOrders.get(so.getPairOrderId());
									if (pairedBuyOrder!=null && pairedBuyOrder.getExecutedPrice()>0){
										float ratio = 1+ so.getLimitPercentage()/100;
										stopPrice = pairedBuyOrder.getExecutedPrice() * ratio;
									}else{
										logger.error(String.format("paired buy order of sell order %s not found.", so.getOrderId()));
									}
								}else{
									stopPrice = so.getStopPrice();
								}
								if (cq.getLow()<=stopPrice){
									so.setExecutedPrice(stopPrice);
									so.setStatus(StatusType.executed);
									so.setExecuteTime(cq.getStartTime());
									executedOrders.put(so.getOrderId(), so);
									return qlist.subList(idx-1, qlist.size());
								}
							}else if (so.getOrderType() == OrderType.stoptrailingpercentage){
								float lastPrice;//the reference price to set the stop price
								if (idx==1){
									lastPrice = cq.getOpen();
								}else{
									lastPrice = prevCq.getHigh();
									if (cq.getOpen()>lastPrice){
										lastPrice = cq.getOpen();
									}
								}
								float stopPrice = lastPrice * (1+so.getIncrementPercent()/100);
								//pick low as current price, since we are not using tick data, we are using candle data, can be 1,5,15,30 minute, etc.
								if (cq.getLow()<stopPrice){
									so.setExecutedPrice(stopPrice);
									so.setStatus(StatusType.executed);
									so.setExecuteTime(cq.getStartTime());
									executedOrders.put(so.getOrderId(), so);
									return qlist.subList(idx-1, qlist.size());
								}
							}
							else{
								logger.error(String.format("order type %s for action type %s not supported.", so.getOrderType(), so.getAction()));
							}
						}
					}
				}else{
					logger.error(String.format("action type %s not supported.", so.getAction()));
				}
			}
			idx++;
		}
		return qlist;
	}
	
	/**
	 * 
	 * @param dsoMap: for each day(submit day), given stockid, submit order to execute
	 * @param cq
	 */
	public static void submitDailyOrder(List<BuySellInfo> bsil, TreeMap<Date, CandleQuote> cq, StockConfig sc){
		for (BuySellInfo bsi: bsil){
			Map<String, StockOrder> executedOrder = new HashMap<String, StockOrder>(); //
			List<StockOrder> sol = bsi.getSos();
			List<StockOrder> buySOs = new ArrayList<StockOrder>();
			List<StockOrder> sellSOs = new ArrayList<StockOrder>();
			for (StockOrder so:sol){
				if (so.getAction()==ActionType.buy){
					buySOs.add(so);
				}else if (so.getAction() == ActionType.sell){
					sellSOs.add(so);
				}
			}
			Date oneTDBefore = cq.floorKey(DateTimeUtil.yesterday(bsi.getSubmitD()));
			if (oneTDBefore!=null){
				Iterator<CandleQuote> cqi = cq.tailMap(oneTDBefore, true).values().iterator();
				List<CandleQuote> cqs = new ArrayList<CandleQuote>();
				for (int i=0; i<=bsi.getSs().getHoldDuration(); i++){
					if (cqi.hasNext()){
						cqs.add(cqi.next());
					}
				}
				List<CandleQuote> afterBuyCQI = tryExecuteOrder(buySOs, cqs, executedOrder, sc);
				if (afterBuyCQI!=null){
					tryExecuteOrder(sellSOs, afterBuyCQI, executedOrder, sc);
					logger.debug(buySOs);
					logger.debug(sellSOs);
				}
			}
		}
	}
	
	public static BuySellResult calculateBuySellResult(BuySellInfo bsi, List<StockOrder> solist){
		List<StockOrder> buySOs = new ArrayList<StockOrder>();
		List<StockOrder> sellSOs = new ArrayList<StockOrder>();
		for (StockOrder so:solist){
			if (so.getAction()==ActionType.buy){
				buySOs.add(so);
			}else if (so.getAction() == ActionType.sell){
				sellSOs.add(so);
			}
		}
		float buyPrice=0;
		Date buyTime=null;
		String stockid=null;
		for (StockOrder so: buySOs){
			if (so.getStatus()==StatusType.executed){
				buyPrice = so.getExecutedPrice();
				buyTime = so.getExecuteTime();
				stockid = so.getStockid();
				break;
			}
		}
		if (stockid!=null){
			float sellPrice=0;
			StockOrder exeSo=null;
			for (StockOrder so: sellSOs){
				if (so.getStatus()==StatusType.executed){
					exeSo = so;
					sellPrice = exeSo.getExecutedPrice();
					break;
				}
			}
			if (buyPrice !=0 && sellPrice !=0){
				float percent = (sellPrice-buyPrice)/buyPrice;
				String selltype = null;
				if (exeSo.getOrderType()!=null){
					selltype = exeSo.getOrderType().toString();
				}else if (exeSo.getTif()!=null){
					selltype = exeSo.getTif().toString();
				}else{
					logger.error(String.format("tif and ordertype all null? for so:%s", exeSo));
				}
				return new BuySellResult(bsi.getSubmitD(), stockid, buyTime, buyPrice, 
						exeSo.getExecuteTime(), sellPrice, selltype, percent);
			}
		}
		return null;
	}
	
	//used by test
	public static BuySellResult trade(SelectCandidateResult scr, SellStrategy ss, StockConfig sc, CrawlConf cconf){
		try {
			String stockid = scr.getStockId();
			Date submitD = sdf.parse(scr.getDt());
			List<StockOrder> sol = SellStrategy.makeStockOrders(scr, ss);
			BuySellInfo bsi = new BuySellInfo("any", ss, sol, submitD);
			
			List<Object> lo = StockPersistMgr.getDataPivotByStockDate(cconf.getSmalldbconf(), sc.getFQDailyQuoteTableMapper(), stockid, 
					submitD, submitD, 1, ss.getHoldDuration());
			TreeMap<Date, CandleQuote> dcmap = new TreeMap<Date, CandleQuote>();
			for (Object o:lo){
				CandleQuote cq = (CandleQuote) o;
				dcmap.put(cq.getStartTime(), cq);
			}
			List<BuySellInfo> bsil = new ArrayList<BuySellInfo>();
			bsil.add(bsi);
			TradeSimulator.submitDailyOrder(bsil, dcmap, sc);
			return TradeSimulator.calculateBuySellResult(bsi, sol);
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
}
