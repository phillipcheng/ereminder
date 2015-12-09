package org.cld.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Date;
import java.util.List;

import org.cld.stock.CandleQuote;
import org.cld.stock.CqIndicators;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.DefaultHighLowDataset;

public class CandlestickChart extends ChartPanel {
	private static final long serialVersionUID = 1L;
	
	public CandlestickChart(JFreeChart jfc){
		super(jfc);
	}

	//list of candlequote
	public void setData(List<CqIndicators> cqilist){
		int size = cqilist.size();
		Date[] dates = new Date[size];
		double[] highs = new double[size];
		double[] lows = new double[size];
		double[] opens = new double[size];
		double[] closes = new double[size];
		double[] volumes = new double[size];
		for (int i=0; i<cqilist.size(); i++){
			CandleQuote cq = ((CqIndicators)cqilist.get(i)).getCq();
			dates[i] = cq.getStartTime();
			highs[i] = cq.getHigh();
			lows[i] = cq.getLow();
			opens[i] = cq.getOpen();
			closes[i] = cq.getClose();
			volumes[i] = cq.getVolume();
		}
		DefaultHighLowDataset hlds = new DefaultHighLowDataset("prices", dates, highs, lows, opens, closes, volumes);
		XYPlot xyp = (XYPlot) this.getChart().getPlot();
		xyp.setDataset(hlds);
	}
	
	public static CandlestickChart createCandlestickChart(){
		JFreeChart jfreechart = ChartFactory.createCandlestickChart(null, null, null, null, true);
		XYPlot xyplot = (XYPlot)jfreechart.getPlot();
		xyplot.setDomainPannable(true);
		xyplot.setBackgroundPaint(Color.white);
		CandlestickRenderer rend = (CandlestickRenderer) xyplot.getRenderer();
		rend.setDownPaint(Color.blue);
		rend.setUpPaint(Color.red);
		NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
		numberaxis.setAutoRangeIncludesZero(false);
		numberaxis.setUpperMargin(0.0D);
		numberaxis.setLowerMargin(0.0D);
		
		CandlestickChart chartpanel = new CandlestickChart(jfreechart);
		chartpanel.setDomainZoomable(true);
		chartpanel.setRangeZoomable(true);
		chartpanel.setMouseWheelEnabled(true);
		return chartpanel;
	}
	
	
}
