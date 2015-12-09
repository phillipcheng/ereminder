package org.cld.stock.strategy.select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.cld.stock.CandleQuote;
import org.cld.stock.CqIndicators;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DataMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RandomD extends SelectStrategy {
	
	public RandomD(){
	}

	private List<CandleQuote> cqlist;
	
	@Override
	public void initData(Map<DataMapper, List<? extends Object>> resultMap){
		List<CqIndicators> cqilist = (List<CqIndicators>) resultMap.get(this.quoteMapper());
		for (CqIndicators cqi:cqilist){
			cqlist.add(cqi.getCq());
		}
	}
	
	@Override
	public int maxLookupNum() {
		return 0;
	}
	
	@JsonIgnore
	@Override
	public DataMapper[] getDataMappers() {
		return new DataMapper[]{super.quoteMapper()};
	}
	
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
		int idx=0;
		Random r = new Random();
		while (idx<lo.size()){
			//emit a random value for stock on trading day dt
			CandleQuote cq = (CandleQuote) lo.get(idx);
			if (checkValid(cq)){
				scrl.add(new SelectCandidateResult(sc.getNormalTradeStartTime(cq.getStartTime()), r.nextFloat()));
			}
			idx++;
		}
		return scrl;
	}
}
