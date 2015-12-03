package org.cld.stock.strategy;

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
	private static Logger logger =  LogManager.getLogger(SellStrategy.class);

	public static final int HU_DAY=0;
	public static final int HU_MIN=1;
	
	public static String KEY_SELL_STRATEGYS="sellstrategys";
	public static String KEY_SELECT_NUMBER="sls.selectnumber"; //the number of stock selected
	public static String KEY_SELLS_DURATION="sls.duration";
	public static String KEY_SELLS_DURATION_UNIT="sls.duration.unit";
	public static String KEY_SELLS_LIMIT_PERCENTAGE="sls.limitPercentage";
	public static String KEY_SELLS_STOP_PERCENTAGE="sls.stopPercentage";
	public static String KEY_SELLS_ISTRAILING="sls.trailing";
	

	private int selectNumber=1;
	private int holdDuration=0;
	private int holdUnit = HU_DAY;
	private float limitPercentage=0;//% unit
	private float stopPercentage=0; //%unit
	private boolean trailing=false;
	
	public SellStrategy(){
	}
	
	public SellStrategy(int selectNumber, int holdDuration, int unit, float limitPercentage, float stopPercentage, boolean trailing){
		this.selectNumber = selectNumber;
		this.holdDuration = holdDuration;
		this.setHoldUnit(unit);
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
		String[] selectNumbers = new String[]{"1"};
		String[] durations = new String[]{"0"};
		String[] limitPercentages = new String[]{"0"};
		String[] stopPercentage = new String[]{"0"};
		String[] trailings = new String[]{"true"};
		
		String strSelectNumber = props.getString(SellStrategy.KEY_SELECT_NUMBER);
		if (strSelectNumber!=null){
			selectNumbers = StringUtil.parseFloatSteps(strSelectNumber);
		}
		String strDurations = props.getString(SellStrategy.KEY_SELLS_DURATION);
		if (strDurations!=null){
			durations = StringUtil.parseFloatSteps(strDurations);
		}
		int hu = props.getInt(SellStrategy.KEY_SELLS_DURATION_UNIT, 0);
		String strLimitPercents = props.getString(SellStrategy.KEY_SELLS_LIMIT_PERCENTAGE);
		if (strLimitPercents!=null){
			limitPercentages = StringUtil.parseFloatSteps(strLimitPercents);
		}
		String strStopPercents = props.getString(SellStrategy.KEY_SELLS_STOP_PERCENTAGE);
		if (strStopPercents!=null){
			stopPercentage = StringUtil.parseFloatSteps(strStopPercents);
		}
		String strTrailing = props.getString(SellStrategy.KEY_SELLS_ISTRAILING);
		if (strTrailing!=null){
			trailings = StringUtil.parseSteps(strTrailing);
		}
		List<SellStrategy> ssl = new ArrayList<SellStrategy>();
		for (String sselectNumber:selectNumbers){
			for (String sduration:durations){
				for (String slp:limitPercentages){
					for (String sstp:stopPercentage){
						for (String trail:trailings){
							boolean btrail = Boolean.parseBoolean(trail);
							float lp = Float.parseFloat(slp);
							float stp = Float.parseFloat(sstp);
							int selectNumber = (int) Float.parseFloat(sselectNumber);
							int duration = (int) Float.parseFloat(sduration);
							if (lp>stp){//sell limit percent should be bigger then stop loss percent
								SellStrategy sls = new SellStrategy();
								sls.setSelectNumber((int)selectNumber);
						        sls.setHoldDuration((int) duration);
						        sls.setHoldUnit(hu);
						        sls.setLimitPercentage(lp);
						        sls.setStopPercentage(stp);
						        sls.setTrailing(btrail);
						        ssl.add(sls);
							}
						}
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
		String stockid = scr.getStockId();
		float buyLimit = scr.getBuyPrice();
		buyOrder.setStockid(stockid);
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
		forceCleanSellOrder.setStockid(stockid);
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
		limitSellOrder.setStockid(stockid);
		limitSellOrder.setAction(ActionType.sell);
		limitSellOrder.setOrderType(OrderType.limit);
		limitSellOrder.setQuantity(qty);
		limitSellOrder.setLimitPrice((float) (buyPrice*(1+ss.getLimitPercentage()*0.01)));
		limitSellOrder.setTif(TimeInForceType.DayOrder);
		sol.add(limitSellOrder);
		
		StockOrder stopSellOrder = new StockOrder();
		stopSellOrder.setSubmitTime(buyDate);
		stopSellOrder.setStockid(stockid);
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
		stopSellOrder.setTif(TimeInForceType.DayOrder);
		sol.add(stopSellOrder);
		sol.add(makeForceSellOrder(stockid, qty));
		
		return sol;
	}
	
	//by my simulator
	public static List<StockOrder> makeStockOrders(SelectCandidateResult scr, SellStrategy ss){
		ArrayList<StockOrder> sol = new ArrayList<StockOrder>();
		StockOrder buyOrder = new StockOrder();
		String stockid = scr.getStockId();
		float buyLimit = scr.getBuyPrice();
		buyOrder.setStockid(stockid);
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
		limitSellOrder.setStockid(stockid);
		limitSellOrder.setAction(ActionType.sell);
		limitSellOrder.setOrderType(OrderType.limit);
		limitSellOrder.setLimitPercentage(ss.getLimitPercentage());
		limitSellOrder.setPairOrderId(buyOrder.getOrderId());
		limitSellOrder.setTif(TimeInForceType.GTC);
		
		StockOrder stopSellOrder = new StockOrder();
		stopSellOrder.setSubmitTime(null);
		stopSellOrder.setStockid(stockid);
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
		forceCleanSellOrder.setStockid(stockid);
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

	public int getHoldUnit() {
		return holdUnit;
	}

	public void setHoldUnit(int holdUnit) {
		this.holdUnit = holdUnit;
	}
}
