package org.cld.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import org.cld.stock.CqIndicators;
import org.cld.stock.indicator.Indicator;
import org.cld.stock.strategy.SelectStrategy;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class TimeSeriesChart extends ChartPanel {
	private static final long serialVersionUID = 1L;
	
	public TimeSeriesChart(JFreeChart jfc){
		super(jfc);
	}

	//list of cqIndicators
	public void setData(List<CqIndicators> cqilist, SelectStrategy bs){
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		
		for (String name: bs.getIndMap().keySet()){
			Indicator ind = bs.getIndMap().get(name);
			if (Indicator.V_CHART_TIMESERIES.equals(ind.getChartType())){
				String key = ind.toKey();
				TimeSeries series = new TimeSeries(key);
				for (CqIndicators cqi: cqilist){
					float v = cqi.getIndicator(key);
					if (v == Indicator.V_NA){
						v = 0f;
					}
					series.add(new FixedMillisecond(cqi.getCq().getStartTime()), v);
				}
				dataset.addSeries(series);
			}
		}
	
		XYPlot xyp = (XYPlot) this.getChart().getPlot();
		xyp.setDataset(dataset);
	}
	
	public static TimeSeriesChart createTimeSeriesChart(){
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(null, null, null, null);
		XYPlot xyplot = (XYPlot)jfreechart.getPlot();
		xyplot.setDomainPannable(true);

		xyplot.setBackgroundPaint(Color.white);
		
		NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
		numberaxis.setAutoRangeIncludesZero(false);
		numberaxis.setUpperMargin(0.0D);
		numberaxis.setLowerMargin(0.0D);
		TimeSeriesChart chartpanel = new TimeSeriesChart(jfreechart);
		chartpanel.setDomainZoomable(true);
		chartpanel.setRangeZoomable(true);
		chartpanel.setMouseWheelEnabled(true);
		return chartpanel;
	}
	
	
}
