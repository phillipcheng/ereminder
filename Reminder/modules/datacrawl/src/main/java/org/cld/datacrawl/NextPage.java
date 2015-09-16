package org.cld.datacrawl;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class NextPage {
	public static final int STATUS_NORMAL=0;
	public static final int STATUS_ERR=1;
	public static final int STATUS_LASTPAGE = 2;
	
	private String nextUrl;
	private HtmlElement nextItem; //the HtmlElement represents next page
	private HtmlPage motherPage; //if next item is on the frame, this is the enclosing page
	private String frameId; //can be index or name
	private int status = STATUS_NORMAL;
	
	public NextPage(int status){
		this.status=status;
	}

	public NextPage(String nextUrl){
		this.nextUrl = nextUrl;
		this.motherPage = null;
		this.frameId = null;
	}
	
	public NextPage(String nextUrl, HtmlPage motherPage, String frameId){
		this.nextUrl = nextUrl;
		this.motherPage = motherPage;
		this.frameId = frameId;
	}
	
	public NextPage(HtmlElement nextItem, HtmlPage motherPage, String frameId){
		this.nextItem = nextItem;
		this.motherPage = motherPage;
		this.frameId = frameId;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		if (nextUrl!=null){
			sb.append("nextUrl:" + nextUrl + "\n");
		}
		if (nextItem!=null){
			sb.append("nextItem:" + nextItem.asXml() + "\n");
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
	public HtmlPage getMotherPage() {
		return motherPage;
	}
	public void setMotherPage(HtmlPage motherPage) {
		this.motherPage = motherPage;
	}
	public String getFrameId() {
		return frameId;
	}
	public void setFrameId(String frameId) {
		this.frameId = frameId;
	} 

}
