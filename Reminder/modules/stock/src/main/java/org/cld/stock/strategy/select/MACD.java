package org.cld.stock.strategy.select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cld.stock.CqIndicators;
import org.cld.stock.indicator.EMA;
import org.cld.stock.indicator.EMAMacd;
import org.cld.stock.indicator.Indicator;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DataMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MACD extends SelectStrategy {

	public static final String EMA_UP="scs.param.indicator.emaUp.periods";
	public static final String EMA_DOWN="scs.param.indicator.emaDown.periods";
	public static final String EMA_SIGNAL="scs.param.indicator.emaSignal.periods";
	
	//down>up>signal
	private int upPeriods;
	private int downPeriods;
	private int signalPeriods;
	private EMA upEMA;
	private EMA downEMA;
	private EMAMacd signalEMA;
	private String upKey;
	private String downKey;
	private String signalKey;
	
	List<CqIndicators> cqilist;
	private float lastmacd;
	
	public MACD(){
	}
	
	@Override
	public void init(){
		super.init();
		upPeriods = (int) Float.parseFloat((String) this.getParams().get(EMA_UP));
		upEMA = new EMA(upPeriods);
		upKey = upEMA.toKey();
		downPeriods = (int) Float.parseFloat((String) this.getParams().get(EMA_DOWN));
		downEMA = new EMA(downPeriods);
		downKey = downEMA.toKey();
		signalPeriods = (int) Float.parseFloat((String) this.getParams().get(EMA_SIGNAL));
		signalEMA = new EMAMacd(signalPeriods);
		signalKey = signalEMA.toKey();
	}
	
	@Override
	public void initData(Map<DataMapper, List<? extends Object>> resultMap){
		cqilist = (List<CqIndicators>) resultMap.get(super.quoteMapper());
	}
	
	@Override
	public int maxLookupNum() {
		return downPeriods;
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
		List<CqIndicators> lo = null;
		if (super.quoteMapper().oneFetch()){
			lo = cqilist;
		}else{
			lo = (List<CqIndicators>) tableResults.get(super.quoteMapper());
		}
		for (int i=0; i<lo.size(); i++){
			CqIndicators cqi = lo.get(i);
			float macd = Indicator.V_NA;
			if (cqi.hasIndicator(downKey)){
				float up = cqi.getIndicator(upKey);
				float down = cqi.getIndicator(downKey);
				float signal = cqi.getIndicator(signalKey);
				macd = up-down-signal;
				if (macd>0 && lastmacd!=Indicator.V_NA && lastmacd<0){
					if (this.getLookupUnit() == IntervalUnit.day){
						scrl.add(new SelectCandidateResult(sc.getNormalTradeStartTime(cqi.getCq().getStartTime()), 0f));
					}else if (this.getLookupUnit() == IntervalUnit.minute){
						scrl.add(new SelectCandidateResult(cqi.getCq().getStartTime(), 0f));
					}
				}
				lastmacd = macd;
			}
		}
		return scrl;
	}
}
