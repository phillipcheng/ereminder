package org.cld.stock.common;

import java.util.Date;

import org.cld.stock.strategy.IntervalUnit;

public class StockDataConfig {
	
	private Date startDt;
	private Date endDt;
	private String stockId;
	private String baseMarketId;
	private IntervalUnit unit;
	
	
	public Date getStartDt() {
		return startDt;
	}
	public void setStartDt(Date startDt) {
		this.startDt = startDt;
	}
	public Date getEndDt() {
		return endDt;
	}
	public void setEndDt(Date endDt) {
		this.endDt = endDt;
	}
	public String getStockId() {
		return stockId;
	}
	public void setStockId(String stockId) {
		this.stockId = stockId;
	}
	public IntervalUnit getUnit() {
		return unit;
	}
	public void setUnit(IntervalUnit unit) {
		this.unit = unit;
	}
	public String getBaseMarketId() {
		return baseMarketId;
	}
	public void setBaseMarketId(String baseMarketId) {
		this.baseMarketId = baseMarketId;
	}
	
	

}
