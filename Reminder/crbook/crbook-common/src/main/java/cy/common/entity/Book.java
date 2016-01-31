package cy.common.entity;


import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.cld.util.CompareUtil;
import org.cld.util.PatternResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cy.common.persist.RemotePersistManager;

@XmlRootElement(name = Book.BOOK_KEY)
@XmlAccessorType(XmlAccessType.FIELD)
public class Book implements Reading{
	public static final String NAME_NONAME="noname";
    
	public static final String BOOK_KEY="Book";
	
	public static final String ID_KEY="id";
	public static final String TYPE_KEY="type";
	public static final String NAME_KEY="name";
	public static final String TotalPage_KEY="tp";
	public static final String LastReadPage_KEY="lp";
	public static final String IndexedPage_KEY="ip";
	public static final String UTIME_KEY="utime";
	public static final String DATA_KEY="data";
	public static final String CAT_KEY="cat";
	public static final String FULL_PATH_KEY="fp";
	public static final String READ_KEY="read";
	public static final String CACHED_KEY="cached";
	public static final String AUTHOR_KEY="author";
	public static final String STATUS_KEY="status";
	
	@XmlTransient
	private int state;
	
	public static final int STATUS_UNKNOWN=0;
	public static final int STATUS_ONGOING=1;
	public static final int STATUS_FINISHED=2;
	@XmlAttribute(name=STATUS_KEY)
	private int status; //for online stuff, they are updated regularly until it is finished

	@XmlAttribute(name=ID_KEY)
	private String id;
	
	@XmlAttribute(name=TYPE_KEY)
	private int type;
	
	@XmlAttribute(name=NAME_KEY)
	private String bookname=NAME_NONAME;
	
	@XmlAttribute(name=TotalPage_KEY)
	private int totalpage;
	
	@XmlAttribute(name=LastReadPage_KEY)
	private int lastpage=1;//set to 1st page
	
	@XmlAttribute(name=IndexedPage_KEY)
	private int indexedPages=0;//number of indexed, successfully crawled page

	//for json
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
	private String cat; //category
	
	@XmlAttribute(name=FULL_PATH_KEY)
	private String fullPath;
	
	//following fields will not be imported
	@XmlAttribute(name=READ_KEY)
	private int read;  //0 not read
	
	@XmlAttribute(name=CACHED_KEY)
	private int cached; //0 not cached
	
	@XmlAttribute(name=AUTHOR_KEY)
	private String author;

	//these are inside of data
    public static final String KEY_COVER_URI="coverUri";
    @XmlTransient
    private String coverUri;
    
	public static final String KEY_STICKER_DIR="stickerDir";
	@XmlTransient
	private String stickerDir;
	
	//the common prefix of all page url, b for base
	public static final String KEY_BASE_URL="bUrl";
	@XmlTransient
	private String bUrl;
	
	//the common suffix of all page url, s for suffix
	public static final String KEY_SUFFIX_URL="sUrl";
	@XmlTransient
	private String sUrl;
	
	//the url generation pattern
	public static final String KEY_PAGE_BGURL_PATTERN="pbgPtn";
	@XmlTransient
	private PatternResult pageBgUrlPattern;
	
	//the key for each page url within the pagelist, this is redundent, because of JSON requirement
	public static final String KEY_EACH_PAGE_URL="p";
	
	public static final String AT="@";

