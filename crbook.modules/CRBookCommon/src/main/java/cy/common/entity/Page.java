package cy.common.entity;

import org.cld.util.CompareUtil;
import org.json.*;

import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = Page.PAGE_KEY)
@XmlAccessorType(XmlAccessType.FIELD)
public class Page implements IJSON{
	
	public static final String NAME_NONAME="noname";
    
	public static final String PAGE_KEY="page";
	public static final String PAGELIST_KEY="pagelist";
	
	public static final String BOOKID_KEY="bid";
	public static final String PAGENUM_KEY="pn";
	public static final String DATA_KEY="data";
	public static final String UTIME_KEY="utime";
	
	@XmlAttribute(name=BOOKID_KEY)
	private String bookid;
	@XmlAttribute(name=PAGENUM_KEY)
	private int pagenum;//the page number are ranging from 1 to totalPage
	@XmlAttribute(name=DATA_KEY)
	private String data;
	@XmlAttribute(name=UTIME_KEY)
	private String utime;
	
	//these are inside of data
	public static final String KEY_ANSWERSTROKES="answerStrokes";
	public static final String KEY_QUESTIONSTROKES="questionStrokes";    
    public static final String KEY_BACKGROUNDURI="b";
    public static final String KEY_REWARD_URI="rewardUri";
	public static final String AT="@";
    
    @XmlTransient
    private List<Stroke> answerStrokes;
    @XmlTransient
    private List<Stroke> questionStrokes;
    @XmlTransient
    private String backgroundUri;
    @XmlTransient
	private String rewardUri;
    
