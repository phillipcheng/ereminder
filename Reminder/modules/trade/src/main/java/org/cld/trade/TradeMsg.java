package org.cld.trade;

import java.util.Date;
import java.util.Map;

import org.cld.stock.trade.StockOrder;


public class TradeMsg {
	private TradeMsgType msgType;
	private StockOrderType orderType;
	private String orderId;
	private String symbol;
	private float price;
	private Map<String, StockOrder> somap; //the somap context
	private String targetMsgId;
	
	Date updateDt;
	
	public TradeMsg(TradeMsgType msgType, StockOrderType orderType, String orderId, String symbol, float price, Map<String, StockOrder> somap){
		this.msgType = msgType;
		this.orderType = orderType;
		this.orderId = orderId;
		this.symbol = symbol;
		this.price = price;
		this.setSomap(somap);
	}
	
	public String getMsgId(){
		return String.format("%s_%s_%s", msgType, orderType, symbol);
	}

	public String toString(){
		return String.format("SM:%s,%s,%s,%.2f", msgType, orderId, symbol, price);
	}


	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public Date getUpdateDt() {
		return updateDt;
	}
	public void setUpdateDt(Date updateDt) {
		this.updateDt = updateDt;
	}
	public Map<String, StockOrder> getSomap() {
		return somap;
	}
	public void setSomap(Map<String, StockOrder> somap) {
		this.somap = somap;
	}
	public String getTargetMsgId() {
		return targetMsgId;
	}
	public void setTargetMsgId(String targetMsgId) {
		this.targetMsgId = targetMsgId;
	}
	public TradeMsgType getMsgType() {
		return msgType;
	}
	public void setMsgType(TradeMsgType msgType) {
		this.msgType = msgType;
	}
	public StockOrderType getOrderType() {
		return orderType;
	}
	public void setOrderType(StockOrderType orderType) {
		this.orderType = orderType;
	}

}
