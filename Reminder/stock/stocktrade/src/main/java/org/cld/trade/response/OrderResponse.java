package org.cld.trade.response;

import java.util.Map;

import org.json.JSONObject;

public class OrderResponse {
	
	public static final String ID="@id";
	public static final String ELAPSEDTIME="elapsedtime";
	public static final String CLIENTORDERID="clientorderid";
	public static final String ORDERSTATUS="orderstatus";
	public static final String ERROR="error";
	
	private String clientorderid;
	private String error;
	
	public static final String SUCCESS="Success";
	
	public OrderResponse(String clientorderid, String error){
		this.clientorderid = clientorderid;
		this.error = error;
	}
	
	public OrderResponse(Map<String, Object> map){
		if (!JSONObject.NULL.equals(map.get(CLIENTORDERID))){
			clientorderid = (String) map.get(CLIENTORDERID);
		}
		error = (String) map.get(ERROR);
	}
	
	public String toString(){
		return String.format("ORsp:%s,%s", clientorderid, error);
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
}
