package org.cld.stock.strategy.select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cld.stock.CandleQuote;
import org.cld.stock.CandleQuoteDS;
import org.cld.stock.CqIndicators;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DataMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class WShape extends SelectStrategy {

	public static final String LOOKUP_PERIOD="scs.param.luperiod";
	public static final String AMPLITUDE="scs.param.amplitude";
	
	private int periods;
	private float ampThresh;
	private List<CandleQuote> cqlist;
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
	public void initData(Map<DataMapper, List<? extends Object>> resultMap){
		List<CqIndicators> cqilist = (List<CqIndicators>) resultMap.get(this.quoteMapper());
		for (CqIndicators cqi:cqilist){
			cqlist.add(cqi.getCq());
		}
	}
	
	@Override
	public int maxLookupNum() {
		return periods;
	}
	
	@JsonIgnore
	@Override
	public DataMapper[] getDataMappers() {
		return new DataMapper[]{super.quoteMapper()};
	}
	
	//using the close of current cq, then submit date has to be next trading day
	@Override
	public List<SelectCandidateResult> selectByHistory(Map<DataMapper, List<? extends Object>> tableResults) {
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<CandleQuote> lo = null;
		if (super.quoteMapper().oneFetch()){
			lo = cqlist;
		}else{
			lo = new ArrayList<CandleQuote>();
			List<CqIndicators> cqilist = (List<CqIndicators>) tableResults.get(this.quoteMapper());
			for (CqIndicators cqi:cqilist){
				lo.add(cqi.getCq());
			}
		}
		int idx=0;//
		while (cqds.size()<periods){
			if (idx<lo.size()){
				cqds.add((CandleQuote)lo.get(idx));
				idx++;
			}else{
				break;
			}
		}
		if (cqds.size()>=periods){
			while (idx<lo.size()){
				CandleQuote currentCq = cqds.getLast();
				CandleQuote prevCq = cqds.getPrev(currentCq);
				CandleQuote maxCq = cqds.getCq(prevCq.getStartTime(), true);//get max
				CandleQuote minCq = cqds.getCq(maxCq.getStartTime(), false);//get min
				if (maxCq!=null && minCq!=null){//checkValid(prevCq) && 
					float amplitude = (maxCq.getClose()-minCq.getClose())/(maxCq.getClose()+minCq.getClose());
					if (amplitude>ampThresh*0.01f && prevCq.getClose()<minCq.getClose() && currentCq.getOpen()>prevCq.getClose()){
						logger.debug(String.format("amp:%.4f,\n min:%s,\n max:%s,\n prev:%s,\n cur:%s", amplitude, minCq, maxCq, prevCq, currentCq));
						if (this.getLookupUnit() == IntervalUnit.day){
							scrl.add(new SelectCandidateResult(sc.getNormalTradeStartTime(currentCq.getStartTime()), 0f));
						}else if (this.getLookupUnit() == IntervalUnit.minute){
							scrl.add(new SelectCandidateResult(currentCq.getStartTime(), 0f));
						}
					}
				}
				//
				CandleQuote nextCq = (CandleQuote) lo.get(idx);
				cqds.add(nextCq);
				cqds.removeMin();
				idx++;
			}
			return scrl;
		}else{
			return null;
		}

	}
}
