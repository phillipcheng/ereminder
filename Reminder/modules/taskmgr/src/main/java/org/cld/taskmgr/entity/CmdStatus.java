package org.cld.taskmgr.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.CrawledItemId;


/*
	CrawledId: storeid:'CmdStatus', id:marketId + cmdName, createTime is the 'endTime'
	Params: startTime, Map<JobId, Status>
	Status:
	RUNNING(1),
   SUCCEEDED(2),
   FAILED(3),
   PREP(4),
   KILLED(5);
*/
public class CmdStatus extends CrawledItem{
	protected static Logger logger =  LogManager.getLogger(CmdStatus.class);
	public static final String CRAWLITEM_TYPE="org.cld.taskmgr.entity.CmdStatus";
	public static final String STORE_ID="CmdStatus";
	
	private static final String sep="|";
	private String marketId;
	private String cmdName;

	private Map<String, Integer> jsMap = new HashMap<String, Integer>();
	private Date startTime;
	private Date endTime;
	
	//default constructor for json
	public CmdStatus(){	
	}
	
	//timeless cmd, static info, only related to market, not related to time
	public CmdStatus(String marketId, String cmdName, Date endTime, Date startTime, boolean timeless, SimpleDateFormat sdf){
		super(CRAWLITEM_TYPE, "default");
		if (timeless){
			this.setId(new CrawledItemId(getId(marketId, cmdName), STORE_ID, endTime));
		}else{
			this.setId(new CrawledItemId(getId(marketId, cmdName, endTime, sdf), STORE_ID, endTime));
		}
		this.marketId=marketId;
		this.cmdName=cmdName;
		this.startTime = startTime;
		this.setEndTime(endTime);
	}
	
	public String toString(){
		return String.format("id: %s, marketId:%s, cmdName:%s, startTime:%s, endTime:%s, jobStatusMap:%s", 
				this.getId(), marketId, cmdName, startTime, endTime, jsMap);
	}
	
	public static String getId(String marketId, String cmdName, Date endTime, SimpleDateFormat sdf){
		String strEndTime = "null";
		if (endTime!=null){
			strEndTime = sdf.format(endTime);
		}
		return marketId + sep + cmdName + sep + strEndTime;
	}
	
	public static String getId(String marketId, String cmdName){
		return marketId + sep + cmdName;
	}
	
	public static CmdStatus getCmdStatus(DataStoreManager dsm, String marketId, String cmd, Date endTime, SimpleDateFormat sdf){
		String id = getId(marketId, cmd, endTime, sdf);
		return (CmdStatus) dsm.getCrawledItem(id, CmdStatus.STORE_ID, CmdStatus.class);
	}
	
	public static CmdStatus getCmdStatus(DataStoreManager dsm, String marketId, String cmd){
		String id = getId(marketId, cmd);
		return (CmdStatus) dsm.getCrawledItem(id, CmdStatus.STORE_ID, CmdStatus.class);
	}
	
	public String getMarketId() {
		return marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

	public String getCmdName() {
		return cmdName;
	}

	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}

	public Map<String, Integer> getJsMap() {
		return jsMap;
	}

	public void setJsMap(Map<String, Integer> jsMap) {
		this.jsMap = jsMap;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
