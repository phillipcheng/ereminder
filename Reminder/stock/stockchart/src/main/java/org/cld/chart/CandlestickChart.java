package org.cld.chart;

import java.awt.Color;
import java.util.Date;
import java.util.List;

import org.cld.stock.common.CandleQuote;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.indicator.Indicator;
import org.cld.stock.indicator.RenderType;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectStrategy;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;

//with 1 candlestick and multiple timeseries
public class CandlestickChart extends ChartPanel implements DataChart{
	private static final long serialVersionUID = 1L;
	String chartName;
	
	public CandlestickChart(JFreeChart jfc){
		super(jfc);
	}

	@Override
	public void setData(List<CqIndicators> cqilist, SelectStrategy bs, IntervalUnit unit, List<Date> dl){
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
		
		DefaultHighLowDataset hlDataset = new DefaultHighLowDataset("prices", dates, highs, lows, opens, closes, volumes);
		XYPlot plot = (XYPlot) this.getChart().getPlot();
		ChartUtil.setTimeLine(unit, plot, dates[0]);
		plot.setDataset(hlDataset);
		ChartUtil.setMarkers(plot, dl);
		

		//for lines overlay with candlestick
		TimeSeriesCollection lineDataset = new TimeSeriesCollection();
		for (String indName: bs.getIndMap().keySet()){
			Indicator ind = bs.getIndMap().get(indName);
			if (chartName.equals(ind.getChartId())){
				List<TimeSeries> tsl = ChartUtil.getTSCollection(ind, cqilist, RenderType.line);
				for (TimeSeries ts:tsl){
					lineDataset.addSeries(ts);
				}
			}
		}
		
		int index = 1;
		plot.setDataset(index, lineDataset);
		plot.mapDatasetToRangeAxis(index, 0);
		XYItemRenderer renderer2 = new XYLineAndShapeRenderer();
		plot.setRenderer(index, renderer2);
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		
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

	@Override
	public void setMyName(String name) {
		this.chartName = name;
	}
	@Override
	public String getMyName() {
		return chartName;
	}
}
