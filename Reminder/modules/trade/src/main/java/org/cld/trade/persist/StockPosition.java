package org.cld.trade.persist;

import java.util.Date;

public class StockPosition {
	int orderQty;
	float orderPrice;
	Date dt;//update time
	String symbol;
	int isOpenPos;
	
	public StockPosition(Date dt, int orderQty, float orderPrice, String symbol, int isOpenPos){
		this.dt = dt;
		this.orderQty=orderQty;
		this.orderPrice=orderPrice;
		this.symbol = symbol;
		this.isOpenPos=isOpenPos;
	}
	
	public Date getDt() {
		return dt;
	}
	public void setDt(Date dt) {
		this.dt = dt;
	}
	public int getOrderQty() {
		return orderQty;
	}
	public void setOrderQty(int orderQty) {
		this.orderQty = orderQty;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getIsOpenPos() {
		return isOpenPos;
	}
	public void setIsOpenPos(int isOpenPos) {
		this.isOpenPos = isOpenPos;
	}
	public float getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(float orderPrice) {
		this.orderPrice = orderPrice;
	}
}
