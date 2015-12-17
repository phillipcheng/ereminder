package org.cld.trade.persist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import org.cld.stock.strategy.StockOrder;
import org.cld.trade.StockOrderType;
import org.cld.util.JsonUtil;

public class StockPosition {
	String symbol;
	int orderQty;
	float orderPrice;
	Date buySubmitDt;//buy submit datetime
	String buyOrderId;
	String stopSellOrderId;
	String limitSellOrderId;
	Map<String, StockOrder> soMap;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//for jdbc
	public StockPosition(String symbol, int orderQty, float orderPrice, Date buySubmitDt, 
			String buyOrderId, String stopSellOrderId, String limitSellOrderId, String jsonSOs){
		this.symbol = symbol;
		this.orderQty = orderQty;
		this.orderPrice = orderPrice;
		this.buySubmitDt = buySubmitDt;
		this.buyOrderId = buyOrderId;
		this.stopSellOrderId = stopSellOrderId;
		this.limitSellOrderId = limitSellOrderId;
		soMap = (Map<String, StockOrder>) JsonUtil.objFromJson(jsonSOs, Map.class);
	}
	
	public StockPosition(String symbol, int orderQty, float orderPrice, Date buySubmitDt, 
			String buyOrderId, String stopSellOrderId, String limitSellOrderId, Map<String, StockOrder> soMap){
		this.symbol = symbol;
		this.orderQty = orderQty;
		this.orderPrice = orderPrice;
		this.buySubmitDt = buySubmitDt;
		this.buyOrderId = buyOrderId;
		this.stopSellOrderId = stopSellOrderId;
		this.limitSellOrderId = limitSellOrderId;
		this.soMap = soMap;
	}
	public String toString(){
		return String.format("SP:%s,%d,%.2f,%s,%s,%s,%s", symbol, orderQty, orderPrice, sdf.format(buySubmitDt), 
				buyOrderId, stopSellOrderId, limitSellOrderId);
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(int orderQty) {
		this.orderQty = orderQty;
	}

	public float getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(float orderPrice) {
		this.orderPrice = orderPrice;
	}

	public Date getBuySubmitDt() {
		return buySubmitDt;
	}

	public void setBuySubmitDt(Date buySubmitDt) {
		this.buySubmitDt = buySubmitDt;
	}

	public String getBuyOrderId() {
		return buyOrderId;
	}

	public void setBuyOrderId(String buyOrderId) {
		this.buyOrderId = buyOrderId;
	}

	public String getStopSellOrderId() {
		return stopSellOrderId;
	}

	public void setStopSellOrderId(String stopSellOrderId) {
		this.stopSellOrderId = stopSellOrderId;
	}

	public String getLimitSellOrderId() {
		return limitSellOrderId;
	}

	public void setLimitSellOrderId(String limitSellOrderId) {
		this.limitSellOrderId = limitSellOrderId;
	}

	public Map<String, StockOrder> getSoMap() {
		return soMap;
	}
	public void setSoMap(Map<String, StockOrder> soMap) {
		this.soMap = soMap;
	}
}
