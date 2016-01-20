package org.cld.stock.strategy.select;

import org.cld.stock.common.CandleQuote;
import org.cld.stock.common.CandleQuoteDS;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;

public class WShape extends SelectStrategy {

	public static final String LOOKUP_PERIOD="scs.param.luperiod";
	public static final String AMPLITUDE="scs.param.amplitude";
	
	private int periods;
	private float ampThresh;
	CandleQuoteDS cqds = new CandleQuoteDS();
	
	public WShape(){
	}
	
	@Override
	public void init(){
		super.init();
		periods = (int) Float.parseFloat((String) this.getParams().get(LOOKUP_PERIOD));
		ampThresh = Float.parseFloat((String) this.getParams().get(AMPLITUDE));
	}
	
	@Override
	public void cleanup(){
		cqds.cleanup();
	}

	@Override
	public SelectCandidateResult selectByStream(CqIndicators cqi) {
		SelectCandidateResult scr = null;
		cqds.add(cqi.getCq());
		if (cqds.size()>periods)
			cqds.removeMin();
		if (cqds.size()>=periods){
			CandleQuote currentCq = cqds.getLast();
			CandleQuote prevCq = cqds.getPrev(currentCq);
			CandleQuote maxCq = cqds.getCq(prevCq.getStartTime(), true);//get max
			CandleQuote minCq = cqds.getCq(maxCq.getStartTime(), false);//get min
			if (maxCq!=null && minCq!=null){//checkValid(prevCq) && 
				float amplitude = (maxCq.getClose()-minCq.getClose())/(maxCq.getClose()+minCq.getClose());
				if (amplitude>ampThresh*0.01f && prevCq.getClose()<minCq.getClose() && currentCq.getOpen()>prevCq.getClose()){
					logger.debug(String.format("amp:%.4f,\n min:%s,\n max:%s,\n prev:%s,\n cur:%s", amplitude, minCq, maxCq, prevCq, currentCq));
					if (this.getLookupUnit() == IntervalUnit.day){
						scr = new SelectCandidateResult(currentCq.getSymbol(), sc.getNormalTradeStartTime(currentCq.getStartTime()), 
								0f, currentCq.getClose());
					}else if (this.getLookupUnit() == IntervalUnit.minute){
						scr = new SelectCandidateResult(currentCq.getSymbol(), currentCq.getStartTime(), 0f, currentCq.getClose());
					}
				}
			}
		}
		return scr;
	}
}
