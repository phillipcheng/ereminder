package org.cld.stock.strategy;

public class OrderFilled {
	String symbol;
	int cumQty;
	float avgPrice;
	StockOrder.ActionType side;//buy,sell
	StockOrder.OrderType typ;
	
	public OrderFilled(String symbol, int cumQty, float avgPrice, StockOrder.ActionType side, StockOrder.OrderType typ){
		this.symbol = symbol;
		this.cumQty = cumQty;
		this.avgPrice = avgPrice;
		this.side = side;
		this.typ = typ;
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
	public StockOrder.ActionType getSide() {
		return side;
	}
	public void setSide(StockOrder.ActionType side) {
		this.side = side;
	}
	public StockOrder.OrderType getTyp() {
		return typ;
	}
	public void setTyp(StockOrder.OrderType typ) {
		this.typ = typ;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
