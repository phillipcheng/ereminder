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
import org.cld.util.jdbc.JDBCMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RandomSS extends SelectStrategy {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private StockConfig sc;
	
	public RandomSS(){
	}
	
	//init after json deserilized
	public void init(){
		super.init();
		sc = StockUtil.getStockConfig(this.getBaseMarketId());
	}
	
	@JsonIgnore
	@Override
	public JDBCMapper[] getTableMappers() {
		return new JDBCMapper[]{sc.getFQDailyQuoteTableMapper()};
	}
	
	@JsonIgnore
	@Override
	public List<SelectCandidateResult> selectByHistory(Map<JDBCMapper, List<Object>> tableResults) {
		Object[] params = this.getParams();
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<Object> lo = tableResults.get(sc.getFQDailyQuoteTableMapper());
		int idx=0;
		Random r = new Random();
		while (idx<lo.size()){
			//emit a random value for stock on trading day dt
			CandleQuote cq = (CandleQuote) lo.get(idx);
			if (checkValid(cq)){
				scrl.add(new SelectCandidateResult(sdf.format(cq.getStartTime()), r.nextFloat()));
			}
			idx++;
		}
		return scrl;
	}
}
