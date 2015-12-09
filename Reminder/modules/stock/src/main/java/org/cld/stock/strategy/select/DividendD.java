package org.cld.stock.strategy.select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.cld.stock.AnnounceTime;
import org.cld.stock.CandleQuote;
import org.cld.stock.CqIndicators;
import org.cld.stock.DivSplit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DataMapper;
import org.cld.util.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class DividendD extends SelectStrategy {
	
	public static final String yield="scs.param.yield";
	
	private float yieldThreshold=0f;
	
	public DividendD(){
	}
	
	List<DivSplit> dvlist;
	TreeMap<Date, CandleQuote> cqMap = new TreeMap<Date, CandleQuote>();
	
	@Override
	public void init(){
		super.init();
		String syield = (String) this.getParams().get(yield);
		yieldThreshold = Float.parseFloat(syield);
	}
	
	@Override
	public void initData(Map<DataMapper, List<? extends Object>> resultMap){
		List<? extends Object> fqlist = resultMap.get(super.quoteMapper());
		for (Object o:fqlist){
			CqIndicators cqi = (CqIndicators)o;
			cqMap.put(cqi.getCq().getStartTime(), cqi.getCq());
		}
		dvlist = (List<DivSplit>) resultMap.get(sc.getDividendTableMapper());
	}
	
	@Override
	public int maxLookupNum() {
		return 0;
	}

	@JsonIgnore
	@Override
	public DataMapper[] getDataMappers() {
		return new DataMapper[]{super.quoteMapper(), sc.getDividendTableMapper()};
	}
	
	/**
	 * @param tableResults not used. using the one passed by init
	 */
	@Override
	public List<SelectCandidateResult> selectByHistory(Map<DataMapper, List<? extends Object>> tableResults) {
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		for (Object dvo:dvlist){
			DivSplit dv = (DivSplit)dvo;
			Date submitD = cqMap.ceilingKey(dv.getDt());//>=
			if (submitD==dv.getDt()){
				if (dv.getAt()==AnnounceTime.afterMarket){
					submitD = cqMap.ceilingKey(DateTimeUtil.tomorrow(submitD));
				}
			}
			Date beforeSumbitD = cqMap.floorKey(DateTimeUtil.yesterday(submitD));
			if (beforeSumbitD!=null){
				CandleQuote cq = cqMap.get(beforeSumbitD);
				if (checkValid(cq)){
					float yield = dv.getDividend()/(cq.getClose()/cq.getFqIdx());
					if (yield>yieldThreshold){
						scrl.add(new SelectCandidateResult(sc.getNormalTradeStartTime(submitD), yield));
					}
				}
			}
		}
		return scrl;
	}
}
