package org.xml.mytaskdef;

import java.util.regex.Matcher;

import org.xml.taskdef.BrowseCatType;


public class BrowseCatInst {
	private ParsedBrowseCat pbc;
	private IdUrlMapping matchedIum;
	private Matcher matcher;

	public BrowseCatInst(ParsedBrowseCat pbc, IdUrlMapping matchedIum, Matcher matcher){
		this.pbc = pbc;
		this.matchedIum = matchedIum;
		this.matcher = matcher;
	}
	
	public ParsedBrowseCat getPBC(){
		return pbc;
	}
	
	public BrowseCatType getBc() {
		return pbc.getBc();
	}

	public String getId() {
		int idx = matchedIum.getIdIdx();
		return matcher.group(idx);
	}

	public int getPageNum() {
		int idx = matchedIum.getPageNumIdx();
		return Integer.parseInt(matcher.group(idx));
	}
}