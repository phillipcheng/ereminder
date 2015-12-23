package org.cld.trade.persist;

import java.util.Date;
import java.util.Map;

import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.StockOrder;
import org.cld.util.JsonUtil;

public class StockPosition {
	private String buyOrderId = null;
	private String stopSellOrderId = null;
	private String limitSellOrderId = null;
	private SelectCandidateResult scr;
	private String bsName;
	private Map<String, StockOrder> soMap;
	
	//for jdbc
	public StockPosition(String symbol, int orderQty, float orderPrice, Date buySubmitDt, 
			String buyOrderId, String stopSellOrderId, String limitSellOrderId, String bsName, String jsonSOs){
		this.buyOrderId = buyOrderId;
		this.stopSellOrderId = stopSellOrderId;
		this.limitSellOrderId = limitSellOrderId;
		scr = new SelectCandidateResult(symbol, buySubmitDt, 0f, orderPrice);
		this.bsName = bsName;
		soMap = (Map<String, StockOrder>) JsonUtil.objFromJson(jsonSOs, Map.class);
	}
	
	public StockPosition(SelectCandidateResult scr, String bsName, Map<String, StockOrder> soMap, 
			String buyOrderId){
		this.setBuyOrderId(buyOrderId);
		this.scr = scr;
		this.bsName = bsName;
		this.soMap = soMap;
	}
	
	public String toString(){
		return String.format("SP:%s,%s,%s", scr.toString(), bsName, soMap.toString());
	}

	public Map<String, StockOrder> getSoMap() {
		return soMap;
	}
	public void setSoMap(Map<String, StockOrder> soMap) {
		this.soMap = soMap;
	}

	public SelectCandidateResult getScr() {
		return scr;
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

	public String getBsName() {
		return bsName;
	}

	public void setBsName(String bsName) {
		this.bsName = bsName;
	}
}
