package org.cld.stock.trade;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StockOrder {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public enum ActionType{
		buy,
		sell,
		sellshort,
		buycover,
	}
	public enum OrderType{
		market,
		limit,
		stop,
		stoplimit,
		marketclose,
		stoptrailingdollar,
		stoptrailingpercentage,
		forceclean,//force the position to be cleaned at the end of the duration
	}

	public enum StatusType{
		open,
		executed,
		partialexecuted,
		cancelled
	}
	
	String marketid;//stockbase id: sina, nasdaq
	String orderId; //autogenerated, can't set
	String pairOrderId;
	Date submitTime;
	Date executeTime;
	String stockid;
	int quantity;
	ActionType action;
	OrderType orderType;
	float limitPercentage;//the raised percentage to the corresponding executed buy order
	float limitPrice;
	float stopPrice;
	int duration;//valid till number of days
	StatusType status;
	float executedPrice;
	float incrementPercent;//in 1% unit
	float incrementDollar;//in cent unit
	
	private static Integer idroot=0;
	public static SimpleDateFormat detailSdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
	
	public String toString(){
		String strExeTime = null;
		if (executeTime==null){
			strExeTime = "";
		}else{
			strExeTime = sdf.format(executeTime);
		}
		return String.format("stockid:%s, exePrice:%.2f, exeTime:%s", stockid, executedPrice, strExeTime);
	}
	public StockOrder(){
		String sd = detailSdf.format(new Date());
		synchronized(idroot){
			idroot++;
		}
		orderId = sd + "_" + idroot;
	}
	
	public String getMarketid() {
		return marketid;
	}
	public void setMarketid(String marketid) {
		this.marketid = marketid;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Date getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}
	public String getStockid() {
		return stockid;
	}
	public void setStockid(String stockid) {
		this.stockid = stockid;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public ActionType getAction() {
		return action;
	}
	public void setAction(ActionType action) {
		this.action = action;
	}
	public OrderType getOrderType() {
		return orderType;
	}
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
	public float getLimitPrice() {
		return limitPrice;
	}
	public void setLimitPrice(float limitPrice) {
		this.limitPrice = limitPrice;
	}
	public float getStopPrice() {
		return stopPrice;
	}
	public void setStopPrice(float stopPrice) {
		this.stopPrice = stopPrice;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public StatusType getStatus() {
		return status;
	}
	public void setStatus(StatusType status) {
		this.status = status;
	}
	public float getExecutedPrice() {
		return executedPrice;
	}
	public void setExecutedPrice(float executedPrice) {
		this.executedPrice = executedPrice;
	}
	public float getIncrementPercent() {
		return incrementPercent;
	}
	public void setIncrementPercent(float incrementPercent) {
		this.incrementPercent = incrementPercent;
	}
	public float getIncrementDollar() {
		return incrementDollar;
	}
	public void setIncrementDollar(float incrementDollar) {
		this.incrementDollar = incrementDollar;
	}
	public String getPairOrderId() {
		return pairOrderId;
	}
	public void setPairOrderId(String pairOrderId) {
		this.pairOrderId = pairOrderId;
	}
	public static Integer getIdroot() {
		return idroot;
	}
	public float getLimitPercentage() {
		return limitPercentage;
	}
	public void setLimitPercentage(float limitPercentage) {
		this.limitPercentage = limitPercentage;
	}
	public Date getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}
}
