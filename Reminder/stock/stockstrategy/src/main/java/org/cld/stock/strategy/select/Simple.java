package org.cld.stock.strategy.select;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.CandleQuote;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.indicator.Bollinger;
import org.cld.stock.indicator.Indicator;
import org.cld.stock.indicator.MACD;
import org.cld.stock.indicator.RSI;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;

public class Simple extends SelectStrategy {
	public static Logger logger = LogManager.getLogger(Simple.class);
	
	public static final String Ind_MACD="macd";
	public static final String Ind_BOLLINGER="bollinger";
	public static final String Ind_RSI="rsi";
	public static final String RSI_THRESHOLD="scs.param.rsi.threshold";
	
	private String macdKey;
	private String bollingerKey;
	private String rsiKey;
	
	//down>up>signal
	private MACD macd;
	private Bollinger bollinger;
	private RSI	rsi;
	private float rsiThreshold=69;
	int timeWindow = 10;//unit
	int timeElapsed =0;
	int numOppFound = 0; //num of opp found within the window

	private Map<String, Float> prevBrMap=null;
	
	public Simple(){
	}
	
	public String toString(){
		return String.format("Simple: macd:%s, bollinger:%s, rsi:%s,%.2f", macd.toKey(), bollinger.toKey(), rsi.toKey(), rsiThreshold);
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
		if (this.params.containsKey(RSI_THRESHOLD)){
			rsiThreshold = Float.parseFloat((String) this.params.get(RSI_THRESHOLD));
		}
		logger.info(String.format("rsiThreshold: %.2f", rsiThreshold));
	}
	
	@Override
	public void cleanup(){
		macd.cleanup();
		bollinger.cleanup();
		rsi.cleanup();
		prevBrMap=null;
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
				timeElapsed++;
				if (timeElapsed == timeWindow){
					timeElapsed=0;
					numOppFound=0;
				}
				float diff = macdMap.get(MACD.DIFF);
				float dea = macdMap.get(MACD.DEA);
				if (diff>0 && diff>dea &&
						prevBrMap!=null && brMap.get(Bollinger.upperBand)>prevBrMap.get(Bollinger.upperBand) &&
						rsiV>rsiThreshold){
					if (this.getLookupUnit() == IntervalUnit.day){
						scr = new SelectCandidateResult(cq.getSymbol(), sc.getNormalTradeStartTime(cq.getStartTime()), 0f, cq.getClose());
					}else if (this.getLookupUnit() == IntervalUnit.minute){
						if (numOppFound==0){
							timeElapsed=0;
						}
						if (numOppFound==1){//only output opp when this is the 2nd with the time window
							scr = new SelectCandidateResult(cq.getSymbol(), cq.getStartTime(), 0f, cq.getClose());
						}
						numOppFound++;
					}
				}
				prevBrMap = brMap;
			}
		}
		return scr;
	}
}
