package org.cld.trade.response;

import java.util.Map;

public class OrderResponse {
	
	public static final String ID="@id";
	public static final String ELAPSEDTIME="elapsedtime";
	public static final String CLIENTORDERID="clientorderid";
	public static final String ORDERSTATUS="orderstatus";
	public static final String ERROR="error";
	
	private String id;
	private long elapsedTime;
	private String clientorderid;
	private String orderstatus;
	private String error;
	
	public static final String SUCCESS="Success";
	public OrderResponse(Map<String, Object> map){
		id = (String) map.get(ID);
		elapsedTime = Long.parseLong((String) map.get(ELAPSEDTIME));
		clientorderid = (String) map.get(CLIENTORDERID);
		orderstatus = (String) map.get(ORDERSTATUS);
		error = (String) map.get(ERROR);
	}
	
	public String toString(){
		return String.format("ORsp:%s,%d,%s,%s,%s", id, elapsedTime, clientorderid, orderstatus, error);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getElapsedTime() {
		return elapsedTime;
	}
	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	public String getClientorderid() {
		return clientorderid;
	}
	public void setClientorderid(String clientorderid) {
		this.clientorderid = clientorderid;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getOrderstatus() {
		return orderstatus;
	}
	public void setOrderstatus(String orderstatus) {
		this.orderstatus = orderstatus;
	}
}
