package org.cld.stock.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.StockOrder.ActionType;
import org.cld.stock.strategy.StockOrder.OrderType;
import org.cld.stock.strategy.StockOrder.TimeInForceType;
import org.cld.util.CombPermUtil;
import org.cld.util.StringUtil;

//for abstract class json mapping
public class SellStrategy {
	private static Logger logger =  LogManager.getLogger(SellStrategy.class);
	
	public static String KEY_SELL_STRATEGYS="sellstrategys";
	
	public static String KEY_SELECT_NUMBER="sls.selectnumber"; //the number of stock selected
	public static String KEY_SELLS_DURATION="sls.duration";
	public static String KEY_SELLS_DURATION_UNIT="sls.duration.unit";
	public static String KEY_SELLS_LIMIT_PERCENTAGE="sls.limitPercentage";
	public static String KEY_SELLS_STOP_PERCENTAGE="sls.stopPercentage";
	public static String KEY_SELLS_ISTRAILING="sls.trailing";
	
	private int selectNumber=1;
	private int holdDuration=0;
	private String holdUnit = StrategyConst.V_UNIT_DAY;
	private float limitPercentage=0;//% unit
	private float stopPercentage=0; //%unit
	private boolean trailing=false;
	
	public SellStrategy(){
	}
	
	public SellStrategy(int selectNumber, String holdUnit, int holdDuration, float limitPercentage, float stopPercentage, boolean trailing){
		this.selectNumber = selectNumber;
		this.holdUnit = holdUnit;
		this.holdDuration = holdDuration;
		this.limitPercentage = limitPercentage;
		this.stopPercentage = stopPercentage;
		this.trailing = trailing;
	}

	@Override
	public int hashCode(){
		return (int) (selectNumber + holdDuration + limitPercentage + stopPercentage);
	}
	
