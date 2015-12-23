package org.cld.stock.indicator;

import java.util.HashMap;
import java.util.Map;

import org.cld.stock.common.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class Indicator {
	
	public static final float V_NA=-10000;
	
	public static final String KEY_CHART="chart";
	public static final String KEY_PERIODS="periods";
	
	public static final String V_CHART_CANDLESTICK="candlestick";
	public static final String V_CHART_TIMESERIES="timeseries";
	public static final String V_CHART_HISTOGRAM="histogram";
	
	private String chartId;//which chart this indicator will be put on
	private int periods=1;
	
	//init
	public abstract void init(Map<String, String> params);
	//calculate
	public abstract Object calculate(CqIndicators cqi, SelectStrategy bs);
	//return the string representation of this indicator including params
	public abstract String toKey();
	//
	private Map<String, RenderType> rmap = new HashMap<String, RenderType>();
	
	public int getPeriods(){
		return periods;
	}
	public void setPeriods(int periods){
		this.periods = periods;
	}
	public String getChartId() {
		return chartId;
	}
	public void setChartId(String chartId) {
		this.chartId = chartId;
	}
	public Map<String, RenderType> getRmap() {
		return rmap;
	}
	public void setRmap(Map<String, RenderType> rmap) {
		this.rmap = rmap;
	}
	
}
