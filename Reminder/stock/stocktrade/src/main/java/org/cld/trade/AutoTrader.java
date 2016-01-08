package org.cld.trade;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.stock.common.DivSplit;
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
import org.cld.stock.strategy.persist.StrategyPersistMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.trade.evt.MarketOpenCloseEvtType;
import org.cld.trade.evt.MarketStatusType;
import org.cld.trade.evt.MonitorBuyOrderTrdMsg;
import org.cld.trade.evt.MonitorSellPriceTrdMsg;
import org.cld.trade.evt.MonitorSellStopOrderTrdMsg;
import org.cld.trade.evt.TradeMsgType;
import org.cld.trade.mgmt.AutoTraderMXBean;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.OrderStatus;
import org.cld.util.ProxyConf;
import org.cld.util.StringUtil;
import org.cld.util.jdbc.DBConnConf;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class AutoTrader implements Runnable, AutoTraderMXBean {
	private static Logger logger =  LogManager.getLogger(AutoTrader.class);
	//external configures
	private static final String TRADE_CONNECTOR="trade.connector";
	private static final String HISTORY_DUMP="history.dump";
	private static final String STRATEGIES="strategies";
	private static final String CRAWL_CONF="crawl.conf";
	
	private static final String USESTREAM_KEY="use.stream";
	private static final String USE_AMOUNT="use.amount";
	private static final String BASE_MARKET="base.market";
	private static final String IS_PREVIEW="is.preview";
	
	PropertiesConfiguration atProperties;
	//(symbol, (interval unit, [trade-strategy instance]))
	private Map<String, TreeMap<IntervalUnit, List<TradeStrategy>>> siutsMap = 
			new HashMap<String, TreeMap<IntervalUnit, List<TradeStrategy>>>();
	//(symbol_bsname, trade-strategy instance)
	private Map<String, TradeStrategy> tsInstMap = new HashMap<String, TradeStrategy>();
	private Set<String> symbols = new HashSet<String>();
	
	//
	private TradeApi tradeConnector;
	private String historyDumpProperties;
	private String crawlconfProperties;
	private CrawlConf cconf;

	private ProxyConf proxyConf;
	private DBConnConf dbConf;
	private int useAmount;
	private String baseMarketId;
	
	private String regularMarketOpenCron="0 29 9 ? * 2-6";
	private String regularMarketClosedCron = "0 0 16 ? * 2-6";
	private String preMarketOpenCron = "0 0 6 ? * 2-6";
	private String preMarketCloseCron = "0 28 9 ? * 2-6";
	private String afterHourMarketOpenCron = "0 2 16 ? * 2-6";
	private String afterHourMarketCloseCron = "0 0 20 ? * 2-6";
	
	private boolean isPreview=true;
	private Map<String, TradeMsg> msgMap = new HashMap<String, TradeMsg>();
	private Set<String> prevMsgIds = new HashSet<String>();//to curb too much log
	
	private StockConfig sc;
	private TradeDataMgr tradeDataMgr;
	private MarketStatusType curMst;
	
	@Override
	public void setupStrategys(){
		try {
			siutsMap = new HashMap<String, TreeMap<IntervalUnit, List<TradeStrategy>>>();
			tsInstMap = new HashMap<String, TradeStrategy>();
			List<Object> strategyFiles = atProperties.getList(STRATEGIES);
			for (int i=0; i<strategyFiles.size(); i++){
				String strategyName = (String)strategyFiles.get(i);
				logger.debug(String.format("interval-stock-ts map: %s", siutsMap));
				PropertiesConfiguration props = new PropertiesConfiguration(strategyName);
				Map<String, List<SelectStrategy>> bsMap = SelectStrategy.genMap(props, strategyName, getBaseMarketId(), dbConf);
				SellStrategy[] ssl = SellStrategy.gen(props);
				for (String symbol:bsMap.keySet()){
					symbols.add(symbol);
					TreeMap<IntervalUnit, List<TradeStrategy>> tslMap = siutsMap.get(symbol);
					if (tslMap==null){
						tslMap = new TreeMap<IntervalUnit, List<TradeStrategy>>();
						siutsMap.put(symbol, tslMap);
					}
					SelectStrategy bs = bsMap.get(symbol).get(0);//only 1 bs for each strategy file
					List<TradeStrategy> tsl = tslMap.get(bs.getLookupUnit());
					if (tsl==null){
						tsl = new ArrayList<TradeStrategy>();
						tslMap.put(bs.getLookupUnit(), tsl);
					}
					String key = getSymbolBsKey(symbol, bs.getName());
					TradeStrategy ts = new TradeStrategy(bs, ssl[0]);
					tsl.add(ts);
					tsInstMap.put(key, ts);
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public AutoTrader(){
		try{
			atProperties = new PropertiesConfiguration("at.properties");
			atProperties.getBoolean(USESTREAM_KEY);
			proxyConf = new ProxyConf(atProperties);
			dbConf = new DBConnConf("dm.", atProperties);
			setUseAmount(atProperties.getInt(USE_AMOUNT));
			setBaseMarketId(atProperties.getString(BASE_MARKET));
			isPreview = atProperties.getBoolean(IS_PREVIEW);
			historyDumpProperties= atProperties.getString(HISTORY_DUMP);
			//setup trade connctor
			tradeConnector = new TradeKingConnector(atProperties.getString(TRADE_CONNECTOR));
			//setup cconf
			crawlconfProperties = atProperties.getString(CRAWL_CONF);
			setCconf((CrawlConf) TaskUtil.getTaskConf(crawlconfProperties));
			//setup strategy
			setupStrategys();
			//setup data manager
			sc = StockUtil.getStockConfig(getBaseMarketId());
	        tradeDataMgr = new TradeDataMgr(this, sc);
	        //apply split div when engine start, this is called used by preMarket open evt
	        this.applySplitDiv(new Date());
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public static String getSymbolBsKey(String symbol, String bsName){
		return String.format("%s_%s", symbol, bsName);
	}
	
	public SelectStrategy getBs(String symbol, String bsName){
		String key = getSymbolBsKey(symbol, bsName);
		TradeStrategy ts = tsInstMap.get(key);
		if (ts!=null){
			return ts.getBs();
		}else{
			return null;
		}
	}
	
	public SellStrategy getSs(String symbol, String bsName){
		String key = getSymbolBsKey(symbol, bsName);
		TradeStrategy ts = tsInstMap.get(key);
		if (ts!=null){
			return ts.getSs();
		}else{
			return null;
		}
	}
	
	public List<TradeStrategy> getTsl(String symbol, IntervalUnit iu){
		TreeMap<IntervalUnit, List<TradeStrategy>> map = siutsMap.get(symbol);
		if (map!=null){
			return map.get(iu);
		}
		return null;
	}
	
	public List<TradeStrategy> getTsl(String symbol){
		List<TradeStrategy> tsl = new ArrayList<TradeStrategy>();
		TreeMap<IntervalUnit, List<TradeStrategy>> map = siutsMap.get(symbol);
		if (map!=null){
			for (IntervalUnit iu: map.keySet()){
				List<TradeStrategy> l = map.get(iu);
				if (l!=null){
					tsl.addAll(l);
				}
			}
		}
		return tsl;
	}
	
	private static Map<String, StockOrder> makeMap(List<StockOrder> sol){
		Map<String, StockOrder> map = new HashMap<String, StockOrder>();
		if (sol!=null){
			for (StockOrder so : sol){
				if (so.getAction()==ActionType.buy){
					map.put(StockOrderType.buy.name(), so);
				}else{
					if (so.getTif()==TimeInForceType.MarktOnClose){
						map.put(StockOrderType.sellmarketclose.name(), so);
					}else{
						if (so.getOrderType()==OrderType.stoplimit){
							map.put(StockOrderType.sellstop.name(), so);
						}else if (so.getOrderType()==OrderType.stoptrailingpercentage){
							map.put(StockOrderType.sellstop.name(), so);
						}else if (so.getOrderType()==OrderType.limit){
							map.put(StockOrderType.selllimit.name(), so);
						}
					}
				}
			}
		}
		return map;
	}
	
	public static Map<String, StockOrder> genStockOrderMap(SelectCandidateResult scr, SellStrategy ss, int cashAmount){
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
	
	public void applySplitDiv(Date dt){
		logger.info("applySplitDiv!");
		List<DivSplit> dsList = StrategyPersistMgr.getTodayDiv(dbConf, dt);
		dsList.addAll(StrategyPersistMgr.getTodaySplit(dbConf, dt));
		for (DivSplit div:dsList){
			List<TradeStrategy> tsl = getTsl(div.getSymbol());
			for (TradeStrategy ts: tsl){
				ts.getBs().xdivDay(div, dbConf);
			}
		}
	}
	
	public void initEngine(){
		Map<String, OrderStatus> osmap = tradeConnector.getOrderStatus();
		if (osmap!=null){
			for (String id:osmap.keySet()){
				OrderStatus os = osmap.get(id);
				if (os.getSide().equals(FixmlConst.Side_Buy) &&
						os.getStat().equals(OrderStatus.OPEN)){
					//for all buy order submitted in pending state, add the MonitorBuyOrderTrdMsg
					StockPosition sp = TradePersistMgr.getStockPositionByOrderId(this.getDbConf(), os.getOrderId());
					if (sp!=null){
						TradeMsg mboMsg = new MonitorBuyOrderTrdMsg(os.getOrderId(), sp.getScr(), sp.getBsName(), sp.getSoMap());
						addMsg(mboMsg);
					}else{
						logger.error(String.format("stock position for %s not found.", os.getOrderId()));
					}
				}else if (os.getSide().equals(FixmlConst.Side_Sell) &&
						os.getStat().equals(OrderStatus.OPEN) &&
						os.getTyp().equals(FixmlConst.Typ_StopLimit) || os.getTyp().equals(FixmlConst.Typ_StopTrailing) || os.getTyp().equals(FixmlConst.Typ_Stop)){
					//for all stop sell order submitted, add the MonitorSellPriceTrdMsg
					StockPosition sp = TradePersistMgr.getStockPositionByOrderId(this.getDbConf(), os.getOrderId());
					if (sp!=null){
						StockOrder limitSellOrder = sp.getSoMap().get(StockOrderType.selllimit.name());
						if (limitSellOrder!=null){
							TradeMsg msoMsg = new MonitorSellPriceTrdMsg(limitSellOrder.getSymbol(), limitSellOrder.getLimitPrice(), sp.getScr(), sp.getBsName(), sp.getSoMap());
							addMsg(msoMsg);
						}else{
							logger.error(String.format("limit sell order not found in somap:%s", sp.getSoMap()));
						}
						TradeMsg mssoMsg = new MonitorSellStopOrderTrdMsg(os.getOrderId(), sp.getScr(), sp.getBsName(), sp.getSoMap());
						addMsg(mssoMsg);
					}else{
						logger.error(String.format("stock position for %s not found.", os.getOrderId()));
					}
				}else if (!os.getStat().equals(OrderStatus.FILLED)){
					logger.info(String.format("order not filled but can't be processed: %s", os));
				}
			}
		}else{
			logger.error("order status map is null.");
		}
	}
	
	private static final int PROCESS_INTERVAL=2000;
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
			try{
				if (curMst==MarketStatusType.Regular){
					//check msgs changed to output log
					boolean changedMsg=false;
					if (prevMsgIds.size()!=getMsgMap().size()){
						changedMsg = true;
					}else{
						for (String msgId:prevMsgIds){
							if (!getMsgMap().containsKey(msgId)){
								changedMsg = true;
								break;
							}
						}
					}
					if (changedMsg){
						logger.info(String.format("%d messages to process.", getMsgMap().size()));
						logger.info(String.format("process msg: %s",getMsgMap().values()));
					}
					//store prev msg ids
					prevMsgIds.clear();
					for (String msgId:getMsgMap().keySet()){
						prevMsgIds.add(msgId);
					}
					List<TradeMsgPR> tmprlist = new ArrayList<TradeMsgPR>();
					List<String> keys = getMsgMapKeys();
					for (String key:keys){
						TradeMsg msg = getMsg(key);
						if (msg!=null){
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
				}else{
					//do nothing for none-regular market now.
				}
				Thread.sleep(PROCESS_INTERVAL);
			}catch(Throwable t){//prevent the system from stop for unknown error
				logger.error("", t);
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
	        
	        Trigger preMarketOpenTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(MarketOpenCloseEvtType.preMarketOpen.toString(), TradeMsgType.marketOpenClose.toString()).forJob(genMarketMsgJob).
	        		withSchedule(CronScheduleBuilder.cronSchedule(at.getPreMarketOpenCron()).inTimeZone(TimeZone.getTimeZone("EST"))).
	        		build();
	        Trigger preMarketCloseTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(MarketOpenCloseEvtType.preMarketClose.toString(), TradeMsgType.marketOpenClose.toString()).forJob(genMarketMsgJob).
	        		withSchedule(CronScheduleBuilder.cronSchedule(at.getPreMarketCloseCron()).inTimeZone(TimeZone.getTimeZone("EST"))).
	        		build();
	        Trigger regularMarketOpenTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(MarketOpenCloseEvtType.regularMarketOpen.toString(), TradeMsgType.marketOpenClose.toString()).forJob(genMarketMsgJob).
	        		withSchedule(CronScheduleBuilder.cronSchedule(at.getRegularMarketOpenCron()).inTimeZone(TimeZone.getTimeZone("EST"))).
	        		build();
	        Trigger regularMarketCloseTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(MarketOpenCloseEvtType.regularMarketClose.toString(), TradeMsgType.marketOpenClose.toString()).forJob(genMarketMsgJob).
	        		withSchedule(CronScheduleBuilder.cronSchedule(at.getRegularMarketClosedCron()).inTimeZone(TimeZone.getTimeZone("EST"))).
	        		build();
	        Trigger afterMarketOpenTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(MarketOpenCloseEvtType.afterMarketOpen.toString(), TradeMsgType.marketOpenClose.toString()).forJob(genMarketMsgJob).
	        		withSchedule(CronScheduleBuilder.cronSchedule(at.getAfterHourMarketOpenCron()).inTimeZone(TimeZone.getTimeZone("EST"))).
	        		build();
	        Trigger afterMarketCloseTrigger = TriggerBuilder.newTrigger().
	        		withIdentity(MarketOpenCloseEvtType.afterMarketClose.toString(), TradeMsgType.marketOpenClose.toString()).forJob(genMarketMsgJob).
	        		withSchedule(CronScheduleBuilder.cronSchedule(at.getAfterHourMarketCloseCron()).inTimeZone(TimeZone.getTimeZone("EST"))).
	        		build();
	        
	        scheduler.addJob(genMarketMsgJob, true); 
	        scheduler.scheduleJob(preMarketOpenTrigger);
	        scheduler.scheduleJob(preMarketCloseTrigger);
	        scheduler.scheduleJob(regularMarketOpenTrigger);
	        scheduler.scheduleJob(regularMarketCloseTrigger);
	        scheduler.scheduleJob(afterMarketOpenTrigger);
	        scheduler.scheduleJob(afterMarketCloseTrigger);
	        scheduler.start();
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	private static String getHM(String cronExp){
		String[] ele = cronExp.split(" ");
		String min = ele[1];
		String hour = ele[2];
		if (hour.length()==1){
			hour = "0" + hour;
		}
		if (min.length()==1){
			min = "0"+min;
		}
		return String.format("%s:%s", hour, min);
	}
	
	public static MarketStatusType getMarketStatus(AutoTrader at){
		String preMarketOpenHM = getHM(at.getPreMarketOpenCron());
		String preMarketCloseHM = getHM(at.getPreMarketCloseCron());
		String regularMarketOpenHM = getHM(at.getRegularMarketOpenCron());
		String regularMarketCloseHM = getHM(at.getRegularMarketClosedCron());
		String afterMarketOpenHM = getHM(at.getAfterHourMarketOpenCron());
		String afterMarketCloseHM = getHM(at.getAfterHourMarketCloseCron());
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("EST"));
		cal.setTime(new Date());
		int dow = cal.get(Calendar.DAY_OF_WEEK);
		if (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY){
			return MarketStatusType.Close;
		}
		SimpleDateFormat hmSdf = new SimpleDateFormat("HH:mm");
		hmSdf.setTimeZone(TimeZone.getTimeZone("EST"));
		String hourMinute = hmSdf.format(cal.getTime());
		//[,)
		if (StringUtil.inRange(preMarketOpenHM, preMarketCloseHM, hourMinute)){
			return MarketStatusType.Pre;
		}else if (StringUtil.inRange(regularMarketOpenHM, regularMarketCloseHM, hourMinute)){
			return MarketStatusType.Regular;
		}else if (StringUtil.inRange(afterMarketOpenHM, afterMarketCloseHM, hourMinute)){
			return MarketStatusType.After;
		}else{
			return MarketStatusType.Close;
		}
	}
	
	private List<Thread> streamMgrList = new ArrayList<Thread>();
	
	//using steam feeding or quote query
	private List<Thread> getAndStartStreamMgrs(boolean useStream){
		List<Thread> tl = new ArrayList<Thread>();
		List<String> symbolList = new ArrayList<String>();
        symbolList.addAll(symbols);
        int total=0;
		int totalDataThread = symbolList.size()/StreamMgr.REQUEST_MAX_SYMBOLS + 1;
		while (total<symbolList.size()){
			int end = Math.min(total+StreamMgr.REQUEST_MAX_SYMBOLS, symbolList.size());
			List<String> sl = symbolList.subList(total, end);
			Runnable streamMgr = null;
			if (useStream){
				streamMgr = new StreamMgr(sl, (TradeKingConnector) getTm(), getTradeDataMgr(), getProxyConf());
			}else{
				streamMgr = new StreamSimulator(sl, (TradeKingConnector) getTm(), getTradeDataMgr(), totalDataThread, true);
			}
			Thread t = new Thread(streamMgr);
			tl.add(t);
			t.start();
			total=end;
		}
		return tl;
	}
	
	public void startStreamMgr(MarketStatusType mst){
		if (mst == MarketStatusType.Close){
			for (Thread t: streamMgrList){
				t.interrupt();
			}
			streamMgrList.clear();
		}else if (mst == MarketStatusType.Regular){
			for (Thread t: streamMgrList){
				t.interrupt();
			}
			streamMgrList.clear();
			List<Thread> tl = getAndStartStreamMgrs(true);
			streamMgrList.addAll(tl);
		}else if (mst == MarketStatusType.Pre || mst == MarketStatusType.After){
			for (Thread t: streamMgrList){
				t.interrupt();
			}
			streamMgrList.clear();
			List<Thread> tl = getAndStartStreamMgrs(false);
			streamMgrList.addAll(tl);
		}else{
			logger.error(String.format("unsupported market status type: %s", mst));
		}
	}
	
	public static void main(String[] args){
		try{
			AutoTrader at = new AutoTrader();
			//
			at.initEngine();
			new Thread(at).start();
			//
			MarketStatusType mst = getMarketStatus(at);
			at.setCurMst(mst);
			logger.info(String.format("market status is %s", mst));
			at.startStreamMgr(mst);
	        //
	        HistoryDumpMgr hdm = new HistoryDumpMgr(at.getHistoryDumpProperties(), at.getTradeDataMgr(), at.getSc());
	        new Thread(hdm).start();
	        //
	        AutoTrader.startScheduler(at);
	        //mgmt
	        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
	        ObjectName atName = new ObjectName("stock.trade:type=AutoTrader");
	        mbs.registerMBean(at, atName);
	        ObjectName hdName = new ObjectName("stock.trade:type=HistoryDump");
	        mbs.registerMBean(hdm, hdName);
	        //
	        while(true){
	        	Thread.sleep(10000);
	        }
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public boolean isExtendedHour(){
		return isExtendedHour(this.curMst);
	}
	
	public static boolean isExtendedHour(MarketStatusType mst){
    	if (mst == MarketStatusType.After || mst == MarketStatusType.Pre){
    		return true;
    	}else{
    		return false;
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
	public boolean isPreview() {
		return isPreview;
	}
	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}
	public TradeApi getTm() {
		return tradeConnector;
	}
	public void setTm(TradeApi tradeApi) {
		tradeConnector=tradeApi;
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
	public String getRegularMarketOpenCron() {
		return regularMarketOpenCron;
	}
	public void setRegularMarketOpenCron(String regularMarketOpenCron) {
		this.regularMarketOpenCron = regularMarketOpenCron;
	}
	public String getRegularMarketClosedCron() {
		return regularMarketClosedCron;
	}
	public void setRegularMarketClosedCron(String regularMarketClosedCron) {
		this.regularMarketClosedCron = regularMarketClosedCron;
	}
	public String getPreMarketOpenCron() {
		return preMarketOpenCron;
	}
	public void setPreMarketOpenCron(String preMarketOpenCron) {
		this.preMarketOpenCron = preMarketOpenCron;
	}
	public String getPreMarketCloseCron() {
		return preMarketCloseCron;
	}
	public void setPreMarketCloseCron(String preMarketCloseCron) {
		this.preMarketCloseCron = preMarketCloseCron;
	}
	public String getAfterHourMarketOpenCron() {
		return afterHourMarketOpenCron;
	}
	public void setAfterHourMarketOpenCron(String afterHourMarketOpenCron) {
		this.afterHourMarketOpenCron = afterHourMarketOpenCron;
	}
	public String getAfterHourMarketCloseCron() {
		return afterHourMarketCloseCron;
	}
	public void setAfterHourMarketCloseCron(String afterHourMarketCloseCron) {
		this.afterHourMarketCloseCron = afterHourMarketCloseCron;
	}
	public Map<String, TreeMap<IntervalUnit, List<TradeStrategy>>> getSiutsMap() {
		return siutsMap;
	}
	public void setSiutsMap(Map<String, TreeMap<IntervalUnit, List<TradeStrategy>>> siutsMap) {
		this.siutsMap = siutsMap;
	}
	public StockConfig getSc() {
		return sc;
	}
	public void setSc(StockConfig sc) {
		this.sc = sc;
	}
	public TradeDataMgr getTradeDataMgr() {
		return tradeDataMgr;
	}
	public void setTradeDataMgr(TradeDataMgr tradeDataMgr) {
		this.tradeDataMgr = tradeDataMgr;
	}
	public CrawlConf getCconf() {
		return cconf;
	}
	public void setCconf(CrawlConf cconf) {
		this.cconf = cconf;
	}
	public String getCrawlconfProperties() {
		return crawlconfProperties;
	}
	public void setCrawlconfProperties(String crawlconfProperties) {
		this.crawlconfProperties = crawlconfProperties;
	}

	public MarketStatusType getCurMst() {
		return curMst;
	}

	public void setCurMst(MarketStatusType curMst) {
		this.curMst = curMst;
	}

}
