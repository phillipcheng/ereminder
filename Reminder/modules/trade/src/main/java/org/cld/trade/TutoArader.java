package org.cld.trade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.trade.StockOrder;
import org.cld.stock.trade.StockOrder.ActionType;
import org.cld.stock.trade.StockOrder.OrderType;
import org.cld.stock.trade.StockOrder.TimeInForceType;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.response.OrderResponse;
import org.cld.trade.response.OrderStatus;
import org.cld.trade.response.Quote;
import org.cld.util.jdbc.DBConnConf;

public class TutoArader {
	private static Logger logger =  LogManager.getLogger(TutoArader.class);
	
	private TradeMgr tm;
	private String baseMarketId;
	private String marketId;
	private DBConnConf dbconf;
	private SelectStrategy bs;
	private SellStrategy ss;
	private Map<String, TradeMsg> msgMap;
	
	public SellStrategy getSs() {
		return ss;
	}
	public void setSs(SellStrategy ss) {
		this.ss = ss;
	}
	public TradeMgr getTm() {
		return tm;
	}
	public void setTm(TradeMgr tm) {
		this.tm = tm;
	}
	public Map<String, TradeMsg> getMsgMap() {
		return msgMap;
	}
	public void setMsgMap(Map<String, TradeMsg> msgMap) {
		this.msgMap = msgMap;
	}
	
	public TutoArader(){
		tm = new TradeMgr("tradeking.properties");
		baseMarketId="nasdaq";
		marketId="ALL";
		dbconf = tm.getCconf().getSmalldbconf();
		bs = new SelectStrategy();
		bs.setOrderDirection(SelectStrategy.DESC);
		bs.setParams(new Object[]{7f,0f});//7,0
		ss = new SellStrategy(1, 2, 9, 1);
	}
	
	private Map<StockOrderType, StockOrder> makeMap(List<StockOrder> sol){
		Map<StockOrderType, StockOrder> map = new HashMap<StockOrderType, StockOrder>();
		if (sol!=null){
			for (StockOrder so : sol){
				if (so.getAction()==ActionType.buy){
					map.put(StockOrderType.buy, so);
				}else{
					if (so.getTif()==TimeInForceType.MarktOnClose){
						map.put(StockOrderType.sellmarketclose, so);
					}else{
						if (so.getOrderType()==OrderType.limit){
							map.put(StockOrderType.selllimit, so);
						}else if (so.getOrderType()==OrderType.stoptrailingpercentage){
							map.put(StockOrderType.sellstoptrail, so);
						}
					}
				}
			}
		}
		return map;
	}
	
	//useLast price or use Open price
	public OrderResponse tryBuy(boolean submit, boolean useLast){
		StockOrder sobuy = tm.applyCloseDropAvgNow(baseMarketId, marketId,useLast,bs,ss);
		if (sobuy!=null){
			int quantity=(int) (tm.getUseAmount()/sobuy.getLimitPrice());
			sobuy.setQuantity(quantity);
			StockPosition sos = null;
			if (!submit){
				tm.previewOrder(sobuy);
			}else{
				return tm.makeOrder(sobuy);
			}
		}else{
			logger.info(String.format("no buy order."));
		}
		return null;
	}
	
	public void trySell(boolean submit, StockPosition buySos){
		List<StockOrder> sol = SellStrategy.makeSellOrders(buySos.getSymbol(), buySos.getDt(), buySos.getOrderQty(), buySos.getOrderPrice(), ss);
		Map<StockOrderType, StockOrder> map = makeMap(sol);
		StockOrder sellLimitSO = map.get(StockOrderType.selllimit);
		StockOrder sellStopTrailSO = map.get(StockOrderType.sellstoptrail);
		if (submit){
			tm.makeOrder(sellLimitSO);
			tm.makeOrder(sellStopTrailSO);
		}else{
			tm.previewOrder(sellLimitSO);
			tm.previewOrder(sellStopTrailSO);
		}
	}
	
	/**
	 *  [market will open]
	 * 		|__ no position, apply alg a buy order submitted (Day), 1 msg added (monitor buy order)
	 *      |__ has position, do nothing.
	 * return list of msg >0, add those, remove me, executed
	 * return list of msg =0, remove me, opened, but no opp found.
	 * return null, ignore, not yet opened
	 */
	public List<TradeMsg> marketOpenSoon(){
		//submit 1 limit buy order, submit 1 monitor order msg
		return null;
	}
	
	/**
	 *  [market will close]
	 * 		|__ has position
	 * 				|__ duration met: cancel sell order, submit sell on close order, clean msgs
	 * 				|__ duration not met: do nothing (open sell order, [monitor stop trailing order, monitor price cross])
	 *      |__ no position, do nothing
	 * return true, remove all messages, all orders are cancelled and position cleared
	 * return false, remove me, no position or duration not reached
	 */
	public boolean marketCloseSoon(){
		return true;
	}
	