    @Override
    public void fromTopJSONObject(JSONObject obj){			
		try{			
			bookid = obj.optString(AT+BOOKID_KEY);
			pagenum = obj.optInt(AT+PAGENUM_KEY);
			utime = obj.optString(AT+UTIME_KEY);
			data = obj.optString(AT+DATA_KEY);
			init(true);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
    
    @Override
	public void fromTopJSONString(String json){			
		try{
			JSONObject obj = new JSONObject(json);
			obj = obj.optJSONObject(PAGE_KEY);			
			fromTopJSONObject(obj);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
    
    @Override
	public String toTopJSONString(){
		JSONObject obj = new JSONObject();
		obj.put(AT+BOOKID_KEY, bookid);
		obj.put(AT+PAGENUM_KEY, pagenum);
		obj.put(AT+UTIME_KEY, utime);
		obj.put(AT+DATA_KEY, data);
		JSONObject obj2= new JSONObject();
		obj2.put(PAGE_KEY, obj);
		return obj2.toString();
	}
	
    @Override
	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put(AT+BOOKID_KEY, bookid);
		obj.put(AT+PAGENUM_KEY, pagenum);
		obj.put(AT+UTIME_KEY, utime);
		obj.put(AT+DATA_KEY, data);
		return obj;
	}
	
	public static String toTopJSONListString(List<Page> pl){
		JSONArray jsarray = new JSONArray();
		for (Page p: pl){
			jsarray.put(p.toJSONObject());
		}
		JSONObject obj2= new JSONObject();
		obj2.put(PAGELIST_KEY, jsarray);
		return obj2.toString();
	}
	
	public static List<Page> fromTopJSONListString(String json){			
		List<Page> pl = new ArrayList<Page>();
		try{
			JSONObject obj = new JSONObject(json);
			JSONArray jsarray = obj.optJSONArray(PAGELIST_KEY);
			for (int i=0; i<jsarray.length(); i++){
				JSONObject jpage = (JSONObject) jsarray.get(i);
				Page p = new Page();
				p.fromTopJSONObject(jpage);
				pl.add(p);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return pl;
	}
			
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(",");
		sb.append(backgroundUri);
		return sb.toString();
	}
	
	public String toDBString(String sep){
		StringBuffer sb = new StringBuffer();
		sb.append(bookid);
		sb.append(sep);
		sb.append(pagenum);
		sb.append(sep);
		sb.append(data);		
		return sb.toString();
	}
    
    @Override
    public void dataToJSON(){
		JSONObject obj = new JSONObject();
		try {
			if (backgroundUri!=null && !"".equals(backgroundUri)){
				obj.put(KEY_BACKGROUNDURI, backgroundUri);
			}
			if (rewardUri!=null && !"".equals(rewardUri)){
				obj.put(KEY_REWARD_URI, rewardUri);
			}
			if (answerStrokes.size()!=0){
				JSONArray jarray = new JSONArray();
				for (int i=0; i<answerStrokes.size(); i++){
					jarray.put(answerStrokes.get(i).toJSONObject());
				}
				obj.put(KEY_ANSWERSTROKES, jarray);
			}
			if (questionStrokes.size()!=0){
				JSONArray jarray = new JSONArray();
				for (int i=0; i<questionStrokes.size(); i++){
					jarray.put(questionStrokes.get(i).toJSONObject());
				}
				obj.put(KEY_QUESTIONSTROKES, jarray);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		data = obj.toString();
	}
    
    public Page(){
    	answerStrokes = new ArrayList<Stroke>();
    	questionStrokes = new ArrayList<Stroke>();
    }
    
    private void init(boolean expand){
    	init(bookid, pagenum, data, utime, expand);
    }
    
    public void init(String bookid, int pagenum, String data, String utime, boolean expand){
    	this.bookid = bookid;
		//this.bookname = bookname;
		this.pagenum = pagenum;
		this.data = data;
		this.utime = utime;
		
		//from json string
		if (expand) {
			//fetch these from data
			JSONObject obj = new JSONObject(data);
			JSONArray jarray = obj.optJSONArray(KEY_QUESTIONSTROKES);
			if (jarray!=null){
	            for (int i = 0; i < jarray.length(); i++) {
	                JSONObject strokeObj = jarray.getJSONObject(i);
	                Stroke stroke = new Stroke();
	                stroke.fromJSONObject(strokeObj);
	                questionStrokes.add(stroke);
	            }
			}
	        
			jarray = obj.optJSONArray(KEY_ANSWERSTROKES);
	        if (jarray!=null){
	            for (int i = 0; i < jarray.length(); i++) {
	                JSONObject strokeObj = jarray.optJSONObject(i);
	                Stroke stroke = new Stroke();
	                stroke.fromJSONObject(strokeObj);
	                answerStrokes.add(stroke);
	            }
	        }
	        
	        backgroundUri = obj.optString(KEY_BACKGROUNDURI);
	        rewardUri = obj.optString(KEY_REWARD_URI);
		}
    }
    
    
	public Page(String bookid, int pagenum, String data, String utime, boolean expand){
		this();
		init(bookid, pagenum, data, utime, expand);	
	}
	
	public void setPageNum(int pagenum){
		this.pagenum=pagenum;
	}
	
	public int getPagenum(){
		return pagenum;
	}

	public String getUtime() {
		return utime;
	}

	public void setUtime(String utime) {
		this.utime = utime;
	}
	
	public String getData(){
		return data;
	}
	
	public List<Stroke> getAnswerStrokes(){
		return answerStrokes;
	}
	public List<Stroke> getQuestionStrokes(){
		return questionStrokes;
	}
	
	public void clearAnswerStrokes(){
		answerStrokes.clear();
	}
	
	public void clearQuestionStrokes(){
		questionStrokes.clear();
	}

	public String getBackgroundUri(){
		return backgroundUri;
	}
	public void setBackgroundUri(String backgroundUri){
		this.backgroundUri = backgroundUri;
	}
	
	public String getRewardUri() {
		return rewardUri;
	}
	public void setRewardUri(String rewardUri) {
		this.rewardUri = rewardUri;
	}	
	public String getBookid() {
		return bookid;
	}

	public void setBookid(String bookid) {
		this.bookid = bookid;
	}
	@Override
	public boolean equals(Object o){
		Page p = (Page)o;
		String bg1 = getBackgroundUri();
		String bg2 = p.getBackgroundUri();
		return !CompareUtil.ObjectDiffers(bg1, bg2);
	}

}
