package org.cld.stock.common;

import java.util.Date;
import java.util.TimeZone;

public interface TimedItem {
	public Date getDatetime();
	public String toCsv(TimeZone tz);
}
