package org.cld.taskmgr.entity;

import java.io.Serializable;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BrokenPage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(BrokenPage.class);

	public static final int TYPE_CAT=1;
	public static final int TYPE_LIST=2;
	public static final int TYPE_DETAIL=3;
	public static final int TYPE_PROM=4;
	
	
	private int type;
	private String url;
	private Date lastUpdateTime;
	private int count;
	
	public String toString(){
		return "type:" + type + "\n" +
				"url:" + url + "\n" +
				 "lastUpdateTime:" + lastUpdateTime + "\n" +
				 "count" + count + "\n";
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
