package org.cld.stock.trade;

import java.util.Date;
import java.util.List;

import org.cld.stock.strategy.SellStrategy;

public class BuySellInfo {
	
	private String bs;//name, parameters
	private SellStrategy ss;
	private List<StockOrder> sos;
	private Date submitD;
	
	public BuySellInfo(String bs, SellStrategy ss, List<StockOrder> sos, Date d){
		this.setBs(bs);
		this.sos = sos;
		this.setSs(ss);
		this.setSubmitD(d);
	}
	
	public List<StockOrder> getSos() {
		return sos;
	}
	public void setSos(List<StockOrder> sos) {
		this.sos = sos;
	}

	public String getBs() {
		return bs;
	}

	public void setBs(String bs) {
		this.bs = bs;
	}

	public SellStrategy getSs() {
		return ss;
	}

	public void setSs(SellStrategy ss) {
		this.ss = ss;
	}

	public Date getSubmitD() {
		return submitD;
	}

	public void setSubmitD(Date submitD) {
		this.submitD = submitD;
	}


}
