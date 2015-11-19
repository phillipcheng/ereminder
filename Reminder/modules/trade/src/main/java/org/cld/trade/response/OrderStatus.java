package org.cld.trade.response;

public class OrderStatus {

	public static final String REJECTED="8";
	public static final String CANCELED="4";
	public static final String FILLED="2";
	public static final String OPEN="0";
	public static final String PENDING="A";
	
	String orderId;
	int cumQty;
	float avgPrice;
	String stat;
	
	public OrderStatus(String orderId, int cumQty, float avgPrice, String stat){
		this.orderId = orderId;
		this.cumQty = cumQty;
		this.avgPrice = avgPrice;
		this.stat = stat;
	}
	
	public String toString(){
		return String.format("OS:%s,%d,%.2f,%s", orderId, cumQty, avgPrice, stat);
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

}
