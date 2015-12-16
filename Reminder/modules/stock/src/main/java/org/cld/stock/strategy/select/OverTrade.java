package org.cld.stock.strategy.select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.CandleQuote;
import org.cld.stock.CqIndicators;
import org.cld.stock.DivSplit;
import org.cld.stock.PriceSeg;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DataMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class OverTrade extends SelectStrategy {
	public static Logger logger = LogManager.getLogger(OverTrade.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String p_threshold="scs.param.threshold";
	public static final String p_submitprice="scs.param.submitprice";
	public static final String p_submittime="scs.param.submittime";
	public static final String p_lookupdn="scs.param.lookupdn";
	public static final String p_aggregation="scs.param.aggregation";
	public static final String p_direction="scs.param.direction";
	
	public static final String v_direction_up="up";
	public static final String v_direction_down="down";
	public static final String v_aggregation_avg="avg";
	public static final String v_aggregation_sum="sum";
	public static final String v_submittime_open="open";
	public static final String v_submittime_close="close";

	public static final int MAX_LOOKBACK=50;
	
	float pthreshold=5f;
	float psubmitpriceUpRatio=0f;
	int plookupdn=0;//number of day and night to lookup. from normal open to normal close is 1 day, from normal close to normal open is 1 night
	String paggregation=v_aggregation_avg;
	String pdirection=v_direction_down;
	String psubmittime=v_submittime_open;
	
	private List<CandleQuote> cqLookup = new ArrayList<CandleQuote>();//cache
	
	Set<Date> lxdds = new HashSet<Date>();
	
	public OverTrade(){
	}
	
	@Override
	public void init(){
		super.init();
		super.setParam(SelectStrategy.KEY_LU_UNIT, IntervalUnit.day);
		if (this.getParams().containsKey(p_threshold)){
			pthreshold = Float.parseFloat(getParams().get(p_threshold).toString());
		}
		if (this.getParams().containsKey(p_submitprice)){
			psubmitpriceUpRatio = Float.parseFloat(getParams().get(p_submitprice).toString());
		}
		if (this.getParams().containsKey(p_lookupdn)){
			plookupdn = Integer.parseInt(getParams().get(p_lookupdn).toString());
		}
		if (this.getParams().containsKey(p_aggregation)){
			paggregation = getParams().get(p_aggregation).toString();
		}
		if (this.getParams().containsKey(p_direction)){
			pdirection = getParams().get(p_direction).toString();
		}
		if (this.getParams().containsKey(p_submittime)){
			psubmittime = getParams().get(p_submittime).toString();
		}
		if (plookupdn==0){
			plookupdn = MAX_LOOKBACK;
		}
	}
	
	@JsonIgnore
	@Override
	public Map<String, DataMapper> getDataMappers() {
		super.getDataMappers();
		dataMap.put(SelectStrategy.KEY_DIVSPLIT, sc.getExDivSplitHistoryTableMapper());
		return dataMap;
	}
	
	@Override
	public void initHistoryData(Map<String, List<? extends Object>> resultMap){
		super.initHistoryData(resultMap);
		List<DivSplit> lxd = (List<DivSplit>) resultMap.get(SelectStrategy.KEY_DIVSPLIT);
		for (DivSplit ds:lxd){
			lxdds.add(ds.getDt());
		}
	}

	@Override
	public SelectCandidateResult selectByStream(CqIndicators cqi) {
		if (cqLookup.size()>=plookupdn){//store twice more
			cqLookup.remove(0);
			cqLookup.add(cqi.getCq());
		}else{
			cqLookup.add(cqi.getCq());
		}
		int i=cqLookup.size()-1;
		CandleQuote submitCq = (CandleQuote) cqLookup.get(i);
		if (lxdds.contains(submitCq.getStartTime())){//skip ex days
			return null;
		}
		CandleQuote thisCq = submitCq;
		CandleQuote prevCq = null;
		CandleQuote oneDayBeforeSumbitCq = null;
		if (i-1>=0){
			oneDayBeforeSumbitCq = (CandleQuote) cqLookup.get(i-1);
			float limit=0;
			int days=0;//include the submit day
			SelectCandidateResult scr = null;
			if (checkValid(oneDayBeforeSumbitCq)){
				boolean isDay = true;//using the open-close day portion, else use the close-open night portion
				if (psubmittime.equals(v_submittime_open)){
					isDay = false;
				}else{
					isDay = true;
				}
				int j=i-1;
				prevCq = (CandleQuote)cqLookup.get(j);
				List<PriceSeg> psl = new ArrayList<PriceSeg>();
				while (j>0){
					PriceSeg ps = null;
					if (isDay && (pdirection.equals(v_direction_down) && thisCq.getClose()<thisCq.getOpen() || 
							pdirection.equals(v_direction_up) && thisCq.getClose()>thisCq.getOpen())){
						limit = thisCq.getOpen();
						ps = new PriceSeg(PriceSeg.TAG_OPEN, thisCq.getStartTime(), thisCq.getOpen(), 
								PriceSeg.TAG_CLOSE, thisCq.getStartTime(), thisCq.getClose());
					}else if (!isDay && (pdirection.equals(v_direction_down) && thisCq.getOpen()<prevCq.getClose() ||
							pdirection.equals(v_direction_up) && thisCq.getOpen()>prevCq.getClose())){
						limit = prevCq.getClose();
						ps = new PriceSeg(PriceSeg.TAG_CLOSE, prevCq.getStartTime(), prevCq.getClose(), 
								PriceSeg.TAG_OPEN, thisCq.getStartTime(), thisCq.getOpen());
					}else{
						break;
					}
					if (ps!=null) psl.add(ps);
					if (days>plookupdn){
						break;
					}
					isDay = !isDay;
					days++;
					if (isDay){
						thisCq = prevCq;
						j--;
						prevCq = (CandleQuote)cqLookup.get(j);
					}
				}
				logger.debug(String.format("psl for day %s:%s", sdf.format(submitCq.getStartTime()), psl));
				if (limit*days>0){
					float value =0f;
					if (psubmittime.equals(v_submittime_open)){
						value = limit - submitCq.getOpen();
					}else{
						value = limit - submitCq.getClose();
					}
					value = value / limit;
					if (paggregation.equals(v_aggregation_avg)){
						value= value/days;
					}
					value = Math.abs(value);
					if (value>pthreshold*0.01){
						float price = 0f;
						if (psubmittime.equals(v_submittime_open)){
							price = submitCq.getOpen()*(1+psubmitpriceUpRatio*0.01f);
							scr = new SelectCandidateResult(submitCq.getSymbol(), sc.getNormalTradeStartTime(submitCq.getStartTime()), value, price);
						}else{
							price = submitCq.getClose()*(1+psubmitpriceUpRatio*0.01f);
							scr = new SelectCandidateResult(submitCq.getSymbol(), sc.getNormalTradeEndTime(submitCq.getStartTime()), value, price);
						}
						logger.info(String.format("%s has consecutive %s %s %.3f for %d nd til %s", submitCq.getSymbol(), pdirection, paggregation, 
								value, days, sdf.format(submitCq.getStartTime())));
					}
				}
			}else{
				logger.debug(String.format("not valid stock %s", oneDayBeforeSumbitCq.toString()));
			}
			return scr;
		}else{
			return null;
		}
	}
}
