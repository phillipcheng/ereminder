package cy.common.entity;


import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@XmlRootElement(name = BookPages.BOOKPAGES_KEY)
@XmlAccessorType(XmlAccessType.FIELD)
public class BookPages {
	public static final String BOOKPAGES_KEY="bookpages";
	public static final String PAGELIST_KEY="pagelist";
	
	public BookPages(){	
	}
	
	@XmlElement(name=Book.BOOK_KEY)
	Book b;
	@XmlElement(name=Page.PAGE_KEY)
	List<Page> pages;
	
	public BookPages(Book b, List<Page> pages){
		this.b=b;
		this.pages = pages;
	}
	
	public Book getBook(){
		return b;
	}
	
	public List<Page> getPages(){
		return pages;
	}
	
	public JSONObject toJSONObject(){
		JSONArray jsarray = new JSONArray();
		for (Page p: pages){
			jsarray.put(p.toJSONObject());
		}
		
		JSONObject jObj= new JSONObject();
		jObj.put(Book.BOOK_KEY, b.toJSONObject());
		jObj.put(Page.PAGE_KEY, jsarray);
		
		return jObj;
	}
	
	public String toTopJSONString(){
		JSONObject jobj = toJSONObject();
		JSONObject obj = new JSONObject();
		obj.put(BookPages.BOOKPAGES_KEY, jobj);
		return obj.toString();
	}
}
