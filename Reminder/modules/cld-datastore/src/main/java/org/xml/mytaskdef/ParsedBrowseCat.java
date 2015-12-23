package org.xml.mytaskdef;

import org.xml.taskdef.BrowseCatType;


public class ParsedBrowseCat {
	
	private IdUrlMapping ium;
	private IdUrlMapping iumFirstPage; //optional
	private BrowseCatType bc; //origin definition
	
	public ParsedBrowseCat(IdUrlMapping ium, IdUrlMapping iumFirstPage, BrowseCatType bc){
		this.ium = ium;
		this.iumFirstPage = iumFirstPage;
		this.bc = bc;
	}
	
	public IdUrlMapping getIum() {
		return ium;
	}
	public void setIum(IdUrlMapping ium) {
		this.ium = ium;
	}
	public IdUrlMapping getIumFirstPage() {
		return iumFirstPage;
	}
	public void setIumFirstPage(IdUrlMapping iumFirstPage) {
		this.iumFirstPage = iumFirstPage;
	}
	public BrowseCatType getBc() {
		return bc;
	}
	public void setBc(BrowseCatType bc) {
		this.bc = bc;
	}

}
