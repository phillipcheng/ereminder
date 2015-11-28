package org.cld.stock.strategy.select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.cld.stock.CandleQuote;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DataMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RandomD extends SelectStrategy {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private StockConfig sc;
	
	public RandomD(){
	}
	
	//init after json deserilized
	public void init(){
		super.init();
		sc = StockUtil.getStockConfig(this.getBaseMarketId());
	}
	
	@JsonIgnore
	@Override
	public DataMapper[] getDataMappers() {
		return new DataMapper[]{sc.getBTFQDailyQuoteMapper()};
	}
	
	@Override
	public List<SelectCandidateResult> selectByHistory(Map<DataMapper, List<Object>> tableResults) {
		Object[] params = this.getParams();
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<Object> lo = tableResults.get(sc.getBTFQDailyQuoteMapper());
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
