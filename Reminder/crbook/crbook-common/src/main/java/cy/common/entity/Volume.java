package cy.common.entity;

import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.cld.util.CompareUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cy.common.persist.RemotePersistManager;

@XmlRootElement(name = Volume.VOLUME_KEY)
@XmlAccessorType(XmlAccessType.FIELD)
public class Volume implements Reading {
	public static final String ROOT_VOLUME_PREFIX="99999";
	
	public static final String ROOT_VOLUME_LHH="999999"; //lian huan hua
	public static final String ROOT_VOLUME_MH="999998"; // online man hua
	public static final String ROOT_VOLUME_SELF="999997";
	public static final String ROOT_VOLUME_XS="999996";  //online xiao shuo
	
	
	public static final String ROOT_VOLUME_BANNED="999990";
	
	
	public static final String NAME_NONAME="noname";
	public static Map<String, Volume> ROOT_VOLUMES=new HashMap<String, Volume>();
	static{
		ROOT_VOLUMES.put(ROOT_VOLUME_LHH, new Volume(ROOT_VOLUME_LHH, TYPE_PIC));
		ROOT_VOLUMES.put(ROOT_VOLUME_MH, new Volume(ROOT_VOLUME_MH, TYPE_PIC));
		ROOT_VOLUMES.put(ROOT_VOLUME_XS, new Volume(ROOT_VOLUME_XS, TYPE_NOVEL));
		ROOT_VOLUMES.put(ROOT_VOLUME_SELF, new Volume(ROOT_VOLUME_SELF, TYPE_MIX));
		ROOT_VOLUMES.put(ROOT_VOLUME_BANNED, new Volume(ROOT_VOLUME_BANNED, TYPE_MIX));
		
	}
	
	public static Volume getRootVolume(String id){
		return ROOT_VOLUMES.get(id);
	}
	
	public static final String VOLUME_KEY="Volume";
	public static final String ID_KEY="id";
	public static final String TYPE_KEY="type";
	public static final String NAME_KEY="name";
	public static final String UTIME_KEY="utime";
	public static final String DATA_KEY="data";
	public static final String CAT_KEY="cat";
	public static final String FULL_PATH_KEY="fp";
	public static final String AUTHOR_KEY="au";
	public static final String BOOKNUM_KEY="bn";
	

	public static List<String> getRootVolumes(){
		List<String> vl = new ArrayList<String>();
		for (Volume v: ROOT_VOLUMES.values()){
			vl.add(v.getId());
		}
		return vl;
	}
	//for reading state
	@XmlTransient
	private int state;
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	@XmlAttribute(name=ID_KEY)
	private String id;
	@XmlAttribute(name=TYPE_KEY)
	private int type;
	@XmlAttribute(name=NAME_KEY)
	private String name=NAME_NONAME;
	@XmlAttribute(name=UTIME_KEY)
	private String strUtime;
	public String getStrUtime() {
		return strUtime;
	}
	public void setStrUtime(String strUtime) {
		this.strUtime = strUtime;
	}
	
	@XmlTransient
	private Date utime;
	
	@XmlAttribute(name=DATA_KEY)
	private String data;
	@XmlAttribute(name=CAT_KEY)
	private String parentCat=""; //category
	@XmlAttribute(name=FULL_PATH_KEY)
	private String fullPath;
	@XmlAttribute(name=AUTHOR_KEY)
	private String author;
	//following fields will not be imported
	@XmlAttribute(name=BOOKNUM_KEY)
	private int bookNum; //number of books this volume recursively has in total

	//these are inside of data
    public static final String KEY_COVER_URI="coverUri";
    @XmlTransient
    private String coverUri;
    public static final String KEY_CONTENT_XPATH="content_xpath";
    @XmlTransient
    private String contentXPath;
    public static final String KEY_REFERER="referer";
    @XmlTransient
    private String referer;
    
	public static final String AT="@";
	
