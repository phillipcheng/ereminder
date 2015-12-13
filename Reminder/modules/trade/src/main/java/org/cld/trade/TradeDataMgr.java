package org.cld.trade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.cld.stock.CandleQuote;
import org.cld.stock.CqIndicators;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.TradeTick;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.TradeStrategy;
import org.cld.trade.evt.BuyOppTrdMsg;

//this class will be shared in multiple threads
public class TradeDataMgr {
	Map<IntervalUnit, Map<String, CandleQuote>> currentCqMap;
	
	Map<String, Queue<TradeTick>> historyTickMap = new ConcurrentHashMap<String, Queue<TradeTick>>();
	Map<IntervalUnit, Map<String, Queue<CandleQuote>>> historyCqMap = new HashMap<IntervalUnit, Map<String, Queue<CandleQuote>>>();

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
			Map<String, CandleQuote> scq = new HashMap<String, CandleQuote>();
			currentCqMap.put(iu, scq);
		}
	}
	
	public Map<String, Queue<TradeTick>> getHistoryTickMap() {
		return historyTickMap;
	}
	public Map<IntervalUnit, Map<String, Queue<CandleQuote>>> getHistoryCqMap() {
		return historyCqMap;
	}
	private void addHistoryCq(CandleQuote cq, IntervalUnit iu, String symbol){
		Map<String, Queue<CandleQuote>> hcqMap = historyCqMap.get(iu);
		if (hcqMap==null){
			hcqMap = new HashMap<String, Queue<CandleQuote>>();
			historyCqMap.put(iu, hcqMap);
		}
		Queue<CandleQuote> cqq = hcqMap.get(symbol);
		if (cqq==null){
			cqq = new ConcurrentLinkedQueue<CandleQuote>();
			hcqMap.put(symbol, cqq);
		}
		cqq.add(cq);
	}
	
	//generate msg to at if the tt triggers minute data/day data to generate opptunity
	public void accept(String symbol, TradeTick tt){
		//add tt to historyTickMap
		Queue<TradeTick> ttq = historyTickMap.get(symbol);
		if (ttq==null){
			ttq = new ConcurrentLinkedQueue<TradeTick>();
			historyTickMap.put(symbol, ttq);
		}
		ttq.add(tt);
		CandleQuote cq = new CandleQuote(tt);
		//try buy strategy requires tick
		List<TradeStrategy> tsl = at.getTsMap().get(IntervalUnit.tick);
		if (tsl!=null){
			for (TradeStrategy ts: tsl){
				CqIndicators cqi = CqIndicators.addIndicators(cq, ts.getBs());
				SelectCandidateResult scr = ts.getBs().selectByStream(cqi);
				BuyOppTrdMsg trdmsg = new BuyOppTrdMsg(ts, scr);
				at.addMsg(trdmsg);
			}
		}
		for (IntervalUnit iu: currentCqMap.keySet()){
			List<TradeStrategy> atsl = at.getTsMap().get(iu);
			Map<String, CandleQuote> ccqMap = currentCqMap.get(iu);
			if (ccqMap==null){
				ccqMap = new HashMap<String, CandleQuote>();
				currentCqMap.put(iu, ccqMap);
			}
			CandleQuote currentCq = ccqMap.get(symbol);
			if (currentCq==null){//1st cq
				CandleQuote newCq = StockUtil.aggregate(currentCq, cq, iu, sc);
				ccqMap.put(symbol, newCq);
			}else{
				CandleQuote newCq = StockUtil.aggregate(currentCq, cq, iu, sc);
				if (newCq!=null){
					if (atsl!=null){
						for (TradeStrategy ts: atsl){
							CqIndicators cqi = CqIndicators.addIndicators(newCq, ts.getBs());
							SelectCandidateResult scr = ts.getBs().selectByStream(cqi);
							BuyOppTrdMsg trdmsg = new BuyOppTrdMsg(ts, scr);
							at.addMsg(trdmsg);
						}
					}
					ccqMap.put(symbol, newCq);
					addHistoryCq(currentCq, iu, symbol);
				}
			}
		}
	}
	
	public void historyDump(){
		
	}
}
