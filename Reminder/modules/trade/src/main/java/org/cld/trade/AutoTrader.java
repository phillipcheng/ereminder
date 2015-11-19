package org.cld.trade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.trade.StockOrder;
import org.cld.stock.trade.StockOrder.ActionType;
import org.cld.stock.trade.StockOrder.OrderType;
import org.cld.stock.trade.StockOrder.TimeInForceType;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.OrderResponse;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class AutoTrader {
	private static Logger logger =  LogManager.getLogger(AutoTrader.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private TradeMgr tm;
	private SelectStrategy bs;
	private SellStrategy ss;
	private Map<String, TradeMsg> msgMap = new HashMap<String, TradeMsg>();
	
	public AutoTrader(){
		tm = new TradeMgr("tradeking.properties");
		try{
			PropertiesConfiguration props = new PropertiesConfiguration("strategy.properties");
			List<SelectStrategy> bsl = SelectStrategy.gen(props, "TheStrategy");
			SellStrategy[] ssl = SellStrategy.gen(props);
			if (bsl.size()==1 && ssl.length==1){
				bs = bsl.get(0);
				ss = ssl[0];
			}else{
				logger.error("wrong strategy configuration.");
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	private static Map<StockOrderType, StockOrder> makeMap(List<StockOrder> sol){
		Map<StockOrderType, StockOrder> map = new HashMap<StockOrderType, StockOrder>();
		if (sol!=null){
			for (StockOrder so : sol){
				if (so.getAction()==ActionType.buy){
					map.put(StockOrderType.buy, so);
				}else{
					if (so.getTif()==TimeInForceType.MarktOnClose){
						map.put(StockOrderType.sellmarketclose, so);
					}else{
						if (so.getOrderType()==OrderType.stoplimit){
							map.put(StockOrderType.sellstop, so);
						}else if (so.getOrderType()==OrderType.stoptrailingpercentage){
							map.put(StockOrderType.sellstop, so);
						}else if (so.getOrderType()==OrderType.limit){
							map.put(StockOrderType.selllimit, so);
						}
					}
				}
			}
		}
		return map;
	}
	
	public static Map<StockOrderType, StockOrder> genSellOrderMap(StockPosition sp, SellStrategy ss){
		List<StockOrder> sellsos= SellStrategy.makeSellOrders(sp.getSymbol(), sp.getDt(), sp.getOrderQty(), sp.getOrderPrice(), ss);
		return makeMap(sellsos);
	}
	
	public static Map<StockOrderType, StockOrder> genStockOrderMap(SelectCandidateResult scr, SellStrategy ss, int cashAmount){
		StockOrder buyso = SellStrategy.makeBuyOrder(scr, ss, cashAmount);
		List<StockOrder> sellsos= SellStrategy.makeSellOrders(buyso.getStockid(), buyso.getSubmitTime(), buyso.getQuantity(), buyso.getLimitPrice(), ss);
		sellsos.add(buyso);
		return makeMap(sellsos);
	}
	
	//
	public static OrderResponse trySubmit(TradeMgr tm, StockOrder sobuy, boolean submit){
		if (sobuy!=null){
			if (!submit){
				logger.info(String.format("preview order: %s",sobuy));
				return tm.previewOrder(sobuy);
			}else{
				logger.info(String.format("make order: %s",sobuy));
				return tm.makeOrder(sobuy);
			}
		}else{
			logger.info(String.format("no order to submit."));
			return null;
		}
		
	}
	
	//useLast price or use Open price
	//used by test
	public OrderResponse tryBuyNow(boolean submit, boolean useLast){
		SelectCandidateResult scr = tm.applySelectStrategyNow(tm.getBaseMarketId(), tm.getMarketId(), useLast, bs);
		if (scr!=null){
			StockOrder sobuy =SellStrategy.makeBuyOrder(scr, ss, tm.getUseAmount());
			return trySubmit(tm, sobuy, submit);
		}
		return null;
	}

	public void addMsg(TradeMsg tmsg){
		synchronized(this.msgMap){
			msgMap.put(tmsg.getMsgId(), tmsg);
		}
	}
	public void addMsgs(List<TradeMsg> tml){
		synchronized(this.msgMap){
			for (TradeMsg tmsg:tml){
				msgMap.put(tmsg.getMsgId(), tmsg);
			}
		}
	}
	public int msgMapSize(){
		synchronized(this.msgMap){
			return msgMap.size();
		}
	}
	public List<String> getMsgMapKeys(){
		synchronized(this.msgMap){
			List<String> kl = new ArrayList<String>();
			Iterator<String> it = this.msgMap.keySet().iterator();
			while(it.hasNext()){
				kl.add(it.next());
			}
			return kl;
		}
	}
	public TradeMsg getMsg(String key){
		synchronized(this.msgMap){
			return this.msgMap.get(key);
		}
	}
	
	/**
	 * start: Msgs: [market will open]|[market will close]
	 *  [market will open]
	 * 		|__ no position, apply alg a buy order submitted (Day), 1 msg added (monitor buy order)
	 *      |__ has position, do nothing.
	 * 	[monitor buy order]
	 * 		|__ executed. 1 sell stop order submitted (Day), 2 msg added (monitor sell stop order, monitor price cross). <open position>
	 *      |__ cancelled, remove me
	 * 	[monitor stop order]
	 * 		|__ executed. 1 msg added (stop monitor price cross). <close position>
	 *      |__ cancelled.
	 *  [monitor limit price cross]
	 *      |__ crossed. cancel stop trailing order, submit market sell order. <close position>
	 *  [market will close]
	 * 		|__ has position
	 * 				|__ duration met: cancel sell order, submit sell on close order, clean msgs
	 * 				|__ duration not met: do nothing (open sell order, [monitor stop trailing order, monitor price cross])
	 *      |__ no position, do nothing
	 */
	public void run() {
		while (true){
			if (getMsgMap().size()>0){
				logger.info(String.format("%d messages to process.", getMsgMap().size()));
				List<TradeMsgPR> tmprlist = new ArrayList<TradeMsgPR>();
				List<String> keys = getMsgMapKeys();
				for (String key:keys){
					TradeMsg msg = getMsg(key);
					if (msg!=null){
						logger.info(String.format("process msg: %s",msg));
						TradeMsgPR tmpr = msg.process(tm);
						tmpr.setMsgId(msg.getMsgId());
						tmprlist.add(tmpr);
					}else{
						logger.error(String.format("msg for key:%s not found.", key));
					}
				}
				for (TradeMsgPR tmpr: tmprlist){
					if (tmpr.getNewMsgs()!=null){
						for (TradeMsg sm:tmpr.getNewMsgs()){
							getMsgMap().put(sm.getMsgId(), sm);
						}
					}
					if (tmpr.getRmMsgs()!=null){
						for (String mid:tmpr.getRmMsgs()){
							getMsgMap().remove(mid);
						}
					}
					if (tmpr.isExecuted()){
						getMsgMap().remove(tmpr.getMsgId());
					}
					if (tmpr.cleanAllMsgs){
						getMsgMap().clear();
					}
				}
			}
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				logger.error("", e);
			}
		}
	}
	
	//generate monitor buy order msgs
	public List<TradeMsg> processOpenPosition(){
		List<TradeMsg> tml = new ArrayList<TradeMsg>();
		Date dt= new Date();
		List<StockPosition> lp = TradePersistMgr.getOpenPosition(tm.getCconf().getSmalldbconf(), dt);
		if (lp.size()>0){
			for (StockPosition sp: lp){
				Map<StockOrderType, StockOrder> somap = AutoTrader.genSellOrderMap(sp, ss);
				TradeMsg tmsg;
				if (sp.getOrderId()!=null && !"".equals(sp.getOrderId())){
					tmsg = new MonitorBuyOrderTrdMsg(StockOrderType.buy, sp.getOrderId(), somap);
				}else{//has an open position which is opened day before so the order id is not working.
					tmsg = new BuyOrderFilledTrdMsg(somap);
				}
				tml.add(tmsg);
			}
		}else{
			logger.info(String.format("no open position for day:%s", sdf.format(dt)));
		}
		return tml;
	}
	
	public void start(TradeMsg tmsg){
		msgMap.put(tmsg.getMsgId(), tmsg);
		run();
	}
	
	public static final String JDM_KEY_AT="AutoTrader";
	public static void main(String[] args){
		try{
			AutoTrader at = new AutoTrader();
			//Create & start the scheduler.
	        StdSchedulerFactory factory = new StdSchedulerFactory();
	        factory.initialize("quartz.properties");
	        Scheduler scheduler = factory.getScheduler();
	        JobDataMap jdm = new JobDataMap();
	        jdm.put(JDM_KEY_AT, at);
	        JobDetail genMarketMsgJob = JobBuilder.newJob(GenMsgQuartzJob.class).
	        		withIdentity("genMarketMsg", "genMarketMsg").storeDurably().usingJobData(jdm).build();
	        
	        //scheduler to send market open msg
	        Trigger marketOpenTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(TradeMsgType.marketOpenSoon.toString(), TradeMsgType.marketOpenSoon.toString()).forJob(genMarketMsgJob).
	        		withSchedule(CronScheduleBuilder.cronSchedule(at.getTm().getMarketOpenCron()).inTimeZone(TimeZone.getTimeZone("EST"))).
	        		build();
	        //scheduler to send market close msg
	        Trigger marketCloseTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(TradeMsgType.marketCloseSoon.toString(), TradeMsgType.marketCloseSoon.toString()).forJob(genMarketMsgJob).
	        		withSchedule(CronScheduleBuilder.cronSchedule(at.getTm().getMarketCloseCron()).inTimeZone(TimeZone.getTimeZone("EST"))).
	        		build();
	        scheduler.addJob(genMarketMsgJob, true);
	        scheduler.scheduleJob(marketOpenTrigger);
	        scheduler.scheduleJob(marketCloseTrigger);
	        scheduler.start();
	        List<TradeMsg> tml = at.processOpenPosition();
	        at.addMsgs(tml);
	        at.run();
	        while(true){
	        	Thread.sleep(10000);
	        }
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	//setter and getter
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
	public SelectStrategy getBs() {
		return bs;
	}
	public void setBs(SelectStrategy bs) {
		this.bs = bs;
	}
}
