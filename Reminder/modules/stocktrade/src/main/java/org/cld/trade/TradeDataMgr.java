package org.cld.trade;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.CandleQuote;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.common.TradeTick;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.TradeStrategy;
import org.cld.trade.evt.BuyOppTrdMsg;

//this class will be shared in multiple threads
public class TradeDataMgr {
	private static Logger logger =  LogManager.getLogger(TradeDataMgr.class);
	
	Map<IntervalUnit, Map<String, CandleQuote>> currentCqMap;
	
	Map<String, Deque<TradeTick>> historyTickMap = new ConcurrentHashMap<String, Deque<TradeTick>>();
	Map<IntervalUnit, Map<String, Deque<CandleQuote>>> historyCqMap = new ConcurrentHashMap<IntervalUnit, Map<String, Deque<CandleQuote>>>();

	AutoTrader at;
	StockConfig sc;
	
	public TradeDataMgr(AutoTrader at, StockConfig sc){
		this.at = at;
		this.sc = sc;
		currentCqMap = new TreeMap<IntervalUnit, Map<String, CandleQuote>>();
		IntervalUnit iu = IntervalUnit.tick;
		while (true){
			iu = iu.next();
			if (iu==IntervalUnit.unspecified)
				break;
			Map<String, CandleQuote> scq = new ConcurrentHashMap<String, CandleQuote>();
			currentCqMap.put(iu, scq);
		}
	}
	
	public Map<String, Deque<TradeTick>> getHistoryTickMap() {
		return historyTickMap;
	}
	public Map<IntervalUnit, Map<String, Deque<CandleQuote>>> getHistoryCqMap() {
		return historyCqMap;
	}
	private void addHistoryCq(CandleQuote cq, IntervalUnit iu, String symbol){
		Map<String, Deque<CandleQuote>> hcqMap = historyCqMap.get(iu);
		if (hcqMap==null){
			hcqMap = new HashMap<String, Deque<CandleQuote>>();
			historyCqMap.put(iu, hcqMap);
		}
		Deque<CandleQuote> cqq = hcqMap.get(symbol);
		if (cqq==null){
			cqq = new ConcurrentLinkedDeque<CandleQuote>();
			hcqMap.put(symbol, cqq);
		}
		cqq.add(cq);
	}
	
	private void applyStrategy(List<TradeStrategy> atsl, CandleQuote newCq){
		if (atsl!=null){
			for (TradeStrategy ts: atsl){
				CqIndicators cqi = CqIndicators.addIndicators(newCq, ts.getBs());
				SelectCandidateResult scr = ts.getBs().selectByStream(cqi);
				if (scr!=null){
					BuyOppTrdMsg trdmsg = new BuyOppTrdMsg(ts, scr);
					at.addMsg(trdmsg);
					logger.debug(String.format("opp found:%s", trdmsg));
				}
			}
		}
	}
	//generate msg to at if the tt triggers minute data/day data to generate opptunity
	public void accept(String symbol, TradeTick tt){
		//add tt to historyTickMap
		Deque<TradeTick> ttq = historyTickMap.get(symbol);
		if (ttq==null){
			ttq = new ConcurrentLinkedDeque<TradeTick>();
			historyTickMap.put(symbol, ttq);
		}
		ttq.add(tt);
		CandleQuote cq = new CandleQuote(tt);
		cq.setSymbol(symbol);
		//try buy strategy requires tick
		List<TradeStrategy> tsl = at.getTsl(symbol, IntervalUnit.tick);
		if (tsl!=null){
			applyStrategy(tsl, cq);
		}
		
		for (IntervalUnit iu: currentCqMap.keySet()){
			Map<String, CandleQuote> ccqMap = currentCqMap.get(iu);
			CandleQuote currentCq = ccqMap.get(symbol);
			if (currentCq==null){//1st cq
				CandleQuote newCq = StockUtil.aggregate(currentCq, cq, iu, sc);
				ccqMap.put(symbol, newCq);
			}else{
				List<TradeStrategy> atsl = at.getTsl(symbol, iu);
				CandleQuote newCq = StockUtil.aggregate(currentCq, cq, iu, sc);
				if (newCq!=null){
					applyStrategy(atsl, newCq);
					ccqMap.put(symbol, newCq);
					addHistoryCq(currentCq, iu, symbol);
				}
			}
		}
	}
}
