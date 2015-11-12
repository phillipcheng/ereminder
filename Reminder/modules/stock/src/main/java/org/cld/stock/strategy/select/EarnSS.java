package org.cld.stock.strategy.select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cld.stock.AnnounceTime;
import org.cld.stock.CandleQuote;
import org.cld.stock.QEarnEvent;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DateTimeUtil;
import org.cld.util.jdbc.JDBCMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

//TODO
public class EarnSS extends SelectStrategy {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private StockConfig sc;
	
	public EarnSS(){
	}
	
	//init after json deserilized
	public void init(){
		super.init();
		sc = StockUtil.getStockConfig(this.getBaseMarketId());
	}
	
	@JsonIgnore
	@Override
	public JDBCMapper[] getTableMappers() {
		return new JDBCMapper[]{sc.getFQDailyQuoteTableMapper(), sc.getEarnTableMapper()};
	}
	
	//the bigger the better
	private float calcRatio(float lyeps, float eps, float currentPrice){
		float assumePE=10;
		return (currentPrice/assumePE)/(eps-lyeps);
	}
	
	@JsonIgnore
	@Override
	public List<SelectCandidateResult> getSelectCandidate(Map<JDBCMapper, List<Object>> tableResults) {
		Object[] params = this.getParams();
		float threashold = ((Double)params[0]).floatValue();//
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<Object> fqlist = tableResults.get(sc.getFQDailyQuoteTableMapper());
		TreeMap<Date, CandleQuote> cqMap = new TreeMap<Date, CandleQuote>();
		for (Object cqo:fqlist){
			CandleQuote cq = (CandleQuote) cqo;
			cqMap.put(cq.getStartTime(), cq);
		}
		List<Object> elist = tableResults.get(sc.getEarnTableMapper());
		for (int i=0; i<elist.size(); i++){
			QEarnEvent qev = (QEarnEvent)elist.get(i);
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
							scrl.add(new SelectCandidateResult(sdf.format(submitD), cq.getClose()/cq.getFqIdx(), above));
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
								scrl.add(new SelectCandidateResult(sdf.format(submitD), 0, ratio));
							}
						}
					}
				}
			}
		}
		
		return scrl;
	}
}