	@Override
	public void fromTopJSONObject(JSONObject obj){			
		id = obj.optString(AT+ID_KEY);
		type = obj.optInt(AT+TYPE_KEY);
		bookname = obj.optString(AT+NAME_KEY);
		totalpage = obj.optInt(AT+TotalPage_KEY);
		lastpage = obj.optInt(AT+LastReadPage_KEY);
		strUtime = obj.optString(AT+UTIME_KEY);
		try{		
			if (strUtime!=null && !"".equals(strUtime)){
				utime = RemotePersistManager.SDF_SERVER_DTZ.parse(strUtime);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		data = obj.optString(AT+DATA_KEY);
		cat = obj.optString(AT+CAT_KEY);
		read = obj.optInt(AT+READ_KEY);
		indexedPages=obj.optInt(AT+IndexedPage_KEY);
		cached = obj.optInt(AT+CACHED_KEY);
		author = obj.optString(AT+AUTHOR_KEY);
		status = obj.optInt(AT+STATUS_KEY);
		init(id, type, bookname, totalpage, lastpage, utime, data, cat, read, indexedPages, cached, author, status, true);
	}
	
	@Override
	public void fromTopJSONString(String json){			
		try{
			JSONObject obj = new JSONObject(json);
			obj = obj.optJSONObject(BOOK_KEY);			
			fromTopJSONObject(obj);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public static List<Book> fromTopJSONListString(String json){
		JSONObject listObject = new JSONObject(json);
        JSONArray jarray = listObject.getJSONArray(BOOK_KEY);
        int len = jarray.length();
        List<Book> bl = new ArrayList<Book>();
        for (int i=0; i<len; i++){
        	JSONObject obj = jarray.getJSONObject(i);
        	Book b = new Book();
        	b.fromTopJSONObject(obj);
        	bl.add(b);
        }
        return bl;
	}
	
	@Override
	public String toTopJSONString(){
		JSONObject obj = toJSONObject();
		JSONObject obj2= new JSONObject();
		obj2.put(BOOK_KEY, obj);
		return obj2.toString();
	}
	
	@Override
	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put(AT+ID_KEY, id);
		obj.put(AT+TYPE_KEY, type);
		obj.put(AT+NAME_KEY, bookname);
		obj.put(AT+TotalPage_KEY, totalpage);
		obj.put(AT+LastReadPage_KEY, lastpage);
		obj.put(AT+UTIME_KEY, strUtime);		
		obj.put(AT+DATA_KEY, data);
		obj.put(AT+CAT_KEY, cat);
		obj.put(AT+READ_KEY, read);
		obj.put(AT+CACHED_KEY, cached);
		obj.put(AT+IndexedPage_KEY, indexedPages);
		obj.put(AT+AUTHOR_KEY, author);
		obj.put(AT+STATUS_KEY, status);
		return obj;
	}
	
	@Override
	public void dataToJSON(){
		JSONObject obj = new JSONObject();
		try {
			if (coverUri!=null && !"".equals(coverUri)){
				obj.put(KEY_COVER_URI, coverUri);
			}
			if (stickerDir!=null && !"".equals(stickerDir)){
				obj.put(KEY_STICKER_DIR, stickerDir);
			}
			if (bUrl!=null && !"".equals(bUrl)){
				obj.put(KEY_BASE_URL, bUrl);
			}
			if (sUrl!=null && !"".equals(sUrl)){
				obj.put(KEY_SUFFIX_URL, sUrl);
			}
			if (pageBgUrlPattern!=null){
				obj.put(KEY_PAGE_BGURL_PATTERN, pageBgUrlPattern.toJSON());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		data = obj.toString();
	}
    
	//used by initialization from memory
	public Book(){
    }
	
	//expand: whether expand the data field
	public void init(String id, int type, String bookname, int totalpage, int lastpage, 
			Date utime, String data, String cat, int read, int cached, int indexedPages, 
			String author, int status, boolean expand){
		this.id = id;
		this.type = type;
		this.bookname = bookname;
		this.totalpage = totalpage;
		this.lastpage = lastpage;
		this.setUtime(utime);
		this.data = data;
		this.cat = cat;
		this.read = read;
		this.cached=cached;
		this.indexedPages = indexedPages;
		this.author = author;
		this.status = status;
	
		if (expand && data!=null){
			expand();	
		}
	}
	
	public void expand(){
		if (data!=null){
			JSONObject obj = new JSONObject(data);
			coverUri = obj.optString(KEY_COVER_URI);
			stickerDir = obj.optString(KEY_STICKER_DIR);
			bUrl = obj.optString(KEY_BASE_URL);
			sUrl = obj.optString(KEY_SUFFIX_URL);
			String pattern = obj.optString(KEY_PAGE_BGURL_PATTERN);
			if (pattern!=null && !"".equals(pattern)){
				pageBgUrlPattern = new PatternResult();
				pageBgUrlPattern.fromJSON(pattern);
			}		
		}
	}
	
	//initialized from client db which does not support date type
	public Book(String id, int type, String bookname, int totalpage, int lastpage, 
			String utime, String data, String cat, int read, int cached, int indexedPages, 
			String author, int status){
		this();
		Date d = new Date();
		try {
			d = RemotePersistManager.SDF_SERVER_DTZ.parse(utime);
		}catch(Exception e){
			e.printStackTrace();
		}
		init(id, type, bookname,totalpage,lastpage,d,data, cat, read, cached, indexedPages, author, status, true);
	}
	
	//initialized from server db which date type supported
	public Book(String id, int type, String bookname, int totalpage, int lastpage, 
			Date utime, String data, String cat, int indexedPages, 
			String author, int status, boolean expand){
		this();
		init(id, type, bookname,totalpage,lastpage,utime,data, cat, 0, 0, indexedPages, author, status, expand);
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(id);
		sb.append(",");
		sb.append(bookname);
		sb.append(",");
		sb.append(totalpage);
		sb.append(",");
		sb.append(lastpage);
		sb.append(",");
		sb.append(utime);
		sb.append(",");
		sb.append(data);
		sb.append(",");
		sb.append(coverUri);
		sb.append(",");
		sb.append(stickerDir);		
		sb.append(",");
		sb.append(bUrl);
		sb.append(",");
		sb.append(sUrl);
		sb.append(",");
		sb.append(cat);
		sb.append(",");
		sb.append(pageBgUrlPattern);
		sb.append(",");
		sb.append(read);	
		sb.append(",");
		sb.append(indexedPages);	
		return sb.toString();
	}
	
	public String toDBString(String sep){
		StringBuffer sb = new StringBuffer();
		sb.append(id);
		sb.append(sep);
		sb.append(bookname);
		sb.append(sep);
		sb.append(totalpage);
		sb.append(sep);
		sb.append(lastpage);
		sb.append(sep);
		sb.append(utime);
		sb.append(sep);
		sb.append(data);
		sb.append(sep);
		sb.append(cat);		
		return sb.toString();
	}
	
	@Override
	public String getName(){
		return bookname;
	}	
	@Override
	public void setName(String bookName){
		this.bookname = bookName;
	}
	@Override
	public String getCoverUri(){
		return this.coverUri;
	}
	@Override
	public void setCoverUri(String coverUri){
		this.coverUri = coverUri;
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
	
	public int getTotalPage(){
		return totalpage;
	}
	public void setTotalPage(int totalPage){
		this.totalpage = totalPage;
	}
	
	public int getLastPage(){
		return lastpage;
	}	
	public void setLastPage(int lastPage){
		this.lastpage = lastPage;
	}
	public String getCat() {
		return cat;
	}
	public void setCat(String cat) {
		this.cat = cat;
	}
	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Book))
			return false;
		Book b = (Book)o;
		return !CompareUtil.ObjectDiffers(this.getId(), b.getId());
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getbUrl() {
		return bUrl;
	}

	public void setbUrl(String bUrl) {
		this.bUrl = bUrl;
	}

	public String getsUrl() {
		return sUrl;
	}

	public void setsUrl(String sUrl) {
		this.sUrl = sUrl;
	}

    public PatternResult getPageBgUrlPattern() {
		return pageBgUrlPattern;
	}

	public void setPageBgUrlPattern(PatternResult pageBgUrlPattern) {
		this.pageBgUrlPattern = pageBgUrlPattern;
	}

	public int getRead() {
		return read;
	}

	public void incRead(){
		read++;
	}
	public void setRead(int read) {
		this.read = read;
	}

	public int getCached() {
		return cached;
	}

	public void setCached(int cached) {
		this.cached = cached;
	}

	@Override
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public int getIndexedPages() {
		return indexedPages;
	}

	public void setIndexedPages(int indexedPages) {
		this.indexedPages = indexedPages;
	}
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
