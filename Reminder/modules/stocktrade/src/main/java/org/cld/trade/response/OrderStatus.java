package org.cld.trade.response;

import org.cld.stock.strategy.StockOrder;

public class OrderStatus {

	public static final String REJECTED="8";
	public static final String CANCELED="4";
	public static final String FILLED="2";
	public static final String OPEN="0";
	public static final String PENDING="A";
	
	String symbol;
	String orderId;
	int cumQty;
	float avgPrice;
	String stat;
	String side;
	String typ;
	
	public static String toStatus(StockOrder.StatusType soStatus){
		if (soStatus == StockOrder.StatusType.executed){
			return FILLED;
		}else if (soStatus == StockOrder.StatusType.open){
			return OPEN;
		}else if (soStatus == StockOrder.StatusType.cancelled){
			return CANCELED;
		}else{
			return null;
		}
	}
	
	public OrderStatus(String orderId, String symbol, int cumQty, float avgPrice, String stat, String side, String typ){
		this.orderId = orderId;
		this.symbol = symbol;
		this.cumQty = cumQty;
		this.avgPrice = avgPrice;
		this.stat = stat;
		this.side = side;
		this.typ = typ;
	}
	
	public String toString(){
		return String.format("OS:%s,%s, %d,%.2f,%s,%s,%s", orderId, symbol, cumQty, avgPrice, stat, side, typ);
	}

	public int getCumQty() {
		return cumQty;
	}
	public void setCumQty(int cumQty) {
		this.cumQty = cumQty;
	}
	public float getAvgPrice() {
		return avgPrice;
	}
	public void setAvgPrice(float avgPrice) {
		this.avgPrice = avgPrice;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public String getTyp() {
		return typ;
	}
	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}
