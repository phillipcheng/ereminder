package org.cld.stock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TradeTick implements TimedItem {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public TradeTick(Date d, float last, long vl){
		this.datetime = d;
		this.last = last;
		this.vl = vl;
	}
	
	private Date datetime;
	private float last;
	private long vl;
	
	@Override
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	public float getLast() {
		return last;
	}
	public void setLast(float last) {
		this.last = last;
	}
	public long getVl() {
		return vl;
	}
	public void setVl(long vl) {
		this.vl = vl;
	}
	
	@Override
	public String toCsv(TimeZone tz) {
		sdf.setTimeZone(tz);
		return String.format("%s,%.3f,%d", sdf.format(datetime), last, vl);
	}
}
