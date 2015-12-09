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
import org.cld.stock.HdfsReader;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.StrategyConst;
import org.cld.stock.trade.StockOrder.*;

//this simulator is validated against minute data
public class TradeSimulator {
	private static Logger logger =  LogManager.getLogger(TradeSimulator.class);
	private static final SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	//order list are treated as OCO
	private static Iterator<CandleQuote> tryExecuteOrder(List<StockOrder> sol, Iterator<CandleQuote> qlist, 
			Map<String, StockOrder> executedOrders, StockConfig sc){
		CandleQuote prevCq = null;
		float highest=0f;//for trailing
		while (qlist.hasNext()){
			CandleQuote cq = qlist.next();
			//clock is cq's trading day
			int exeNum=0;
			for (StockOrder so:sol){
				if (so.getAction() == ActionType.buy){
					if (so.getTif()==TimeInForceType.MarktOnClose){//NOT Tested
						if (so.getTriggerTime().equals(cq.getStartTime())){//
							so.setExecutedPrice(cq.getClose());
							so.setStatus(StatusType.executed);
							so.setExecuteTime(cq.getStartTime());//TODO this should be the endTime
							executedOrders.put(so.getOrderId(), so);
							exeNum++;
						}
					}else{
						if (so.getOrderType() == OrderType.market){
							so.setExecutedPrice(cq.getOpen());
							so.setStatus(StatusType.executed);
							so.setExecuteTime(cq.getStartTime());
							executedOrders.put(so.getOrderId(), so);
							exeNum++;
						}else if (so.getOrderType() == OrderType.limit){
							if (cq.getLow()<so.getLimitPrice()){
								//executed
								so.setExecutedPrice(so.getLimitPrice());
								so.setStatus(StatusType.executed);
								so.setExecuteTime(cq.getStartTime());//TODO
								executedOrders.put(so.getOrderId(), so);
								exeNum++;
							}else{
								if (so.getTif()==TimeInForceType.DayOrder){
									Date closeTime = sc.getCloseTime(so.getSubmitTime(), 0, StrategyConst.V_UNIT_DAY);
									if (cq.getStartTime().after(closeTime)){
										//fail to buy
										return null;
									}
								}
							}
						}else{
							logger.error(String.format("order type %s for action type %s not supported.", so.getOrderType(), so.getAction()));
						}
					}
				}else if (so.getAction() == ActionType.sell){
					if (so.getTif()==TimeInForceType.MarktOnClose){
						if (prevCq!=null){
							if (!so.getTriggerTime().before(prevCq.getStartTime()) && 
									so.getTriggerTime().before(cq.getStartTime()) || !qlist.hasNext()){//triggerTime between [prevCq and Cq), in case missing data
								so.setExecutedPrice(cq.getClose());
								so.setStatus(StatusType.executed);
								so.setExecuteTime(cq.getStartTime());//TODO this should be the endTime
								executedOrders.put(so.getOrderId(), so);
								exeNum++;
							}else{
								//logger.info(String.format("trigger Time: %s, prevCq start time:%s, cq start time:%s", 
										//msdf.format(so.getTriggerTime()), msdf.format(prevCq.getStartTime()), msdf.format(cq.getStartTime())));
							}
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
								exeNum++;
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
								exeNum++;
							}
						}else if (so.getOrderType() == OrderType.stoptrailingpercentage){
							if (prevCq==null){
								StockOrder pairedBuyOrder = executedOrders.get(so.getPairOrderId());
								if (pairedBuyOrder!=null && pairedBuyOrder.getExecutedPrice()>0){
									highest = pairedBuyOrder.getExecutedPrice();
								}else{
									logger.error(String.format("paired buy order of sell order %s not found.", so.getOrderId()));
								}
							}
							float stopPrice = highest * (1+so.getIncrementPercent()/100);
							if (cq.getLow()<stopPrice){
								so.setExecutedPrice(stopPrice);
								so.setStatus(StatusType.executed);
								so.setExecuteTime(cq.getStartTime());
								executedOrders.put(so.getOrderId(), so);
								exeNum++;
							}
							if (cq.getHigh()>highest){
								highest = cq.getHigh();
							}
						}
						else{
							logger.error(String.format("order type %s for action type %s not supported.", so.getOrderType(), so.getAction()));
						}
					}
					
				}else{
					logger.error(String.format("action type %s not supported.", so.getAction()));
				}
			}
			if (exeNum>1){
				logger.warn(String.format("multiple order executed within same candle quote, data not granular enough. %s", executedOrders.values()));
				TreeMap<Float, StockOrder> fsm = new TreeMap<Float, StockOrder>();
				for (StockOrder so:executedOrders.values()){
					if (so.getAction()==ActionType.sell){
						fsm.put(so.getExecutedPrice(), so);
					}
				}
				int i=0;
				for (StockOrder so:fsm.values()){
					if (i>0){
						//only take the lowest sell price one, other mark as cancelled
						so.setExecuteTime(null);
						so.setStatus(StatusType.cancelled);
					}
					i++;
				}
				return qlist;
			}else if (exeNum==1){
				return qlist;
			}
			prevCq = cq;
		}
		return qlist;
	}
	/**
	 * 
	 * @param dsoMap: for each day(submit day), given stockid, submit order to execute
	 * @param cq
	 */
	public static void submitStockOrder(BuySellInfo bsi, StockConfig sc, HdfsReader hr){
		logger.debug(bsi.toString());
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
		int holdDays = bsi.getSs().getHoldDuration();
		Date ed = StockUtil.getNextOpenDay(bsi.getSubmitD(), sc.getHolidays(), holdDays);
		List<CandleQuote> myCq = hr.getData(bsi.getSubmitD(), ed);
		Iterator<CandleQuote> cqi = myCq.iterator();
		Iterator<CandleQuote> afterBuyCQI = tryExecuteOrder(buySOs, cqi, executedOrder, sc);
		StockOrder buySO = buySOs.get(0);
		if (buySO.getExecuteTime()!=null){
			for (StockOrder sellSO: sellSOs){
				sellSO.setSubmitTime(buySO.getExecuteTime());//assume at the same time we submit
				if (sellSO.getTif()==TimeInForceType.MarktOnClose){
					sellSO.setTriggerTime(sc.getCloseTime(buySO.getExecuteTime(), bsi.getSs().getHoldDuration(), 
							bsi.getSs().getHoldUnit()));
				}
			}
			tryExecuteOrder(sellSOs, afterBuyCQI, executedOrder, sc);
			logger.debug("buySO:" + buySOs);
			logger.debug("sellSO:" + sellSOs);
		}else{
			logger.debug(String.format("failed to buy: %s", buySO));
		}
	}
	
	public static BuySellResult calculateBuySellResult(Date submitD, List<StockOrder> solist){
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
		if (buySOs.size()==1){
			StockOrder buySO = buySOs.get(0);
			buyPrice = buySO.getExecutedPrice();
			buyTime = buySO.getExecuteTime();
			stockid = buySO.getStockid();
		}else{
			logger.error(String.format("buySO size not 1 but %s", buySOs));
		}
		
		float sellPrice=0;
		StockOrder exeSo=null;
		for (StockOrder so: sellSOs){
			if (so.getStatus()==StatusType.executed){
				exeSo = so;
				sellPrice = exeSo.getExecutedPrice();
				break;
			}
		}
		if (buyPrice !=0){
			if (sellPrice!=0){//success transaction
				float percent = (sellPrice-buyPrice)/buyPrice;
				String selltype = null;
				if (exeSo.getOrderType()!=null){
					selltype = exeSo.getOrderType().toString();
				}else if (exeSo.getTif()!=null){
					selltype = exeSo.getTif().toString();
				}else{
					logger.error(String.format("tif and ordertype all null? for so:%s", exeSo));
				}
				return new BuySellResult(submitD, stockid, buyTime, buyPrice, 
						exeSo.getExecuteTime(), sellPrice, selltype, percent);
			}else{//system error, because of data not granular enough or the stock not active enough, must avoid this
				return new BuySellResult(submitD, stockid, buyTime, buyPrice, 
						submitD, 0f, OrderType.stop.toString(), 0f);
			}
		}else{//failed to buy
			return new BuySellResult(submitD, stockid, submitD, 0f, submitD, 0f, OrderType.stop.toString(), 0f);
		}
	}
	
	//used by test
	public static BuySellResult trade(SelectCandidateResult scr, SellStrategy ss, StockConfig sc, CrawlConf cconf){
		HdfsReader hr =  null;
		try {
			String stockid = scr.getStockId();
			List<StockOrder> sol = SellStrategy.makeStockOrders(scr, ss);
			BuySellInfo bsi = new BuySellInfo("any", ss, sol, scr.getDt());
			hr = StockPersistMgr.getReader(cconf, sc.getBTFQMinuteQuoteMapper(), stockid);
			TradeSimulator.submitStockOrder(bsi, sc, hr);
			return TradeSimulator.calculateBuySellResult(scr.getDt(), sol);
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			hr.close();
		}
	}
}
