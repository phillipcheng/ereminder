package org.cld.stock.strategy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BuySellInfo {
	
	private String bs;//name, parameters
	private SellStrategy ss;
	private List<StockOrder> sos;
	private Date submitD;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public BuySellInfo(String bs, SellStrategy ss, List<StockOrder> sos, Date d){
		this.setBs(bs);
		this.sos = sos;
		this.setSs(ss);
		this.setSubmitD(d);
	}
	
	public String toString(){
		return String.format("bs:%s, ss:%s, sos:%s, submitDate:%s", bs, ss.toString(), sos, sdf.format(submitD));
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
