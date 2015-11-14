package org.cld.trade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.trade.StockOrder;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.OrderResponse;
import org.cld.trade.response.Quote;
import org.cld.util.DateTimeUtil;

public class MarketOpenTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(MarketOpenTrdMsg.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private SelectStrategy bs;
	private SellStrategy ss;
	private boolean useLast=false;
	private String symbol;
	private boolean real=false;
	
	public MarketOpenTrdMsg() {
		super(TradeMsgType.marketOpenSoon);
	}
	public MarketOpenTrdMsg(boolean real, boolean useLast, SelectStrategy bs, SellStrategy ss){
		this();
		this.real=real;
		this.useLast = useLast;
		this.bs=bs;
		this.ss=ss;
	}
	
	public SelectStrategy getBs() {
		return bs;
	}
	public void setBs(SelectStrategy bs) {
		this.bs = bs;
	}
	public SellStrategy getSs() {
		return ss;
	}
	public void setSs(SellStrategy ss) {
		this.ss = ss;
	}

	/**
	 *  [market will open]
	 * 		|__ no position, apply alg a buy order submitted (Day), 1 msg added (monitor buy order)
	 *      |__ has position, do nothing.
	 */
	@Override
	public TradeMsgPR process(TradeMgr tm) {
		StockConfig sc = StockUtil.getStockConfig(tm.getBaseMarketId());
		Date checkDate = StockUtil.getLastOpenDay(DateTimeUtil.yesterday(new Date()), sc.getHolidays());
		List<StockPosition> sp = TradePersistMgr.getOpenPosition(tm.getCconf().getSmalldbconf(), checkDate);
		TradeMsgPR tmpr = new TradeMsgPR();
		if (sp.size()>0){
			logger.info(String.format("has opened position %s.", sp.get(0)));
			tmpr.setExecuted(true);
			return tmpr;
		}else{
			SelectCandidateResult scr = null;
			if (symbol==null){
				//submit 1 limit buy order, submit 1 monitor order msg
				List<Quote> ql = tm.getMarketAllQuotes(tm.getBaseMarketId(), tm.getMarketId());
				int openNum=0;
				for (Quote q: ql){
					if (q.getOpen()>0){
						openNum++;
					}
				}
				logger.info(String.format("market size:%d, open size:%d", ql.size(), openNum));
				if (openNum>(ql.size()*0.5) && ql.size()>5800){
					scr = tm.applySelectStrategy(ql, tm.getBaseMarketId(), tm.getMarketId(), useLast, bs);
				}
			}else{
				//buy specified symbol
				List<Quote> ql = tm.getQuotes(new String[]{symbol});
				if (ql!=null && ql.size()==1){
					Quote q = ql.get(0);
					scr = new SelectCandidateResult(symbol, sdf.format(new Date()), 0f, q.getLast());
				}
			}
			if (scr!=null){
				Map<StockOrderType, StockOrder> somap = AutoTrader.genStockOrderMap(scr, ss, tm.getUseAmount());
				StockOrder buyOrder = somap.get(StockOrderType.buy);
				OrderResponse or = AutoTrader.trySubmit(tm, buyOrder, real);
				if (OrderResponse.SUCCESS.equals(or.getError())){
					List<TradeMsg> tml = new ArrayList<TradeMsg>();
					TradeMsg mbo = new MonitorBuyOrderTrdMsg(StockOrderType.buy, or.getClientorderid(), somap);
					StockPosition trySp = new StockPosition(buyOrder, StockPosition.tryOpen, or.getClientorderid());
					TradePersistMgr.tryPosition(tm.getCconf().getSmalldbconf(), trySp);
					tml.add(mbo);//buy order submitted, monitor buy order msg generated
					tmpr.setExecuted(true);
					tmpr.setNewMsgs(tml);
					return tmpr;
				}else{
					//TODO error handling
					logger.error(String.format("buy error: buy order: %s, response: %s", buyOrder, or));
					tmpr.setExecuted(true);
					return tmpr;
				}
			}
			return tmpr;//opp not found
		}
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
