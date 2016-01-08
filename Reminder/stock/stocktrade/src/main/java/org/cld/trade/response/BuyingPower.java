package org.cld.trade.response;

import java.util.Map;

public class BuyingPower {
	
	private static final String cashavailableforwithdrawal_="cashavailableforwithdrawal";
	private static final String equitypercentage_="equitypercentage";
	private static final String soddaytrading_="soddaytrading";
	private static final String sodstock_="sodstock";
	private static final String sodoptions_="sodoptions";
	private static final String daytrading_="daytrading";
	private static final String stock_="stock";
	private static final String options_="options";
	
	private float cashavailableforwithdrawal;
	private float equitypercentage;
	private float soddaytrading;
	private float sodstock;
	private float sodoptions;
	private float daytrading;
	private float stock;
	private float options;
	
	
	public BuyingPower(Map<String, Object> map){
		cashavailableforwithdrawal = Float.parseFloat((String) map.get(cashavailableforwithdrawal_));
		equitypercentage = Float.parseFloat((String) map.get(equitypercentage_));
		soddaytrading = Float.parseFloat((String) map.get(soddaytrading_));
		sodstock = Float.parseFloat((String) map.get(sodstock_));
		sodoptions = Float.parseFloat((String) map.get(sodoptions_));
		daytrading = Float.parseFloat((String) map.get(daytrading_));
		stock = Float.parseFloat((String) map.get(stock_));
		options = Float.parseFloat((String) map.get(options_));
	}
	
	public String toString(){
		return String.format("cashavailableforwithdrawal:%.2f,equitypercentage:%.2f,soddaytrading:%.2f,sodstock:%.2f,"
				+ "sodoptions:%.2f,daytrading:%.2f,stock:%.2f,options:%.2f", cashavailableforwithdrawal,equitypercentage,soddaytrading,sodstock,
				sodoptions, daytrading, stock, options);
	}

	public float getCashavailableforwithdrawal() {
		return cashavailableforwithdrawal;
	}


	public void setCashavailableforwithdrawal(float cashavailableforwithdrawal) {
		this.cashavailableforwithdrawal = cashavailableforwithdrawal;
	}


	public float getEquitypercentage() {
		return equitypercentage;
	}


	public void setEquitypercentage(float equitypercentage) {
		this.equitypercentage = equitypercentage;
	}


	public float getSoddaytrading() {
		return soddaytrading;
	}


	public void setSoddaytrading(float soddaytrading) {
		this.soddaytrading = soddaytrading;
	}


	public float getSodstock() {
		return sodstock;
	}


	public void setSodstock(float sodstock) {
		this.sodstock = sodstock;
	}


	public float getSodoptions() {
		return sodoptions;
	}


	public void setSodoptions(float sodoptions) {
		this.sodoptions = sodoptions;
	}


	public float getDaytrading() {
		return daytrading;
	}


	public void setDaytrading(float daytrading) {
		this.daytrading = daytrading;
	}


	public float getStock() {
		return stock;
	}


	public void setStock(float stock) {
		this.stock = stock;
	}


	public float getOptions() {
		return options;
	}


	public void setOptions(float options) {
		this.options = options;
	}
	
}
