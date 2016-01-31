package org.cld.stock.strategy.select;

import java.util.Map;

import org.cld.stock.common.CandleQuote;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.indicator.Bollinger;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;

public class BollingerCross extends SelectStrategy {

	public static final String Ind_BOLLINGER="bollinger";
	
	private Bollinger bollinger;
	private String bollingerKey;
	
	private Map<String, Float> prevBrMap=null;
	
	public BollingerCross(){
	}
	
	public String toString(){
		return String.format("BollingerCross: bollinger:%s", bollinger.toKey());
	}
	
	@Override
	public void init(){
		super.init();
		bollinger = (Bollinger) this.indMap.get(Ind_BOLLINGER);
		bollingerKey = bollinger.toKey();
	}
	
	@Override
	public void cleanup(){
		bollinger.cleanup();
		prevBrMap=null;
	}

	@Override
	public SelectCandidateResult selectByStream(CqIndicators cqi) {
		CandleQuote cq = cqi.getCq();
		SelectCandidateResult scr=null;
		if (cqi.hasIndicator(bollingerKey)){
			Map<String, Float> brMap = (Map<String, Float>) cqi.getIndicator(bollingerKey);
			if (brMap!=null && prevBrMap!=null){
				float preV=prevBrMap.get(Bollinger.middleBand);
				float v = brMap.get(Bollinger.middleBand);
				float lowV = brMap.get(Bollinger.lowerBand);
				float price = cq.getClose();
				if (v>=preV //upper trend
						&& price<=lowV //pull back
						){
					if (this.getLookupUnit() == IntervalUnit.day){
						scr = new SelectCandidateResult(cq.getSymbol(), sc.getNormalTradeStartTime(cq.getStartTime()), 0f, cq.getClose());
					}else if (this.getLookupUnit() == IntervalUnit.minute){
						scr = new SelectCandidateResult(cq.getSymbol(), cq.getStartTime(), 0f, cq.getClose());
					}
				}
			}
		}
		return scr;
	}
}
