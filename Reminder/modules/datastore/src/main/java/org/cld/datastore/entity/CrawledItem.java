package org.cld.datastore.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.JsonUtil;
import org.cld.util.StringUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CrawledItem {
	public static final String CRAWLITEM_TYPE="org.cld.datastore.entity.CrawledItem";
	
	private static Logger logger =  LogManager.getLogger(CrawledItem.class);
	
	@JsonIgnore
	protected CrawledItemId id;

	protected String type;//discriminator type: must be the class name: either xxx.CrawledItem, xxx.Category, xxx.Product

	protected String itemType; //the type of the product: default, book, stock, etc

	protected String rootTaskId;
	
	protected String name;

	protected String fullUrl;

	protected String parentCatId;//parent category's internal catId
	
	protected Date updateTime; //time on the page, this item is updated differ than crawl time, time i found it
	
	private String paramData; //json format data of paramMap
	
	private boolean goNext; //do i have a next task
	
	//@JsonIgnore
	private transient Map<String, Object> params = new TreeMap<String, Object>();//I need the order of keys
	
	private String[][] csvValue;//list of key,value pairs for hdfs to save
	
	//called by product, category's default constructor for hibernate
	public CrawledItem(){
		this.type = CRAWLITEM_TYPE;
	}
	
	public CrawledItem(String type){
		this.type = type;
	}
	
	public CrawledItem(String type, String itemType) {
		this.id = new CrawledItemId();
		this.type = type;
		this.itemType = itemType;
	}
	
	public CrawledItem(String type, String itemType, CrawledItemId cid) {
		this(type, itemType);
		this.id = cid;
	}
	
	public String toString(){
		return "CrawledItem:" + toJson();
	}
	

	public void addParam(String key, Object value){
		params.put(key, value);
	}
	public void removeParam(String key){
		params.remove(key);
	}
	public Object getParam(String key){
		return params.get(key);
	}
	//@JsonIgnore
	public Map<String, Object> getParamMap(){
		return params;
	}
	
	//serialize the paramMap to json param data, only selected types (now string) will be stored
	public void toParamData(){
		paramData = JsonUtil.toJsonStringFromMap(params);
	}
	
	//deserialize
	public void fromParamData(){
		params = JsonUtil.fromJsonStringToMap(paramData);
	}
	
	@Override
	public boolean equals(Object o){
		CrawledItem ca = (CrawledItem) o;
		return this.id.equals(ca.id);
	}

	//compare not including the createTime and rootTaskId
	public boolean contentEquals(CrawledItem ci){
		if (ci!=null){
			return this.getId().contentEquals(ci.getId())
					&& StringUtil.nullEquals(this.getFullUrl(), ci.getFullUrl())
					&& StringUtil.nullEquals(this.getName(), ci.getName())
					&& StringUtil.nullEquals(this.getParentCatId(), ci.getParentCatId())
					&& StringUtil.nullEquals(this.type, ci.type)
					&& StringUtil.nullEquals(this.getUpdateTime(), ci.getUpdateTime())
					&& StringUtil.nullEquals(this.getParamData(), ci.getParamData());
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return this.id.hashCode();
	}
	
	//@JsonIgnore
	public CrawledItemId getId() {
		return id;
	}

	public void setId(CrawledItemId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}

	public String getParentCatId() {
		return parentCatId;
	}

	public void setParentCatId(String parentCatId) {
		this.parentCatId = parentCatId;
	}

	public String getRootTaskId() {
		return rootTaskId;
	}

	public void setRootTaskId(String rootTaskId) {
		this.rootTaskId = rootTaskId;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getParamData() {
		return paramData;
	}

	public void setParamData(String paramData) {
		this.paramData = paramData;
	}
	
	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	
	public String getType() {
		return type;
	}
	
	
	public String toJson(){
		List<String> removeKeys = new ArrayList<String>();
		for (String key: getParamMap().keySet()){
			Object o = getParamMap().get(key);
			if (!(o instanceof Serializable)){
				removeKeys.add(key);
			}
		}
		for (String key: removeKeys){
			getParamMap().remove(key);
		}
		ObjectWriter ow = new ObjectMapper().writer().with(new MinimalPrettyPrinter());
		try {
			String json = ow.writeValueAsString(this);
			return json;
		} catch (JsonProcessingException e) {
			logger.error("",e );
			return null;
		}
	}
	
	public static CrawledItem fromJson(String json){
		ObjectMapper mapper = new ObjectMapper();
		try {
			CrawledItem ci =  mapper.readValue(json, CrawledItem.class);
			Class<? extends CrawledItem> clazz = (Class<? extends CrawledItem>) Class.forName(ci.getType());
			ci = mapper.readValue(json, clazz);
			//ci.fromParamData();
			return ci;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	public String[][] getCsvValue() {
		return csvValue;
	}

	public void setCsvValue(String[][] csvValue) {
		this.csvValue = csvValue;
	}

	public boolean isGoNext() {
		return goNext;
	}

	public void setGoNext(boolean goNext) {
		this.goNext = goNext;
	}
}
