package org.cld.stock.strategy.select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cld.stock.AnnounceTime;
import org.cld.stock.CandleQuote;
import org.cld.stock.CqIndicators;
import org.cld.stock.QEarnEvent;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DataMapper;
import org.cld.util.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

//TODO
public class EarnD extends SelectStrategy {

	public static final String threshold="scs.param.threshold";
	
	public EarnD(){
	}

	TreeMap<Date, CandleQuote> cqMap = new TreeMap<Date, CandleQuote>();
	List<QEarnEvent> elist;
	
	@Override
	public void initData(Map<DataMapper, List<? extends Object>> resultMap){	
		List<? extends Object> fqlist = resultMap.get(super.quoteMapper());
		for (Object cqo:fqlist){
			CqIndicators cqi = (CqIndicators) cqo;
			cqMap.put(cqi.getCq().getStartTime(), cqi.getCq());
		}
		elist = (List<QEarnEvent>) resultMap.get(sc.getEarnTableMapper());
	}
	
	@Override
	public int maxLookupNum() {
		return 0;
	}

	@JsonIgnore
	@Override
	public DataMapper[] getDataMappers() {
		return new DataMapper[]{super.quoteMapper(), sc.getEarnTableMapper()};
	}
	
	//the bigger the better
	private float calcRatio(float lyeps, float eps, float currentPrice){
		float assumePE=10;
		return (currentPrice/assumePE)/(eps-lyeps);
	}
	
	/**
	 * @param tableResults not used. using the one passed by init
	 */
	@Override
	public List<SelectCandidateResult> selectByHistory(Map<DataMapper, List<? extends Object>> tableResults) {
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		
		for (int i=0; i<elist.size(); i++){
			QEarnEvent qev = elist.get(i);
			Date submitD = cqMap.ceilingKey(qev.getPubDt());//>=
			int diffDays = DateTimeUtil.DateDiff(submitD, qev.getPubDt());
			if (diffDays<10){//earning date and trading day gap
				if (submitD==qev.getPubDt()){
					if (qev.getAt()==AnnounceTime.afterMarket){
						submitD = cqMap.ceilingKey(DateTimeUtil.tomorrow(submitD));
					}
				}
				Date beforeSumbitD = cqMap.floorKey(DateTimeUtil.yesterday(submitD));
				if (beforeSumbitD!=null){
					CandleQuote cq = cqMap.get(beforeSumbitD);
					if (qev.getConsensusEps()!=QEarnEvent.NO_VALUE){
						float above = (qev.getEps() - qev.getConsensusEps())/(cq.getClose()/cq.getFqIdx());
						if (qev.getEps() > qev.getConsensusEps()){
							scrl.add(new SelectCandidateResult(sc.getNormalTradeStartTime(submitD), cq.getClose()/cq.getFqIdx(), above));
						}
					}else{
						//market buy order if eps increases then same quarter last year
						if (i-4>0){
							QEarnEvent qevly = (QEarnEvent) elist.get(i-4);
							Date dqly = cqMap.floorKey(qevly.getPubDt());
							if (dqly!=null){
								CandleQuote cqly = cqMap.get(dqly);	
								float adjLyEps = qevly.getEps()*cqly.getFqIdx()/cq.getFqIdx();
								float ratio = calcRatio(adjLyEps, qev.getEps(), cq.getClose());
								scrl.add(new SelectCandidateResult(sc.getNormalTradeStartTime(submitD), 0, ratio));
							}
						}
					}
				}
			}
		}
		
		return scrl;
	}
}
