package org.xml.mytaskdef;

public class XPathType {
	
	private String xpath;
	private String frameId;
	
	public XPathType(String xpath, String frameId){
		this.xpath = xpath;
		this.frameId = frameId;
	}
	
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public String getFrameId() {
		return frameId;
	}
	public void setFrameId(String frameId) {
		this.frameId = frameId;
	}

}
