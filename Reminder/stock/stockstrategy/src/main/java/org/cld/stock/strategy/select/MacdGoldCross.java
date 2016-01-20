package org.cld.stock.strategy.select;

import java.util.Map;

import org.cld.stock.common.CandleQuote;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.indicator.Indicator;
import org.cld.stock.indicator.MACD;
import org.cld.stock.indicator.RSI;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;

public class MacdGoldCross extends SelectStrategy {
	public static final String Ind_MACD="macd";
	public static final String Ind_RSI="rsi";
	
	//down>up>signal
	private MACD macd;
	private RSI	rsi;
	
	private String macdKey;
	private String rsiKey;
	
	private float prevMacd = 0;
	
	public MacdGoldCross(){
	}
	
	@Override
	public void init(){
		super.init();
		macd = (MACD) this.indMap.get(Ind_MACD);
		macdKey = macd.toKey();
		rsi = (RSI)this.indMap.get(Ind_RSI);
		rsiKey = rsi.toKey();
	}
	
	@Override
	public void cleanup(){
		macd.cleanup();
		rsi.cleanup();
		prevMacd=0;
	}

	@Override
	public SelectCandidateResult selectByStream(CqIndicators cqi) {
		CandleQuote cq = cqi.getCq();
		SelectCandidateResult scr=null;
		if (cqi.hasIndicator(macdKey)){
			Map<String, Float> macdMap = (Map<String, Float>) cqi.getIndicator(macdKey);
			float rsiV = Indicator.V_NA;
			if (cqi.hasIndicator(rsiKey)){
				rsiV = (float) cqi.getIndicator(rsiKey);
			}
			if (macdMap!=null && rsiV!=Indicator.V_NA){
				float macd = macdMap.get(MACD.MACD);
				if (macd>0 && prevMacd<0) { 
						//&& rsiV>70){
					if (this.getLookupUnit() == IntervalUnit.day){
						scr = new SelectCandidateResult(cq.getSymbol(), sc.getNormalTradeStartTime(cq.getStartTime()), 0f, cq.getClose());
					}else if (this.getLookupUnit() == IntervalUnit.minute){
						scr = new SelectCandidateResult(cq.getSymbol(), cq.getStartTime(), 0f, cq.getClose());
					}
				}
				prevMacd=macd;
			}
		}
		return scr;
	}
}
