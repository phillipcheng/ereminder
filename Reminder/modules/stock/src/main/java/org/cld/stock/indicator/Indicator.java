package org.cld.stock.indicator;

import java.util.List;
import java.util.Map;

import org.cld.stock.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class Indicator {
	
	public static final float V_NA=-10000;
	public static final String KEY_CHART="chart";
	public static final String KEY_PERIODS="periods";
	
	public static final String V_CHART_TIMESERIES="timeseries";
	public static final String V_CHART_HISTOGRAM="histogram";
	
	private String chartType;
	private int periods=1;
	
	//init
	public abstract void init(Map<String, String> params);
	//calculate
	public abstract float calculate(CqIndicators prevCqi, List<CqIndicators> cql, SelectStrategy bs);
	//return the string representation of this indicator including params
	public abstract String toKey();
	//
	public int getPeriods(){
		return periods;
	}
	public void setPeriods(int periods){
		this.periods = periods;
	}
	public String getChartType() {
		return chartType;
	}
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
}
