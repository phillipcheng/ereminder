package org.cld.stock;

import java.util.Date;

//Quarterly Earn Event
public class QEarnEvent {
	private AnnounceTime at;
	private String stockid;
	private float eps;//per quarter
	private Date pubDt;
	private Date fiscalQDt;//fiscal quarter end date
	private float consensusEps=NO_VALUE;
	
	public static final float NO_VALUE=-1000;
	
	public QEarnEvent(AnnounceTime at, String stockid, float eps, Date pubDt, Date fiscalQDt, float consensusEps){
		this.at = at;
		this.stockid = stockid;
		this.eps = eps;
		this.pubDt = pubDt;
		this.fiscalQDt = fiscalQDt;
		this.consensusEps = consensusEps;
	}
	
	public AnnounceTime getAt() {
		return at;
	}
	public void setAt(AnnounceTime at) {
		this.at = at;
	}
	public String getStockid() {
		return stockid;
	}
	public void setStockid(String stockid) {
		this.stockid = stockid;
	}
	public float getEps() {
		return eps;
	}
	public void setEps(float eps) {
		this.eps = eps;
	}
	public Date getPubDt() {
		return pubDt;
	}
	public void setPubDt(Date pubDt) {
		this.pubDt = pubDt;
	}
	public Date getFiscalQDt() {
		return fiscalQDt;
	}
	public void setFiscalQDt(Date fiscalQDt) {
		this.fiscalQDt = fiscalQDt;
	}

	public float getConsensusEps() {
		return consensusEps;
	}

	public void setConsensusEps(float consensusEps) {
		this.consensusEps = consensusEps;
	}
}
