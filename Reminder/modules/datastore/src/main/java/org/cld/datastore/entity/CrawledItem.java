package org.cld.datastore.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="type",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("item")
@Table(name = "CrawledItem")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrawledItem {

	private static Logger logger =  LogManager.getLogger(CrawledItem.class);
	
	@JsonIgnore
	@EmbeddedId
	protected CrawledItemId id;

	@Column(insertable=false, updatable=false)
	protected String type;//discriminator type: category, product

	@Column(name = "itemType")
	protected String itemType; //the type of the product: book, stock, etc

	@Column(name = "rootTaskId")
	protected String rootTaskId;
	
	@Column(name = "name")
	protected String name;

	@Column(name = "fullUrl")
	protected String fullUrl;

	@Column(name = "parentCatId")
	protected String parentCatId;//parent category's internal catId
	
	@Column(name="updateTime")
	protected Date updateTime; //time on the page, this item is updated differ than crawl time, time i found it
	
	@Column(name = "paramData", length = 80000)
	private String paramData; //json format data of paramMap
	
	@JsonIgnore
	private transient Map<String, Object> params = new TreeMap<String, Object>();//I need the order of keys
	
	private List<String[]> csvValue;//list of key,value pairs for hdfs to save
	
	//called by product, category's default constructor for hibernate
	public CrawledItem(){
		
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
		return toJson(true);
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
	@JsonIgnore
	public Map<String, Object> getParamMap(){
		return params;
	}
	
	private JSONArray toJsonArray(List list){
		JSONArray js = new JSONArray();
		for (Object obj:list){
			if (obj instanceof List){
				js.put(toJsonArray((List)obj));
			}else{
				js.put(obj);
			}
		}
		return js;
	}
	
	//serialize the paramMap to json param data, only selected types (now string) will be stored
	public void toParamData(){
		JSONObject jobj = new JSONObject();
		for (String key: params.keySet()){
			Object val = params.get(key);
			if (val instanceof String){
				jobj.put(key, (String)val);
			}else if (val instanceof Float){
				jobj.put(key, ((Float) val).floatValue());
			}else if (val instanceof List){
				jobj.put(key, toJsonArray((List)val));
			}else if (val instanceof JSONArray){
				jobj.put(key, val);
			}else if (val instanceof Integer){
				jobj.put(key, val);
			}else{
				logger.warn(String.format("type not supported for json serialization: %s:%s", key, val));
			}
		}
		paramData = jobj.toString();
	}
	
	//deserialize
	public void fromParamData(){
		if (paramData!=null){
			try{
				JSONObject jobj = new JSONObject(paramData);
				String[] names = JSONObject.getNames(jobj);
				if (names!=null){
					for (String name:names){
						Object o = jobj.opt(name);
						if (o instanceof JSONArray){
							List<String> listdata = new ArrayList<String>();     
							JSONArray jArray = (JSONArray)o; 
							if (jArray != null) { 
							   for (int i=0;i<jArray.length();i++){ 
							    listdata.add(jArray.get(i).toString());
							   } 
							}
							params.put(name, listdata);
						}else{
							params.put(name, o);
						}
					}
				}
			}catch(Exception e){
				logger.error("the paramData is:" + paramData, e);
			}
		}
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
	
	@JsonIgnore
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
	
	
	public String toJson(boolean needParamToData){
		if (needParamToData)
			toParamData();
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
			ci.fromParamData();
			return ci;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	public List<String[]> getCsvValue() {
		return csvValue;
	}

	public void setCsvValue(List<String[]> csvValue) {
		this.csvValue = csvValue;
	}


}
