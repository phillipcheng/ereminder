package org.cld.stock.indicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cld.stock.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.StatisticsUtil;

/*
Bollinger Bands (20,2)					
Date	Price	Middle Band 20-day SMA	20-day Standard Deviation	Upper Band 20-day SMA + STDEVx2	Lower Band 20-day SMA  - STDEVx2	BandWidth				
1		1-May-09	86.16					
2		4-May-09	89.09					
3		5-May-09	88.78					
4		6-May-09	90.32					
5		7-May-09	89.07					
6		8-May-09	91.15					
7		11-May-09	89.44					
8		12-May-09	89.18					
9		13-May-09	86.93					
10		14-May-09	87.68					
11		15-May-09	86.96					
12		18-May-09	89.43					
13		19-May-09	89.32					
14		20-May-09	88.72					
15		21-May-09	87.45					
16		22-May-09	87.26					
17		26-May-09	89.50					
18		27-May-09	87.90					
19		28-May-09	89.13					
20		29-May-09	90.70	88.71	1.29	91.29	86.12	5.17
21		1-Jun-09	92.90	89.05	1.45	91.95	86.14	5.81
22		2-Jun-09	92.98	89.24	1.69	92.61	85.87	6.75
23		3-Jun-09	91.80	89.39	1.77	92.93	85.85	7.09
*/
//simple moving average
public class Bollinger extends Indicator{

	public static final String middleBand = "mb";
	public static final String upperBand = "ub";
	public static final String lowerBand = "lb";
	
	public static final String PARAM_WIDTH="width";
	
	private float width;
	private List<Float> values = new ArrayList<Float>();
	
	public Bollinger(){
	}
	
	public Bollinger(int periods, float width){
		super.setPeriods(periods);
		this.width = width;
	}
	
	@Override
	public void init(Map<String, String> params) {
		width = Float.parseFloat(params.get(PARAM_WIDTH));
		this.getRmap().put(middleBand, RenderType.line);
		this.getRmap().put(upperBand, RenderType.line);
		this.getRmap().put(lowerBand, RenderType.line);
	}
	
	public static Map<String, Float> calcBollinger(List<Float> vs){
		float mb = SMA.calSMA(vs);
		float stddev = StatisticsUtil.getStddev(vs);
		float upper = mb + stddev*2;
		float lower = mb - stddev*2;
		Map<String, Float> map = new HashMap<String, Float>();
		map.put(middleBand, mb);
		map.put(upperBand, upper);
		map.put(lowerBand, lower);
		return map;
	}
	
	@Override
	public Object calculate(CqIndicators cqi, SelectStrategy bs){
		if (values.size()<super.getPeriods()-1){
			values.add(cqi.getCq().getClose());
			return null;
		}else if (values.size()==super.getPeriods()-1){
			values.add(cqi.getCq().getClose());
			return calcBollinger(values);
		}else{
			values.remove(0);
			values.add(cqi.getCq().getClose());
			return calcBollinger(values);
		}
	}

	@Override
	public String toKey() {
		return String.format("Bollinger:%d,%.2f", super.getPeriods(), width);
	}
}