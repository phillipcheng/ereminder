package org.cld.stock.strategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.trade.StockOrder;
import org.cld.stock.trade.StockOrder.ActionType;
import org.cld.stock.trade.StockOrder.OrderType;
import org.cld.stock.trade.StockOrder.TimeInForceType;
import org.cld.util.StringUtil;

//for abstract class json mapping
public class SellStrategy {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static Logger logger =  LogManager.getLogger(SellStrategy.class);

	public static String KEY_SELL_STRATEGYS="sellstrategys";
	public static String KEY_SELECT_NUMBER="sls.selectnumber"; //the number of stock selected
	public static String KEY_SELLS_DURATION="sls.duration";
	public static String KEY_SELLS_LIMIT_PERCENTAGE="sls.limitPercentage";
	public static String KEY_SELLS_STOP_PERCENTAGE="sls.stopPercentage";
	public static String KEY_SELLS_ISTRAILING="sls.trailing";
	

	private int selectNumber=1;
	private int holdDuration=0;
	private float limitPercentage=0;//% unit
	private float stopPercentage=0; //%unit
	private boolean trailing=false;
	
	public SellStrategy(){
	}
	
	public SellStrategy(int selectNumber, int holdDuration, float limitPercentage, float stopPercentage, boolean trailing){
		this.selectNumber = selectNumber;
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
		return String.format("%d,%d,%.2f,%b,%.2f", selectNumber, holdDuration, limitPercentage, trailing, stopPercentage);
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
		String strStopTrailingPercents = props.getString(SellStrategy.KEY_SELLS_STOP_PERCENTAGE);
		if (strStopTrailingPercents!=null){
			stopTrailingPercentages = StringUtil.parseSteps(strStopTrailingPercents);
		}
		String strTrailing = props.getString(SellStrategy.KEY_SELLS_ISTRAILING);
		boolean trailing = false;
		if (strTrailing!=null){
			trailing = Boolean.parseBoolean(strTrailing);
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
				        sls.setStopPercentage(stp);
				        sls.setTrailing(trailing);
				        ssl.add(sls);
					}
				}
			}
		}
		SellStrategy[] ssa = new SellStrategy[ssl.size()];
		return ssl.toArray(ssa);
        
	}

	//used by real
	public static StockOrder makeBuyOrder(SelectCandidateResult scr, SellStrategy ss, int cashAmount){
		StockOrder buyOrder = new StockOrder();
		Date dt = null;
		try {
			dt = sdf.parse(scr.getDt());
		} catch (ParseException e) {
			logger.error("", e);
			return null;
		}
		String stockid = scr.getStockId();
		float buyLimit = scr.getBuyPrice();
		buyOrder.setStockid(stockid);
		buyOrder.setAction(ActionType.buy);
		buyOrder.setSubmitTime(dt);
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
	
	//used by real
	public static List<StockOrder> makeSellOrders(String stockid, Date buyDate, int qty, float buyPrice, SellStrategy ss){
		ArrayList<StockOrder> sol = new ArrayList<StockOrder>();
		if (ss.getLimitPercentage()!=0){
			StockOrder limitSellOrder = new StockOrder();
			limitSellOrder.setSubmitTime(buyDate);
			limitSellOrder.setStockid(stockid);
			limitSellOrder.setAction(ActionType.sell);
			limitSellOrder.setOrderType(OrderType.limit);
			limitSellOrder.setQuantity(qty);
			limitSellOrder.setLimitPrice((float) (buyPrice*(1+ss.getLimitPercentage()*0.01)));
			limitSellOrder.setTif(TimeInForceType.DayOrder);
			sol.add(limitSellOrder);
		}
		
		if (ss.getStopPercentage()!=0){
			StockOrder stopSellOrder = new StockOrder();
			stopSellOrder.setSubmitTime(buyDate);
			stopSellOrder.setStockid(stockid);
			stopSellOrder.setAction(ActionType.sell);
			if (ss.isTrailing()){
				stopSellOrder.setOrderType(OrderType.stoptrailingpercentage);
				stopSellOrder.setIncrementPercent(-1*ss.getStopPercentage());
			}else{
				stopSellOrder.setOrderType(OrderType.stoplimit);
				stopSellOrder.setLimitPrice(buyPrice * (1-ss.getStopPercentage()));
			}
			stopSellOrder.setQuantity(qty);
			stopSellOrder.setTif(TimeInForceType.DayOrder);
			sol.add(stopSellOrder);
		}
		
		StockOrder forceCleanSellOrder = new StockOrder();
		forceCleanSellOrder.setTif(TimeInForceType.MarktOnClose);//of the last day
		forceCleanSellOrder.setStockid(stockid);
		forceCleanSellOrder.setAction(ActionType.sell);
		forceCleanSellOrder.setQuantity(qty);
		sol.add(forceCleanSellOrder);
		return sol;
	}
	
	//by my simulator
	public static List<StockOrder> makeStockOrders(SelectCandidateResult scr, SellStrategy ss){
		ArrayList<StockOrder> sol = new ArrayList<StockOrder>();
		StockOrder buyOrder = new StockOrder();
		Date dt = null;
		try {
			dt = sdf.parse(scr.getDt());
		} catch (ParseException e) {
			logger.error("", e);
			return null;
		}
		String stockid = scr.getStockId();
		float buyLimit = scr.getBuyPrice();
		buyOrder.setStockid(stockid);
		buyOrder.setAction(ActionType.buy);
		buyOrder.setSubmitTime(dt);
		buyOrder.setTif(TimeInForceType.DayOrder);
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
			limitSellOrder.setSubmitTime(dt);
			limitSellOrder.setStockid(stockid);
			limitSellOrder.setAction(ActionType.sell);
			limitSellOrder.setOrderType(OrderType.limit);
			limitSellOrder.setLimitPercentage(ss.getLimitPercentage());
			limitSellOrder.setPairOrderId(buyOrder.getOrderId());
			limitSellOrder.setTif(TimeInForceType.GTC);
			sol.add(limitSellOrder);
		}
		
		if (ss.getStopPercentage()!=0){
			StockOrder stopSellOrder = new StockOrder();
			sol.add(stopSellOrder);
			stopSellOrder.setSubmitTime(dt);
			stopSellOrder.setStockid(stockid);
			stopSellOrder.setAction(ActionType.sell);
			stopSellOrder.setTif(TimeInForceType.GTC);
			if (ss.trailing){
				stopSellOrder.setOrderType(OrderType.stoptrailingpercentage);
				stopSellOrder.setIncrementPercent(-1*ss.getStopPercentage());
			}else{
				stopSellOrder.setOrderType(OrderType.stoplimit);
				stopSellOrder.setLimitPercentage(-1*ss.getStopPercentage());
				stopSellOrder.setPairOrderId(buyOrder.getOrderId());
			}
		}
		
		StockOrder forceCleanSellOrder = new StockOrder();
		forceCleanSellOrder.setTif(TimeInForceType.MarktOnClose);//of the last day
		forceCleanSellOrder.setStockid(stockid);
		forceCleanSellOrder.setAction(ActionType.sell);
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
}
