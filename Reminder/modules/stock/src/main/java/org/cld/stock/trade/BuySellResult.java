package org.cld.stock.trade;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.cld.stock.trade.StockOrder.OrderType;

public class BuySellResult {
	private Date submitD;
	private String stockid;
	private Date buyTime;
	private float buyPrice;
	private Date sellTime;
	private String sellOrderType;//it can be ordertype or timeinforcetype if ordertype is null
	private float sellPrice;
	private float percent;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public BuySellResult(Date submitD, String stockid, Date buyTime, float buyPrice, Date sellTime, float sellPrice, String sellOrderType, float percent){
		this.submitD = submitD;
		this.stockid = stockid;
		this.buyTime = buyTime;
		this.buyPrice = buyPrice;
		this.sellTime = sellTime;
		this.sellPrice = sellPrice;
		this.setSellOrderType(sellOrderType);
		this.percent = percent;
	}

	public String toString(){
		return String.format("%s, %s, %s, %s, %s, %s, %s, %s", sdf.format(getSubmitD()), stockid, 
				sdf.format(getBuyTime()), getBuyPrice(), sdf.format(getSellTime()), getSellOrderType(), getSellPrice(), getPercent());
	}
	public Date getSubmitD() {
		return submitD;
	}

	public void setSubmitD(Date submitD) {
		this.submitD = submitD;
	}

	public String getStockid() {
		return stockid;
	}

	public void setStockid(String stockid) {
		this.stockid = stockid;
	}

	public Date getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(Date buyTime) {
		this.buyTime = buyTime;
	}

	public float getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(float buyPrice) {
		this.buyPrice = buyPrice;
	}

	public Date getSellTime() {
		return sellTime;
	}

	public void setSellTime(Date sellTime) {
		this.sellTime = sellTime;
	}



	public float getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(float sellPrice) {
		this.sellPrice = sellPrice;
	}

	public float getPercent() {
		return percent;
	}

	public void setPercent(float percent) {
		this.percent = percent;
	}

	public String getSellOrderType() {
		return sellOrderType;
	}

	public void setSellOrderType(String sellOrderType) {
		this.sellOrderType = sellOrderType;
	}
}
