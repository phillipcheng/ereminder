package org.cld.datacrawl.util;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class Content {
	public static final int TYPE_URL = 1;
	public static final int TYPE_DETAILS = 2;
	public static final int TYPE_BOTH =3;
	
	private int contentType;
	private HtmlElement htmlContent;
	private String url;
	private String refUrl; //the refUrl to this content
	
	public Content(){
		
	}
	
	public Content(String url){
		this.contentType = TYPE_URL;
		this.url = url;
	}
	
	public int getContentType() {
		return contentType;
	}
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public HtmlElement getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(HtmlElement htmlContent) {
		this.htmlContent = htmlContent;
	}
	
	public boolean hasUrl(){
		return (contentType == TYPE_URL || contentType == TYPE_BOTH);
	}

	public String getRefUrl() {
		return refUrl;
	}

	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}
	
	public String toString(){
		String str="contentType:" + contentType + "\n";
		if (refUrl != null){
			str += "refUrl:" + refUrl + "\n";
		}
		if (url!= null){
			str +="url:" + url + "\n";
		}
		if (htmlContent != null){
			str += "html-content:" + htmlContent.asXml() + "\n";
		}
		return str;
	}

}
