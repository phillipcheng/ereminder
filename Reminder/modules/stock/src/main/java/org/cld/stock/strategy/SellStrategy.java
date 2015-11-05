package org.cld.stock.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.cld.stock.trade.StockOrder;
import org.cld.stock.trade.StockOrder.ActionType;
import org.cld.stock.trade.StockOrder.OrderType;
import org.cld.util.StringUtil;

//for abstract class json mapping
public class SellStrategy {
	public static String KEY_SELL_STRATEGYS="sellstrategys";
	
	public static String KEY_SELECT_NUMBER="sls.selectnumber"; //the number of stock selected
	public static String KEY_SELLS_DURATION="sls.duration";
	public static String KEY_SELLS_LIMIT_PERCENTAGE="sls.limitPercentage";
	public static String KEY_SELLS_TRAIL_PERCENTAGE="sls.stopTrailingPercentage";

	private int selectNumber=1;
	private int holdDuration=0;
	private float limitPercentage=0;//% unit
	private float stopTrailingPercentage=0;//% unit
	
	public SellStrategy(){
	}
	
	public SellStrategy(int selectNumber, int holdDuration, float limitPercentage, float stopTrailingPercentage){
		this.selectNumber = selectNumber;
		this.holdDuration = holdDuration;
		this.limitPercentage = limitPercentage;
		this.stopTrailingPercentage = stopTrailingPercentage;
	}

	@Override
	public int hashCode(){
		return (int) (selectNumber + holdDuration + limitPercentage + stopTrailingPercentage);
	}
	
	@Override
	public boolean equals(Object o){
		if (o!=null){
			SellStrategy os = (SellStrategy) o;
			if (this.selectNumber == os.selectNumber &&
					this.holdDuration==os.holdDuration &&
					this.limitPercentage == os.limitPercentage &&
					this.stopTrailingPercentage == os.stopTrailingPercentage){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public String toString(){
		return String.format("%d,%d,%.2f,%.2f", selectNumber, holdDuration, limitPercentage, stopTrailingPercentage);
	}
	
	public static SellStrategy[] gen(PropertiesConfiguration props){
		Float[] selectNumbers = new Float[]{1f};
		Float[] durations = new Float[]{0f};
		Float[] limitPercentages = new Float[]{0f};
		Float[] stopTrailingPercentages = new Float[]{0f};
		
		String strSelectNumber = props.getString(SellStrategy.KEY_SELECT_NUMBER);
		if (strSelectNumber!=null){
			selectNumbers = StringUtil.parseSteps(strSelectNumber);
		}
		String strDurations = props.getString(SellStrategy.KEY_SELLS_DURATION);
		if (strDurations!=null){
			durations = StringUtil.parseSteps(strDurations);
		}
		String strLimitPercents = props.getString(SellStrategy.KEY_SELLS_LIMIT_PERCENTAGE);
		if (strLimitPercents!=null){
			limitPercentages = StringUtil.parseSteps(strLimitPercents);
		}
		String strStopTrailingPercents = props.getString(SellStrategy.KEY_SELLS_TRAIL_PERCENTAGE);
		if (strStopTrailingPercents!=null){
			stopTrailingPercentages = StringUtil.parseSteps(strStopTrailingPercents);
		}
		List<SellStrategy> ssl = new ArrayList<SellStrategy>();
		for (float selectNumber:selectNumbers){
			for (float duration:durations){
				for (float lp:limitPercentages){
					for (float stp:stopTrailingPercentages){
						SellStrategy sls = new SellStrategy();
						sls.setSelectNumber((int)selectNumber);
				        sls.setHoldDuration((int) duration);
				        sls.setLimitPercentage(lp);
				        sls.setStopTrailingPercentage(stp);
				        ssl.add(sls);
					}
				}
			}
		}
		SellStrategy[] ssa = new SellStrategy[ssl.size()];
		return ssl.toArray(ssa);
        
	}
	
	public static List<StockOrder> makeStockOrders(String stockid, Date dt, float buyLimit, SellStrategy ss){
		ArrayList<StockOrder> sol = new ArrayList<StockOrder>();
		StockOrder buyOrder = new StockOrder();
		buyOrder.setStockid(stockid);
		buyOrder.setAction(ActionType.buy);
		buyOrder.setSubmitTime(dt);
		if (buyLimit>0){
			buyOrder.setOrderType(OrderType.limit);
			buyOrder.setLimitPrice(buyLimit);
		}else{
			buyOrder.setOrderType(OrderType.market);
		}
		buyOrder.setDuration(ss.getHoldDuration());//TODO
		sol.add(buyOrder);
		
		if (ss.getLimitPercentage()!=0){
			StockOrder limitSellOrder = new StockOrder();
			limitSellOrder.setStockid(stockid);
			limitSellOrder.setAction(ActionType.sell);
			limitSellOrder.setOrderType(OrderType.limit);
			limitSellOrder.setLimitPercentage(ss.getLimitPercentage());
			limitSellOrder.setPairOrderId(buyOrder.getOrderId());
			sol.add(limitSellOrder);
		}
		
		if (ss.getStopTrailingPercentage()!=0){
			StockOrder limitTrailSellOrder = new StockOrder();
			limitTrailSellOrder.setStockid(stockid);
			limitTrailSellOrder.setAction(ActionType.sell);
			limitTrailSellOrder.setOrderType(OrderType.stoptrailingpercentage);
			limitTrailSellOrder.setIncrementPercent(ss.getStopTrailingPercentage());
			sol.add(limitTrailSellOrder);
		}
		
		StockOrder forceCleanSellOrder = new StockOrder();
		forceCleanSellOrder.setStockid(stockid);
		forceCleanSellOrder.setAction(ActionType.sell);
		forceCleanSellOrder.setOrderType(OrderType.forceclean);
		sol.add(forceCleanSellOrder);
		
		return sol;
	}
	public int getHoldDuration() {
		return holdDuration;
	}

	public void setHoldDuration(int holdDuration) {
		this.holdDuration = holdDuration;
	}

	public float getLimitPercentage() {
		return limitPercentage;
	}

	public void setLimitPercentage(float limitPercentage) {
		this.limitPercentage = limitPercentage;
	}

	public float getStopTrailingPercentage() {
		return stopTrailingPercentage;
	}

	public void setStopTrailingPercentage(float stopTrailingPercentage) {
		this.stopTrailingPercentage = stopTrailingPercentage;
	}

	public int getSelectNumber() {
		return selectNumber;
	}

	public void setSelectNumber(int selectNumber) {
		this.selectNumber = selectNumber;
	}
}
