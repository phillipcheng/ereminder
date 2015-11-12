package org.cld.trade.persist;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.cld.stock.trade.StockOrder;

public class StockPosition {
	int orderQty;
	float orderPrice;
	Date dt;//update time
	String symbol;
	int isOpenPos;//1 for open position, 2 for closed position, 0 for intention to open (select candidate)
	String orderId;//the order to monitor for future changes, buyOrder for try, sell stop trailling order for open, 
					//real sell order (can be the previous stop trailling) for close or market sell order for limit sell or market on close sell order
	
	public static final int tryOpen=0;
	public static final int open=1;
	public static final int close=2;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public StockPosition(StockOrder so, int isOpenPos, String orderId){
		this.dt = so.getSubmitTime();
		this.orderQty=so.getQuantity();
		this.orderPrice=so.getLimitPrice();
		this.symbol = so.getStockid();
		this.isOpenPos=isOpenPos;
		this.orderId = orderId;
	}
	
	public StockPosition(Date dt, int orderQty, float orderPrice, String symbol, int isOpenPos, String orderId){
		this.dt = dt;
		this.orderQty=orderQty;
		this.orderPrice=orderPrice;
		this.symbol = symbol;
		this.isOpenPos=isOpenPos;
		this.orderId = orderId;
	}
	
	public String toString(){
		return String.format("SP:%s,%s,%d,%.2f,%d,%s", symbol, sdf.format(dt), orderQty, orderPrice, isOpenPos, orderId);
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}
