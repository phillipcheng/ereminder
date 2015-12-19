package org.cld.chart;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.indicator.Indicator;
import org.cld.stock.indicator.RenderType;
import org.cld.stock.strategy.IntervalUnit;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;

public class ChartUtil {
	private static final SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	protected static Logger logger =  LogManager.getLogger(ChartUtil.class);
	
	public static void setMarkers(XYPlot plot, List<Date> dl){
		plot.clearDomainMarkers();
		for (Date dt:dl){
			Marker originalEnd = new ValueMarker(dt.getTime());
	        originalEnd.setPaint(Color.orange);
	        plot.addDomainMarker(originalEnd);
		}
	}
	public static void setTimeLine(IntervalUnit unit, XYPlot plot){
		DateAxis da = (DateAxis) plot.getDomainAxis();
		if (unit == IntervalUnit.day){
			da.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
		}else if (unit == IntervalUnit.minute){
			da.setTimeline(newOneMinuteTimeline());
		}
	}
	
	public static SegmentedTimeline newOneMinuteTimeline() {
        SegmentedTimeline timeline = new SegmentedTimeline(
        		SegmentedTimeline.MINUTE_SEGMENT_SIZE, 390, 1050);
        try{
	        //timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900() + 570 * timeline.getSegmentSize());
        	timeline.setStartTime(msdf.parse("2000-01-03 08:30").getTime());//day time saving
        }catch(Exception e){
        	logger.error("", e);
        }
        timeline.setBaseTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
        return timeline;
    }
	
	public static int getFirstValid(List<CqIndicators> cqilist){
		int idx=0;
		for (CqIndicators cqi:cqilist){
			boolean hasNull=false;
			for (Object o:cqi.getPv().values()){
				if (o==null){
					hasNull=true;
				}else{
					if (o instanceof Float){
						float fv = (Float)o;
						if (fv == Indicator.V_NA){
							hasNull = true;
						}
					}
				}
			}
			if (hasNull){
				idx++;
			}else{
				break;
			}
		}
		return idx;
	}
	
	public static List<TimeSeries> getTSCollection(Indicator ind, List<CqIndicators> cqilist, RenderType rt){
		List<TimeSeries> tslist = new ArrayList<TimeSeries>();
		String key = ind.toKey();
		Map<String, TimeSeries> seriesMap = new HashMap<String, TimeSeries>();
		TimeSeries oneTs=null;
		for (CqIndicators cqi: cqilist){
			Object o = cqi.getIndicator(key);
			float v;
			if (o instanceof Map){
				Map<String, Float> vMap = (Map<String, Float>) o;
				for (String pk: vMap.keySet()){
					RenderType krt = ind.getRmap().get(pk);
					if (krt==rt){
						v = vMap.get(pk);
						String tskey = String.format("%s-%s", key, pk);
						TimeSeries ts = seriesMap.get(tskey);
						if (ts==null){
							ts = new TimeSeries(tskey);
							seriesMap.put(tskey, ts);
							tslist.add(ts);
						}
						ts.add(new FixedMillisecond(cqi.getCq().getStartTime()), v);
					}
				}
			}else{
				RenderType krt = ind.getRmap().get(key);
				if (krt==rt){
					if (oneTs==null){
						oneTs = new TimeSeries(key);
						tslist.add(oneTs);
					}
					v = (float) o;
					oneTs.add(new FixedMillisecond(cqi.getCq().getStartTime()), v);
				}
			}
		}
		return tslist;
	}
}
