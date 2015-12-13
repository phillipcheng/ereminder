package org.cld.stock;

import java.util.Date;
import java.util.TimeZone;

public interface TimedItem {
	public Date getDatetime();
	public String toCsv(TimeZone tz);
}