	@Override
	public boolean equals(Object o){
		if (o!=null){
			SellStrategy os = (SellStrategy) o;
			if (this.selectNumber == os.selectNumber &&
					this.holdUnit.equals(os.holdUnit) &&
					this.holdDuration==os.holdDuration &&
					this.limitPercentage == os.limitPercentage &&
					this.trailing == os.trailing &&
					this.stopPercentage == os.stopPercentage){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public String toString(){
		return String.format("%d,%s,%d,%.2f,%b,%.2f", selectNumber, holdUnit, holdDuration, limitPercentage, trailing, stopPercentage);
	}
	
	public static SellStrategy[] gen(PropertiesConfiguration props){
		Map<String, Object[]> paramMap = new HashMap<String,Object[]>();
		paramMap.put(KEY_SELECT_NUMBER, StringUtil.parseFloatSteps(props.getString(KEY_SELECT_NUMBER, "1")));
		paramMap.put(KEY_SELLS_DURATION,StringUtil.parseFloatSteps(props.getString(KEY_SELLS_DURATION, "0")));
		paramMap.put(KEY_SELLS_DURATION_UNIT, StringUtil.parseFloatSteps(props.getString(KEY_SELLS_DURATION_UNIT, "day")));
		paramMap.put(KEY_SELLS_LIMIT_PERCENTAGE, StringUtil.parseFloatSteps(props.getString(KEY_SELLS_LIMIT_PERCENTAGE, "2")));
		paramMap.put(KEY_SELLS_STOP_PERCENTAGE, StringUtil.parseFloatSteps(props.getString(KEY_SELLS_STOP_PERCENTAGE, "1")));
		paramMap.put(KEY_SELLS_ISTRAILING, StringUtil.parseFloatSteps(props.getString(KEY_SELLS_ISTRAILING, "true")));
		List<Map<String,Object>> paramsMapList = CombPermUtil.eachOne(paramMap);
		List<SellStrategy> ssl = new ArrayList<SellStrategy>();
		for (Map<String,Object> pm:paramsMapList){
			SellStrategy sls = new SellStrategy();
			sls.setSelectNumber((int) Float.parseFloat((String)pm.get(KEY_SELECT_NUMBER)));
	        sls.setHoldUnit((String) pm.get(KEY_SELLS_DURATION_UNIT));
	        sls.setHoldDuration((int) Float.parseFloat((String)pm.get(KEY_SELLS_DURATION)));
	        sls.setLimitPercentage(Float.parseFloat((String)pm.get(KEY_SELLS_LIMIT_PERCENTAGE)));
	        sls.setStopPercentage(Float.parseFloat((String)pm.get(KEY_SELLS_STOP_PERCENTAGE)));
	        sls.setTrailing(Boolean.parseBoolean((String) pm.get(KEY_SELLS_ISTRAILING)));
	        ssl.add(sls);
		}
		SellStrategy[] ssa = new SellStrategy[ssl.size()];
		return ssl.toArray(ssa);
        
	}

	//used by real
	public static StockOrder makeBuyOrder(SelectCandidateResult scr, SellStrategy ss, float cashAmount){
		StockOrder buyOrder = new StockOrder();
		String stockid = scr.getSymbol();
		float buyLimit = scr.getBuyPrice();
		buyOrder.setSymbol(stockid);
		buyOrder.setAction(ActionType.buy);
		buyOrder.setSubmitTime(scr.getDt());
		buyOrder.setTif(TimeInForceType.DayOrder);
		if (buyLimit>0){
			buyOrder.setOrderType(OrderType.limit);
			buyOrder.setLimitPrice(buyLimit);
			int quantity=(int) (cashAmount/buyLimit);
			buyOrder.setQuantity(quantity);
		}else{
			buyOrder.setOrderType(OrderType.market);
		}
		return buyOrder;
	}
	
	//by real
	public static StockOrder makeForceSellOrder(String stockid, int qty){
		StockOrder forceCleanSellOrder = new StockOrder();
		forceCleanSellOrder.setTif(TimeInForceType.MarktOnClose);//of the last day
		forceCleanSellOrder.setSymbol(stockid);
		forceCleanSellOrder.setAction(ActionType.sell);
		forceCleanSellOrder.setOrderType(OrderType.market);
		forceCleanSellOrder.setQuantity(qty);
		return forceCleanSellOrder;
	}
	//used by real
	public static List<StockOrder> makeSellOrders(String stockid, Date buyDate, int qty, float buyPrice, SellStrategy ss){
		ArrayList<StockOrder> sol = new ArrayList<StockOrder>();
		
		StockOrder limitSellOrder = new StockOrder();
		limitSellOrder.setSubmitTime(buyDate);
		limitSellOrder.setSymbol(stockid);
		limitSellOrder.setAction(ActionType.sell);
		limitSellOrder.setOrderType(OrderType.limit);
		limitSellOrder.setQuantity(qty);
		limitSellOrder.setLimitPrice((float) (buyPrice*(1+ss.getLimitPercentage()*0.01)));
		limitSellOrder.setTif(TimeInForceType.GTC);
		sol.add(limitSellOrder);
		
		StockOrder stopSellOrder = new StockOrder();
		stopSellOrder.setSubmitTime(buyDate);
		stopSellOrder.setSymbol(stockid);
		stopSellOrder.setAction(ActionType.sell);
		if (ss.isTrailing()){
			stopSellOrder.setOrderType(OrderType.stoptrailingpercentage);
			stopSellOrder.setIncrementPercent(-1*ss.getStopPercentage());
		}else{
			stopSellOrder.setOrderType(OrderType.stoplimit);
			stopSellOrder.setLimitPrice(buyPrice * (float)(1-ss.getStopPercentage()*0.01));
			stopSellOrder.setStopPrice(buyPrice * (float)(1-ss.getStopPercentage()*0.01));
		}
		stopSellOrder.setQuantity(qty);
		stopSellOrder.setTif(TimeInForceType.GTC);
		sol.add(stopSellOrder);
		//sol.add(makeForceSellOrder(stockid, qty));
		
		return sol;
	}
	
	//by my simulator
	public static List<StockOrder> makeStockOrders(SelectCandidateResult scr, SellStrategy ss){
		ArrayList<StockOrder> sol = new ArrayList<StockOrder>();
		StockOrder buyOrder = new StockOrder();
		String stockid = scr.getSymbol();
		float buyLimit = scr.getBuyPrice();
		buyOrder.setSymbol(stockid);
		buyOrder.setAction(ActionType.buy);
		buyOrder.setSubmitTime(scr.getDt());
		buyOrder.setTif(TimeInForceType.DayOrder);
		if (buyLimit>0){
			buyOrder.setOrderType(OrderType.limit);
			buyOrder.setLimitPrice(buyLimit);
		}else{
			buyOrder.setOrderType(OrderType.market);
		}
		buyOrder.setDuration(ss.getHoldDuration());//TODO
		sol.add(buyOrder);

		
		StockOrder limitSellOrder = new StockOrder();
		limitSellOrder.setSubmitTime(null);//this submit time will be set after buyOrder executed.
		limitSellOrder.setSymbol(stockid);
		limitSellOrder.setAction(ActionType.sell);
		limitSellOrder.setOrderType(OrderType.limit);
		limitSellOrder.setLimitPercentage(ss.getLimitPercentage());
		limitSellOrder.setPairOrderId(buyOrder.getOrderId());
		limitSellOrder.setTif(TimeInForceType.GTC);
		
		StockOrder stopSellOrder = new StockOrder();
		stopSellOrder.setSubmitTime(null);
		stopSellOrder.setSymbol(stockid);
		stopSellOrder.setAction(ActionType.sell);
		stopSellOrder.setTif(TimeInForceType.GTC);
		stopSellOrder.setPairOrderId(buyOrder.getOrderId());
		if (ss.trailing){
			stopSellOrder.setOrderType(OrderType.stoptrailingpercentage);
			stopSellOrder.setIncrementPercent(-1*ss.getStopPercentage());
		}else{
			stopSellOrder.setOrderType(OrderType.stoplimit);
			stopSellOrder.setLimitPercentage(-1*ss.getStopPercentage());
			stopSellOrder.setPairOrderId(buyOrder.getOrderId());
		}
		sol.add(limitSellOrder);
		sol.add(stopSellOrder);
		
		StockOrder forceCleanSellOrder = new StockOrder();
		forceCleanSellOrder.setTif(TimeInForceType.MarktOnClose);//of the last day
		forceCleanSellOrder.setSubmitTime(null);
		forceCleanSellOrder.setSymbol(stockid);
		forceCleanSellOrder.setAction(ActionType.sell);
		forceCleanSellOrder.setPairOrderId(buyOrder.getOrderId());
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

	public int getSelectNumber() {
		return selectNumber;
	}

	public void setSelectNumber(int selectNumber) {
		this.selectNumber = selectNumber;
	}

	public boolean isTrailing() {
		return trailing;
	}

	public void setTrailing(boolean trailing) {
		this.trailing = trailing;
	}

	public float getStopPercentage() {
		return stopPercentage;
	}

	public void setStopPercentage(float stopPercentage) {
		this.stopPercentage = stopPercentage;
	}

	public String getHoldUnit() {
		return holdUnit;
	}

	public void setHoldUnit(String holdUnit) {
		this.holdUnit = holdUnit;
	}
}
