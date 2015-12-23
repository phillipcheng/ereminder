package org.cld.chart;

import java.util.Date;
import java.util.List;

import org.cld.stock.common.CqIndicators;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectStrategy;

public interface DataChart {
	public void setMyName(String name);
	public String getMyName();
	public void setData(List<CqIndicators> cqilist, SelectStrategy bs, IntervalUnit unit, List<Date> dl);
	
}
