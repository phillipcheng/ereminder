package org.cld.trade.mgmt;

import javax.management.MXBean;

@MXBean
public interface HistoryDumpMXBean {
	
	public void dumpTickNow();
	
	public long getTotalSize();

}