	@Override
    public void fromTopJSONObject(JSONObject obj){    	
		id = obj.optString(AT+ID_KEY);
		type = obj.optInt(AT+TYPE_KEY);
		name = obj.optString(AT+NAME_KEY);
		strUtime = obj.optString(AT+UTIME_KEY);
		try{
			if (strUtime!=null){
				utime = RemotePersistManager.SDF_SERVER_DTZ.parse(strUtime);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		data = obj.optString(AT+DATA_KEY);
		parentCat = obj.optString(AT+CAT_KEY);
		author = obj.optString(AT+AUTHOR_KEY);
		bookNum = obj.optInt(AT+BOOKNUM_KEY);
		
		init(id, type, name, utime, data, parentCat, author, bookNum, true);
    }

	@Override
	public void fromTopJSONString(String json){			
		try{
			JSONObject obj = new JSONObject(json);
			obj = obj.optJSONObject(VOLUME_KEY);
			fromTopJSONObject(obj);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public static List<Volume> fromTopJSONListString(String json){
		JSONObject listObject = new JSONObject(json);
        JSONArray jarray = listObject.getJSONArray(VOLUME_KEY);
        int len = jarray.length();
        List<Volume> vl = new ArrayList<Volume>();
        for (int i=0; i<len; i++){
        	JSONObject obj = jarray.getJSONObject(i);
        	Volume v = new Volume();
        	v.fromTopJSONObject(obj);
        	vl.add(v);
        }
        return vl;
	}

	@Override
	public String toTopJSONString(){
		JSONObject obj = toJSONObject();
		JSONObject obj2= new JSONObject();
		obj2.put(VOLUME_KEY, obj);
		return obj2.toString();
	}
	
	@Override
	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put(AT+ID_KEY, id);
		obj.put(AT+TYPE_KEY, type);
		obj.put(AT+NAME_KEY, name);
		obj.put(AT+UTIME_KEY, strUtime);
		obj.put(AT+DATA_KEY, data);
		obj.put(AT+CAT_KEY, parentCat);
		obj.put(AT+AUTHOR_KEY, author);
		obj.put(AT+BOOKNUM_KEY, bookNum);
		return obj;
	}
    
	//for data field
    private JSONObject fromJSON(){
		try{
			return new JSONObject(data);
		}
		catch(JSONException e){
			return new JSONObject();
		}
	}
    
    //for data field
    @Override
    public void dataToJSON(){
		JSONObject obj = new JSONObject();
		try {
			obj.put(KEY_COVER_URI, coverUri);
			obj.put(KEY_CONTENT_XPATH, contentXPath);
			obj.put(KEY_REFERER, referer);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		data = obj.toString();
	}
	
    public Volume(){
	}
    
	public Volume(String id, int type){
		this.id =id;
		this.type = type;
	}
    //initialized from client db which does not support date type
	public Volume(String id, int type, String name, String utime, String data, String parentCat, 
			String author, int bookNum){
		Date d = new Date();
		try {
			d = RemotePersistManager.SDF_SERVER_DTZ.parse(utime);
		}catch(Exception e){
			e.printStackTrace();
		}
		init(id, type, name, d, data, parentCat, author, bookNum, true);
	}
	
	//initialized from server db which date type supported
	public Volume(String id, int type, String name, Date utime, String data, String parentCat, 
			String author, int bookNum, boolean expand){
		init(id,type,name,utime,data, parentCat, author, bookNum, expand);
	}
	
	public void init(String id, int type, String name, Date utime, String data, String parentCat, 
			String author, int bookNum, boolean expand){
		this.id = id;
		this.type = type;
		this.name = name;
		this.setUtime(utime);
		this.data = data;
		this.parentCat = parentCat;
		this.author = author;
		this.bookNum = bookNum;
		
		if (expand){
			JSONObject obj = fromJSON();
			coverUri = obj.optString(KEY_COVER_URI);
			contentXPath = obj.optString(KEY_CONTENT_XPATH);
			referer = obj.optString(KEY_REFERER);
		}
	}

	public String toString(){
		return toTopJSONString();
	}
	
	public String toDBString(String sep){
		StringBuffer sb = new StringBuffer();
		sb.append(id);
		sb.append(sep);
		sb.append(name);
		sb.append(sep);
		sb.append(utime);
		sb.append(sep);
		sb.append(parentCat);		
		sb.append(sep);
		sb.append(author);	
		sb.append(sep);
		sb.append(data);	
		sb.append(sep);
		sb.append(bookNum);
		return sb.toString();
	}

	@Override
	public boolean equals(Object o){
		if (!(o instanceof Volume)){
			return false;
		}
		Volume v = (Volume)o;
		return !CompareUtil.ObjectDiffers(this.getId(), v.getId());
	}
	@Override
	public String getName(){
		return name;
	}	
	@Override
	public void setName(String name){
		this.name = name;
	}
	@Override
	public String getCoverUri(){
		return this.coverUri;
	}
	@Override
	public void setCoverUri(String coverUri){
		this.coverUri = coverUri;
	}	
	@Override
	public String getCat(){
		return getParentCat();
	}	
	
	public Date getUtime() {
		return utime;
	}
	public void setUtime(Date utime) {
		this.utime = utime;
		if (utime!=null){
			strUtime = RemotePersistManager.SDF_SERVER_DTZ.format(utime);
		}
	}
	
	public String getData(){
		return this.data;
	}	
	public String getParentCat() {
		return parentCat;
	}
	public void setParentCat(String parentCat) {
		this.parentCat = parentCat;
	}
	@Override
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFullPath() {
		return fullPath;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getBookNum() {
		return bookNum;
	}
	public void setBookNum(int bookNum) {
		this.bookNum = bookNum;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getContentXPath() {
		return contentXPath;
	}
	public void setContentXPath(String contentXPath) {
		this.contentXPath = contentXPath;
	}
    public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	
	@Override
	public int compareTo(Reading o) {
		if (o==null){
			return 1;
		}else{
			return this.getId().compareTo(o.getId());
		}
	}

}
