package org.cld.trade.response;

import java.util.Map;

public class Holding {
	private String symbol;
	private int qty;
	
	/*
    <holding>
	    <accounttype>2</accounttype>
	    <costbasis>30356.34</costbasis>
	    <gainloss>-1468.3400000000001</gainloss>
	    <instrument>
	      <cusip>33812L102</cusip>
	      <desc>FITBIT INC</desc>
	      <factor>0.0</factor>
	      <sectyp>CS</sectyp>
	      <sym>FIT</sym>
	    </instrument>
	    <marketvalue>28888.0</marketvalue>
	    <marketvaluechange>-1080.0</marketvaluechange>
	    <price>36.11</price>
	    <purchaseprice>37.945425</purchaseprice>
	    <qty>800.0</qty>
	    <quote>
	      <change>0.0</change>
	      <extendedquote>
	        <dividenddata/>
	      </extendedquote>
	      <format/>
	      <lastprice>36.11</lastprice>
	    </quote>
	    <sodcostbasis>0.0</sodcostbasis>
	    <underlying/>
  	</holding>
  */
	public static final String QTY="qty";
	public static final String INSTRUMENT="instrument";
	public static final String SYM="sym";
	public Holding(Map<String, Object> map){
		String sqty = (String) map.get(QTY);
		qty = (int) Float.parseFloat(sqty);
		Map<String, Object> insmap = (Map<String, Object>) map.get(INSTRUMENT);
		symbol = (String) insmap.get(SYM);
	}
	public String toString(){
		return String.format("H:%s,%d", symbol, qty);
	}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
}
