package org.cld.datastore.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


@XmlRootElement(name = SiteConf.JSON_KEY_SITECONF)
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "siteconf")
public class SiteConf {
	private static Logger logger =  LogManager.getLogger(SiteConf.class);
	
	public static final String JSON_KEY_SITECONF="SiteConf";
	public static final String JSON_KEY_ID="id";
	public static final String JSON_KEY_USERID="userid";
	public static final String JSON_KEY_CONFXML="confxml";
	public static final String JSON_KEY_STATUS="status";
	public static final String JSON_KEY_UTIME="utime";

	public static final String AT="@";
	
	public static final int STATUS_TESTING=0;//involved in testing
	public static final int STATUS_DEPLOYED=1;//deployed in production
	
	private static String format="yyyy-MM-dd:HH:mm:ss";
	private static SimpleDateFormat sdf = new SimpleDateFormat(format);
	
	@Column(name = "id")
	@Id
	@XmlAttribute(name=JSON_KEY_ID)
	private String id;
	
	@Column(name = "userid")
	@XmlAttribute(name=JSON_KEY_USERID)
	private String userid;
	
	@Column(name = "confxml", length=6000)
	@XmlAttribute(name=JSON_KEY_CONFXML)
	private String confxml;
	
	@Column(name = "status")
	@XmlAttribute(name=JSON_KEY_STATUS)
	private int status;
	
	@Column(name = "utime")
	@XmlTransient
	private Date utime;
	
	//for json
	@XmlAttribute(name=JSON_KEY_UTIME)
	private String strUtime;
	public String getStrUtime() {
		return strUtime;
	}
	public void setStrUtime(String strUtime) {
		this.strUtime = strUtime;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("id:");
		sb.append(id + "\n");
		sb.append("userid:");
		sb.append(userid + "\n");
		sb.append("status:");
		sb.append(status + "\n");
		sb.append("utime:");
		sb.append(utime + "\n");
		sb.append("confxml:");
		sb.append(confxml + "\n");
		return sb.toString();		
	}
	
	public String getStatusAsString(){
		if (status==STATUS_TESTING){
			return "testing";
		}else{
			return "deployed"; 
		}
	}
	
	public String getUtimeAsString(){
		return sdf.format(utime);
	}

	public void fromTopJSONObject(JSONObject obj){			
		id = obj.optString(AT+JSON_KEY_ID);
		userid = obj.optString(AT+JSON_KEY_USERID);
		confxml = obj.optString(AT+JSON_KEY_CONFXML);
		status = obj.optInt(AT+JSON_KEY_STATUS);
		strUtime = obj.optString(AT+JSON_KEY_UTIME);
		try{		
			if (strUtime!=null && !"".equals(strUtime)){
				utime = sdf.parse(strUtime);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public static List<SiteConf> fromTopJSONListString(String json){
		JSONObject listObject = new JSONObject(json);
        JSONArray jarray = listObject.getJSONArray(JSON_KEY_SITECONF);
        int len = jarray.length();
        List<SiteConf> sclist = new ArrayList<SiteConf>();
        for (int i=0; i<len; i++){
        	JSONObject obj = jarray.getJSONObject(i);
        	SiteConf sc = new SiteConf();
        	sc.fromTopJSONObject(obj);
        	sclist.add(sc);
        }
        return sclist;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getConfxml() {
		return confxml;
	}
	public void setConfxml(String confxml) {
		this.confxml = confxml;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getUtime() {
		return utime;
	}
	public void setUtime(Date utime) {
		this.utime = utime;
		if (utime!=null){
			strUtime = sdf.format(utime);
		}
	}
	
}