	/**
	 * [monitor buy order]
	 * 		|__ executed. 1 sell stop trailing order submitted (GTC), 2 msg added (monitor stop trailing order, monitor price cross). <open position>
	 *      |__ cancelled, remove me
	 * return list of msg >0, add those, remove me, executed
	 * return list of msg =0, remove me, be cancelled.
	 * return null, ignore
	 */
	public List<TradeMsg> monitorBuyOrderExecuted(TradeMsg sm){
		Map<String, OrderStatus> map = tm.getOrderStatus();
		OrderStatus os = map.get(sm.getOrderId());
		if (os!=null){
			if (OrderStatus.FILLED.equals(os.getStat())){
				//submit 1 sell stop trailing order, 1 monitor order msg and 1 monitor price msg
				
			}else{
				logger.info(String.format("status is %s for order %s", os.getStat(), os.getOrderId()));
			}
		}else{
			logger.info(String.format("%s not found in the recent orders.", sm.getOrderId()));
		}
		return null;
	}
	
	/**
	 * 	[monitor stop trailing order]
	 * 		|__ executed. 1 msg added (stop monitor price cross). <close position>
	 *      |__ cancelled.
	 * true: remove me, executed.
	 * false: ignore
	 */
	public boolean monitorSellOrderExecuted(TradeMsg sm){
		Map<String, OrderStatus> map = tm.getOrderStatus();
		OrderStatus os = map.get(sm.getOrderId());
		if (os!=null){
			if (OrderStatus.FILLED.equals(os.getStat())){
				if (sm.getSomap().get(StockOrderType.sellstoptrail).getOrderId().equals(sm.getOrderId())){
					//send 1 stop monitoring price msg
					return true;
				}
			}else{
				logger.info(String.format("status is %s for order %s", os.getStat(), os.getOrderId()));
			}
		}else{
			logger.info(String.format("%s not found in the recent orders.", sm.getOrderId()));
		}
		return false;
	}
	
	/**
	 *  [monitor sell limit price cross]
	 *      |__ crossed. cancel stop trailing order, submit market sell order. <close position>
	 * true: remove me, crossed.
	 * false: ignore
	 */
	public boolean monitorPrice(TradeMsg sm){
		List<Quote> ql = tm.getQuotes(new String[]{sm.getSymbol()});
		if (ql!=null && ql.size()==1){
			Quote q = ql.get(0);
			if (q.getLast()>=sm.getPrice()){
				//send 1 cancel order (succeeded), send 1 market order
				return true;
			}
		}else{
			logger.error("quote does not from symbol %s", sm.getSymbol());
		}
		return false;
	}
	
	/**
	 * start: Msgs: [market will open]|[market will close]
	 *  [market will open]
	 * 		|__ no position, apply alg a buy order submitted (Day), 1 msg added (monitor buy order)
	 *      |__ has position, do nothing.
	 * 	[monitor buy order]
	 * 		|__ executed. 1 sell stop trailing order submitted (GTC), 2 msg added (monitor stop trailing order, monitor price cross). <open position>
	 *      |__ cancelled, remove me
	 * 	[monitor stop trailing order]
	 * 		|__ executed. 1 msg added (stop monitor price cross). <close position>
	 *      |__ cancelled.
	 *  [monitor sell limit price cross]
	 *      |__ crossed. cancel stop trailing order, submit market sell order. <close position>
	 *  [market will close]
	 * 		|__ has position
	 * 				|__ duration met: cancel sell order, submit sell on close order, clean msgs
	 * 				|__ duration not met: do nothing (open sell order, [monitor stop trailing order, monitor price cross])
	 *      |__ no position, do nothing
	 */
	public void run() {
		while (true){
			List<String> removeList = new ArrayList<String>();
			List<TradeMsg> addList = new ArrayList<TradeMsg>();
			for (TradeMsg sm:getMsgMap().values()){
				if (sm.getMsgType()==TradeMsgType.monitorBuyLimitOrder){
					List<TradeMsg> toAddList = monitorBuyOrderExecuted(sm);
					if (toAddList!=null && toAddList.size()>=0){
						addList.addAll(toAddList);
						removeList.add(sm.getMsgId());	
					}
				}else if (sm.getMsgType()==TradeMsgType.monitorSellStopTrailingOrder){
					boolean executed = monitorSellOrderExecuted(sm);
					if (executed){
						removeList.add(sm.getTargetMsgId());//
						removeList.add(sm.getMsgId());
					}
				}else if (sm.getMsgType()==TradeMsgType.monitorSellLimitPrice){
					boolean crossed = monitorPrice(sm);
					if (crossed){
						removeList.add(sm.getMsgId());
					}
				}else if (sm.getMsgType()==TradeMsgType.marketOpenSoon){
					List<TradeMsg> toAddList = marketOpenSoon();
					if (toAddList!=null && toAddList.size()>=0){
						addList.addAll(toAddList);
						removeList.add(sm.getMsgId());	
					}
				}else if (sm.getMsgType()==TradeMsgType.marketCloseSoon){
					boolean cleanAll = marketCloseSoon();
					if (cleanAll){
						removeList.addAll(getMsgMap().keySet());	
					}else{
						removeList.add(sm.getMsgId());
					}
				}else{
					logger.error(String.format("msg type not supported:%s", tm));
				}
			}
			for (TradeMsg sm:addList){
				getMsgMap().put(sm.getMsgId(), sm);
			}
			for (String mid:removeList){
				getMsgMap().remove(mid);
			}
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				logger.error("", e);
			}
		}
	}
}
