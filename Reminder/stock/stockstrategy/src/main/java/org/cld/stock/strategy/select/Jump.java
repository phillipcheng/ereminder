package org.cld.stock.strategy.select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cld.stock.common.CandleQuote;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.indicator.EMA;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;

public class Jump extends SelectStrategy {
	public static final String Ind_EMA="ema";

	public static final String Jump_Ratio="scs.param.jumpRatio";
	public static final String NegRatio_Threshold="scs.param.numNegEmaRatioThreshold";
	private static final int Ratio_ListSize=10;
	
	float jumpRatio=2f;
	int numNegEmaRatioThreshold=3;
	
	float preV;
	
	//
	EMA ema;
	String emaKey;
	int negEmaRatioNum=0;//number of negative EMA ratio in the past 10 periods
	int posEmaRatioNum=0;
	List<Boolean> emaRatioList = new ArrayList<Boolean>();
	
	public Jump(){
	}
	
	public String toString(){//this is the key of the strategy
		return String.format("Jump: jumpRatio:%.2f, ema:%s, negThreshold:%d", jumpRatio, emaKey, numNegEmaRatioThreshold);
	}
	
	@Override
	public void init(){
		super.init();
		ema = (EMA) this.indMap.get(Ind_EMA);
		if (ema!=null){
			emaKey = ema.toKey();
		}
		if (this.params.containsKey(Jump_Ratio)){
			jumpRatio = Float.parseFloat((String) this.params.get(Jump_Ratio));
		}
		if (this.params.containsKey(NegRatio_Threshold)){
			numNegEmaRatioThreshold = (int) Float.parseFloat((String) this.params.get(NegRatio_Threshold));
		}
	}
	
	@Override
	public void cleanup(){
		preV=0;
		if (ema!=null)
			ema.cleanup();
		negEmaRatioNum=0;
		posEmaRatioNum=0;
		emaRatioList.clear();
	}

	private void addRatio(float ratio){
		if (emaRatioList.size()>=Ratio_ListSize){
			emaRatioList.remove(0);
		}
		if (ratio>0){
			emaRatioList.add(true);
		}else{
			emaRatioList.add(false);
		}
	}
	
	private boolean negTrend(){
		if (emaRatioList.size()<Ratio_ListSize){
			return true;
		}else{
			int neg=0;
			for (int i=0; i<emaRatioList.size(); i++){
				if (!emaRatioList.get(i)){
					neg++;
				}
			}
			if (neg>numNegEmaRatioThreshold){
				return true;
			}else{
				return false;
			}
		}
	}
	
	@Override
	public SelectCandidateResult selectByStream(CqIndicators cqi) {
		CandleQuote cq = cqi.getCq();
		SelectCandidateResult scr=null;
		if (cqi.hasIndicator(emaKey)){
			Map<String, Float> map = (Map<String, Float>) cqi.getIndicator(emaKey);
			float ratio = map.get(EMA.RATIO);
			addRatio(ratio);
		}
		float curV = cq.getClose()-cq.getOpen();
		if (preV!=0 && curV>0 && curV>jumpRatio*Math.abs(preV) &&
				!negTrend()){
			if (this.getLookupUnit() == IntervalUnit.day){
				scr = new SelectCandidateResult(cq.getSymbol(), sc.getNormalTradeStartTime(cq.getStartTime()), 0f, cq.getClose());
			}else if (this.getLookupUnit() == IntervalUnit.minute){
				scr = new SelectCandidateResult(cq.getSymbol(), cq.getStartTime(), 0f, cq.getClose());
			}
			cleanup();//cleanup after a scr generated
		}
		preV = curV;
		//logger.debug(String.format("up:%d, down:%d", numUp, numDown));
		return scr;
	}
}
