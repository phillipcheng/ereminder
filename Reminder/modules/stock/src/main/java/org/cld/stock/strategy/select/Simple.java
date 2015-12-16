package org.cld.stock.strategy.select;

import java.util.Map;

import org.cld.stock.CandleQuote;
import org.cld.stock.CqIndicators;
import org.cld.stock.indicator.Bollinger;
import org.cld.stock.indicator.Indicator;
import org.cld.stock.indicator.MACD;
import org.cld.stock.indicator.RSI;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;

public class Simple extends SelectStrategy {
	public static final String Ind_MACD="macd";
	public static final String Ind_BOLLINGER="bollinger";
	public static final String Ind_RSI="rsi";
	
	//down>up>signal
	private MACD macd;
	private Bollinger bollinger;
	private RSI	rsi;
	
	private String macdKey;
	private String bollingerKey;
	private String rsiKey;
	
	private Map<String, Float> prevBrMap=null;
	
	public Simple(){
	}
	
	@Override
	public void init(){
		super.init();
		macd = (MACD) this.indMap.get(Ind_MACD);
		macdKey = macd.toKey();
		bollinger = (Bollinger) this.indMap.get(Ind_BOLLINGER);
		bollingerKey = bollinger.toKey();
		rsi = (RSI)this.indMap.get(Ind_RSI);
		rsiKey = rsi.toKey();
	}

	@Override
	public SelectCandidateResult selectByStream(CqIndicators cqi) {
		CandleQuote cq = cqi.getCq();
		SelectCandidateResult scr=null;
		if (cqi.hasIndicator(macdKey) && cqi.hasIndicator(bollingerKey)){
			Map<String, Float> macdMap = (Map<String, Float>) cqi.getIndicator(macdKey);
			Map<String, Float> brMap = (Map<String, Float>) cqi.getIndicator(bollingerKey);
			float rsiV = Indicator.V_NA;
			if (cqi.hasIndicator(rsiKey)){
				rsiV = (float) cqi.getIndicator(rsiKey);
			}
			if (macdMap!=null && brMap!=null && rsiV!=Indicator.V_NA){
				float diff = macdMap.get(MACD.DIFF);
				float dea = macdMap.get(MACD.DEA);
				if (diff>0 && diff>dea &&
						prevBrMap!=null && brMap.get(Bollinger.upperBand)>prevBrMap.get(Bollinger.upperBand) &&
						rsiV>70){
					if (this.getLookupUnit() == IntervalUnit.day){
						scr = new SelectCandidateResult(cq.getSymbol(), sc.getNormalTradeStartTime(cq.getStartTime()), 0f, cq.getClose());
					}else if (this.getLookupUnit() == IntervalUnit.minute){
						scr = new SelectCandidateResult(cq.getSymbol(), cq.getStartTime(), 0f, cq.getClose());
					}
				}
				prevBrMap = brMap;
			}
		}
		return scr;
	}
}
