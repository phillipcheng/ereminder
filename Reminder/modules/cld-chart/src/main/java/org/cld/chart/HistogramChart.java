package org.cld.chart;

import java.awt.Color;
import java.util.List;

import org.cld.stock.CqIndicators;
import org.cld.stock.indicator.Indicator;
import org.cld.stock.strategy.SelectStrategy;
import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class HistogramChart extends ChartPanel {
	private static final long serialVersionUID = 1L;
	
	public HistogramChart(JFreeChart jfc){
		super(jfc);
	}

	//list of cqIndicators
	public void setData(List<CqIndicators> cqilist, SelectStrategy bs){
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		for (String name: bs.getIndMap().keySet()){
			Indicator ind = bs.getIndMap().get(name);
			if (Indicator.V_CHART_HISTOGRAM.equals(ind.getChartType())){
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
	
	public static HistogramChart createHistogramChart(){
		JFreeChart jfreechart = ChartFactory.createHistogram(null, null, null, null, PlotOrientation.VERTICAL, true, true, true);
		XYPlot xyplot = (XYPlot)jfreechart.getPlot();
		xyplot.setDomainPannable(true);

		xyplot.setBackgroundPaint(Color.white);
		
		NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
		xyplot.setDomainAxis(new DateAxis());
		numberaxis.setAutoRangeIncludesZero(false);
		numberaxis.setUpperMargin(0.0D);
		numberaxis.setLowerMargin(0.0D);
		HistogramChart chartpanel = new HistogramChart(jfreechart);
		chartpanel.setDomainZoomable(true);
		chartpanel.setRangeZoomable(true);
		chartpanel.setMouseWheelEnabled(true);
		return chartpanel;
	}
	
	
}
