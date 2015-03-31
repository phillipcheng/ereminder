package org.cld.datacrawl;

import com.gargoylesoftware.htmlunit.html.HtmlElement;


public class NextPage {
	public static final int STATUS_NORMAL=0;
	public static final int STATUS_ERR=1;
	public static final int STATUS_LASTPAGE = 2;
	
	private String nextUrl;
	private HtmlElement nextItem; //the HtmlElement represents next page
	private int status = STATUS_NORMAL;
	
	public NextPage(int status){
		this.status=status;
	}
	public NextPage(HtmlElement nextItem){
		this.nextItem = nextItem;
	}
	public NextPage(String nextUrl){
		this.nextUrl = nextUrl;
	}
	public NextPage(String nextUrl, HtmlElement nextItem){
		this.nextItem = nextItem;
		this.nextUrl = nextUrl;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		if (nextUrl!=null){
			sb.append("nextUrl:" + nextUrl + "\n");
		}
		if (nextItem!=null){
			sb.append("nextItem:" + nextItem + "\n");
		}
		sb.append("status:" + status + "\n");
		return sb.toString();
	}
	public String getNextUrl() {
		return nextUrl;
	}
	public void setNextUrl(String nextUrl) {
		this.nextUrl = nextUrl;
	}
	public HtmlElement getNextItem() {
		return nextItem;
	}
	public void setNextItem(HtmlElement nextItem) {
		this.nextItem = nextItem;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	} 

}
