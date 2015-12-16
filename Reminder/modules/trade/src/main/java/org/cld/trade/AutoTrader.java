package org.cld.trade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.StockOrder;
import org.cld.stock.strategy.TradeStrategy;
import org.cld.stock.strategy.StockOrder.ActionType;
import org.cld.stock.strategy.StockOrder.OrderType;
import org.cld.stock.strategy.StockOrder.TimeInForceType;
import org.cld.trade.evt.MonitorBuyOrderTrdMsg;
import org.cld.trade.evt.MonitorSellPriceTrdMsg;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.OrderStatus;
import org.cld.util.ProxyConf;
import org.cld.util.jdbc.DBConnConf;
import org.eclipse.jetty.client.HttpClient;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class AutoTrader implements Runnable {
	private static Logger logger =  LogManager.getLogger(AutoTrader.class);
	//external configures
	private static final String TRADE_CONNECTOR="trade.connector";
	private static final String HISTORY_DUMP="history.dump";
	private static final String STRATEGIES="strategies";
	
	//at configures
	private static final String USESTREAM_KEY="use.stream";
	private static final String USE_AMOUNT="use.amount";
	private static final String BASE_MARKET="base.market";
	private static final String MARKET_OPEN_CRON="market.open.cron";
	private static final String MARKET_CLOSE_CRON="market.close.cron";
	private static final String IS_PREVIEW="is.preview";
	
	//
	private TradeKingConnector tradeConnector;
	private Map<String, TreeMap<IntervalUnit, List<TradeStrategy>>> tsMap = new HashMap<String, TreeMap<IntervalUnit, List<TradeStrategy>>>();//symbol to trade-strategy instance
	private Set<String> symbols = new HashSet<String>();
	private String historyDumpProperties;

	private boolean useStream=true;
	private ProxyConf proxyConf;
	private DBConnConf dbConf;
	private int useAmount;
	private String baseMarketId;
	private String marketOpenCron="0 26 9 ? * 2-6";
	private String marketCloseCron="0 55 15 ? * 2-6";
	private boolean isPreview=true;
	private boolean useLast=false;
	private Map<String, TradeMsg> msgMap = new HashMap<String, TradeMsg>();
	
	public AutoTrader(){
		try{
			PropertiesConfiguration pc = new PropertiesConfiguration("at.properties");
			useStream = pc.getBoolean(USESTREAM_KEY);
			proxyConf = new ProxyConf(pc);
			dbConf = new DBConnConf("dm.", pc);
			setUseAmount(pc.getInt(USE_AMOUNT));
			setBaseMarketId(pc.getString(BASE_MARKET));
			marketOpenCron = pc.getString(MARKET_OPEN_CRON);
			marketCloseCron = pc.getString(MARKET_CLOSE_CRON);
			isPreview = pc.getBoolean(IS_PREVIEW);
			this.historyDumpProperties= pc.getString(HISTORY_DUMP);
			//setup trade connctor
			tradeConnector = new TradeKingConnector(pc.getString(TRADE_CONNECTOR));
			//setup strategy
			List<Object> strategyFiles = pc.getList(STRATEGIES);
			for (int i=0; i<strategyFiles.size(); i++){
				String strategyName = (String)strategyFiles.get(i);
				PropertiesConfiguration props = new PropertiesConfiguration(strategyName);
				Map<String, List<SelectStrategy>> bsMap = SelectStrategy.genMap(props, strategyName, getBaseMarketId());
				SellStrategy[] ssl = SellStrategy.gen(props);
				for (String symbol:bsMap.keySet()){
					symbols.add(symbol);
					TreeMap<IntervalUnit, List<TradeStrategy>> tslMap = tsMap.get(symbol);
					if (tslMap==null){
						tslMap = new TreeMap<IntervalUnit, List<TradeStrategy>>();
						tsMap.put(symbol, tslMap);
					}
					SelectStrategy bs = bsMap.get(symbol).get(0);//only 1 bs for each strategy file
					List<TradeStrategy> tsl = tslMap.get(bs.getLookupUnit());
					if (tsl==null){
						tsl = new ArrayList<TradeStrategy>();
						tslMap.put(bs.getLookupUnit(), tsl);
					}
					tsl.add(new TradeStrategy(bs, ssl[0]));
				}
			}
			logger.debug(String.format("interval-stock-ts map: %s", tsMap));
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public List<TradeStrategy> getTsl(String symbol, IntervalUnit iu){
		TreeMap<IntervalUnit, List<TradeStrategy>> map = tsMap.get(symbol);
		if (map!=null){
			return map.get(iu);
		}
		return null;
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
	
	public static Map<StockOrderType, StockOrder> genStockOrderMap(SelectCandidateResult scr, SellStrategy ss, int cashAmount){
		StockOrder buyso = SellStrategy.makeBuyOrder(scr, ss, cashAmount);
		List<StockOrder> sellsos= SellStrategy.makeSellOrders(buyso.getSymbol(), buyso.getSubmitTime(), buyso.getQuantity(), buyso.getLimitPrice(), ss);
		sellsos.add(buyso);
		return makeMap(sellsos);
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
	
	public void initEngine(){
		Map<String, OrderStatus> osmap = tradeConnector.getOrderStatus();
		for (String id:osmap.keySet()){
			OrderStatus os = osmap.get(id);
			if (os.getSide().equals(FixmlConst.Side_Buy) &&
					os.getStat().equals(OrderStatus.PENDING)){
				//for all buy order submitted in pending state, add the MonitorBuyOrderTrdMsg
				StockPosition sp = TradePersistMgr.getStockPositionByOrderId(this.getDbConf(), os.getOrderId());
				if (sp!=null){
					TradeMsg mboMsg = new MonitorBuyOrderTrdMsg(os.getOrderId(), sp.getSoMap());
					addMsg(mboMsg);
				}else{
					logger.error(String.format("stock position for %s not found.", os.getOrderId()));
				}
			}else if (os.getSide().equals(FixmlConst.Side_Sell) &&
					os.getStat().equals(OrderStatus.PENDING) &&
					os.getTyp().equals(FixmlConst.Typ_StopLimit) || os.getTyp().equals(FixmlConst.Typ_StopTrailing) || os.getTyp().equals(FixmlConst.Typ_Stop)){
				//for all stop sell order submitted, add the MonitorSellPriceTrdMsg
				StockPosition sp = TradePersistMgr.getStockPositionByOrderId(this.getDbConf(), os.getOrderId());
				if (sp!=null){
					StockOrder limitSellOrder = sp.getSoMap().get(StockOrderType.selllimit);
					if (limitSellOrder!=null){
						TradeMsg msoMsg = new MonitorSellPriceTrdMsg(limitSellOrder.getSymbol(), limitSellOrder.getLimitPrice(), sp.getSoMap());
						addMsg(msoMsg);
					}else{
						logger.error(String.format("limit sell order not found in somap:%s", sp.getSoMap()));
					}
				}else{
					logger.error(String.format("stock position for %s not found.", os.getOrderId()));
				}
			}
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
	@Override
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
						TradeMsgPR tmpr = msg.process(this);
						if (tmpr!=null){
							tmpr.setMsgId(msg.getMsgId());
							tmprlist.add(tmpr);
						}
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
	
	public void start(TradeMsg tmsg){
		msgMap.put(tmsg.getMsgId(), tmsg);
		run();
	}
	
	public static final String JDM_KEY_AT="AutoTrader";
	public static void startScheduler(AutoTrader at){
		try{
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
	        		withSchedule(CronScheduleBuilder.cronSchedule(at.getMarketOpenCron()).inTimeZone(TimeZone.getTimeZone("EST"))).
	        		build();
	        //scheduler to send market close msg
	        Trigger marketCloseTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(TradeMsgType.marketCloseSoon.toString(), TradeMsgType.marketCloseSoon.toString()).forJob(genMarketMsgJob).
	        		withSchedule(CronScheduleBuilder.cronSchedule(at.getMarketCloseCron()).inTimeZone(TimeZone.getTimeZone("EST"))).
	        		build();
	        scheduler.addJob(genMarketMsgJob, true);
	        scheduler.scheduleJob(marketOpenTrigger);
	        scheduler.scheduleJob(marketCloseTrigger);
	        scheduler.start();
		}catch(Exception e){
			logger.error("", e);
		}
	}
	public static void main(String[] args){
		try{
			AutoTrader at = new AutoTrader();
			//
			at.initEngine();
			new Thread(at).start();
			//
			StockConfig sc = StockUtil.getStockConfig(at.getBaseMarketId());
	        TradeDataMgr tradeDataMgr = new TradeDataMgr(at, sc);
	        List<String> symbolList = new ArrayList<String>();
	        symbolList.addAll(at.symbols);
	        int total=0;
			HttpClient client = new HttpClient();
			client.start();
			int totalDataThread = symbolList.size()/StreamMgr.REQUEST_MAX_SYMBOLS + 1;
			while (total<symbolList.size()){
				int end = Math.min(total+StreamMgr.REQUEST_MAX_SYMBOLS, symbolList.size());
				List<String> sl = symbolList.subList(total, end);
				Runnable streamMgr = null;
				if (at.useStream){
					streamMgr = new StreamMgr(sl, at.getTm(), tradeDataMgr, at.getProxyConf());
				}else{
					streamMgr = new StreamSimulator(sl, at.getTm(), tradeDataMgr, totalDataThread);
				}
				new Thread(streamMgr).start();
				total=end;
			}
	        //
	        HistoryDumpMgr hdm = new HistoryDumpMgr(at.getHistoryDumpProperties(), tradeDataMgr, sc);
	        new Thread(hdm).start();
	        //
	        AutoTrader.startScheduler(at);
	        //
	        while(true){
	        	Thread.sleep(10000);
	        }
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	//setter and getter
	public int getUseAmount() {
		return useAmount;
	}
	public void setUseAmount(int useAmount) {
		this.useAmount = useAmount;
	}
	public String getBaseMarketId() {
		return baseMarketId;
	}
	public void setBaseMarketId(String baseMarketId) {
		this.baseMarketId = baseMarketId;
	}
	public String getMarketOpenCron() {
		return marketOpenCron;
	}
	public void setMarketOpenCron(String marketOpenCron) {
		this.marketOpenCron = marketOpenCron;
	}
	public String getMarketCloseCron() {
		return marketCloseCron;
	}
	public void setMarketCloseCron(String marketCloseCron) {
		this.marketCloseCron = marketCloseCron;
	}
	public boolean isPreview() {
		return isPreview;
	}
	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}
	public boolean isUseLast() {
		return useLast;
	}
	public void setUseLast(boolean useLast) {
		this.useLast = useLast;
	}
	public TradeKingConnector getTm() {
		return tradeConnector;
	}
	public void setTm(TradeKingConnector tm) {
		this.tradeConnector = tm;
	}
	public Map<String, TradeMsg> getMsgMap() {
		return msgMap;
	}
	public void setMsgMap(Map<String, TradeMsg> msgMap) {
		this.msgMap = msgMap;
	}
	public String getHistoryDumpProperties() {
		return historyDumpProperties;
	}

	public ProxyConf getProxyConf() {
		return proxyConf;
	}

	public void setProxyConf(ProxyConf proxyConf) {
		this.proxyConf = proxyConf;
	}

	public DBConnConf getDbConf() {
		return dbConf;
	}

	public void setDbConf(DBConnConf dbConf) {
		this.dbConf = dbConf;
	}
}
