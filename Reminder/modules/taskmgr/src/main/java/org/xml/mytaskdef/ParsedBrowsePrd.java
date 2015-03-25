package org.xml.mytaskdef;

import java.util.HashMap;
import java.util.Map;

import org.xml.taskdef.AttributeType;
import org.xml.taskdef.BrowseDetailType;

public class ParsedBrowsePrd {
	
	private IdUrlMapping ium;
	private BrowseDetailType browsePrdTaskType; //origin definition
	private Map<String, AttributeType> pdtAttrMap = new HashMap<String, AttributeType>();

	public ParsedBrowsePrd(BrowseDetailType bpt){
		this.browsePrdTaskType = bpt;
		if (bpt.getBaseBrowseTask().getIdUrlMapping()!=null){
			ium = new IdUrlMapping(bpt.getBaseBrowseTask().getIdUrlMapping());
		}
		for (AttributeType at : bpt.getBaseBrowseTask().getUserAttribute()){
			pdtAttrMap.put(at.getName(), at);
		}
	}
	
	public IdUrlMapping getIum() {
		return ium;
	}
	public BrowseDetailType getBrowsePrdTaskType() {
		return browsePrdTaskType;
	}
	public Map<String, AttributeType> getPdtAttrMap() {
		return pdtAttrMap;
	}


}
