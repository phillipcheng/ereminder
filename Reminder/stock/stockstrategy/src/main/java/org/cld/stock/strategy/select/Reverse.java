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

public class Reverse extends SelectStrategy {
	public static final String Ind_EMA="ema";

	public static final String Down_Threshold="scs.param.numDownThreshold";
	public static final String Up_Threshold="scs.param.numUpThreshold";
	public static final String NegRatio_Threshold="scs.param.numNegEmaRatioThreshold";
	private static final int Ratio_ListSize=20;
	
	int numDownThreshold=3;
	int numUpThreshold=2;
	int numNegEmaRatioThreshold=6;
	
	//
	int numDown=0;
	int numUp=0;
	Boolean preV=null; //up is true, down is false
	
	//
	EMA ema;
	String emaKey;
	int negEmaRatioNum=0;//number of negative EMA ratio in the past 10 periods
	int posEmaRatioNum=0;
	List<Boolean> emaRatioList = new ArrayList<Boolean>();
	
	public Reverse(){
	}
	
	public String toString(){//this is the key of the strategy
		return String.format("Reverse: numDow:%d, numUp:%d, ema:%s, negThreshold:%d", numDownThreshold, numUpThreshold, emaKey, numNegEmaRatioThreshold);
	}
	
	@Override
	public void init(){
		super.init();
		ema = (EMA) this.indMap.get(Ind_EMA);
		emaKey = ema.toKey();
		if (this.params.containsKey(Down_Threshold)){
			numDownThreshold = (int) Float.parseFloat((String) this.params.get(Down_Threshold));
		}
		if (this.params.containsKey(Up_Threshold)){
			numUpThreshold = (int) Float.parseFloat((String) this.params.get(Up_Threshold));
		}
		if (this.params.containsKey(NegRatio_Threshold)){
			numNegEmaRatioThreshold = (int) Float.parseFloat((String) this.params.get(NegRatio_Threshold));
		}
	}
	
	@Override
	public void cleanup(){
		numDown=0;
		numUp=0;
		preV=null;
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
		boolean curV = cq.getClose()>cq.getOpen();
		if (preV==null){//init
			if (curV){
				numUp=1;
				numDown=0;
			}else{
				numDown=1;
				numUp=0;
			}
		}else{
			if (curV){//up
				if (preV){//preV is up
					numUp++;
				}else{//preV is down
					numUp=1;
				}
				if (numUp>=numUpThreshold && numDown>=numDownThreshold && //reverse signal occur
						!negTrend()){//not in negtive trend
					if (this.getLookupUnit() == IntervalUnit.day){
						scr = new SelectCandidateResult(cq.getSymbol(), sc.getNormalTradeStartTime(cq.getStartTime()), 0f, cq.getClose());
					}else if (this.getLookupUnit() == IntervalUnit.minute){
						scr = new SelectCandidateResult(cq.getSymbol(), cq.getStartTime(), 0f, cq.getClose());
					}
					cleanup();//cleanup after a scr generated
				}
			}else{//down
				if (preV){//preV is up
					numDown=1;
				}else{//preV is down
					numDown++;
				}
			}
		}
		preV = curV;
		//logger.debug(String.format("up:%d, down:%d", numUp, numDown));
		return scr;
	}
}
