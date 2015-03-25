package org.cld.util;

import java.util.Date;

public class DateTimeRange {
	
	private Date fromDT;
	private Date toDT;
	
	public DateTimeRange(Date f, Date t){
		this.fromDT= f;
		this.toDT = t;
	}
	
	public Date getFromDT() {
		return fromDT;
	}
	public void setFromDT(Date fromDT) {
		this.fromDT = fromDT;
	}
	public Date getToDT() {
		return toDT;
	}
	public void setToDT(Date toDT) {
		this.toDT = toDT;
	}
	

}
