package org.xml.mytaskdef;

import java.util.HashMap;
import java.util.Map;

import org.xml.taskdef.AttributeType;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.ParamType;

public class ParsedBrowsePrd {
	
	private BrowseDetailType browsePrdTaskType; //origin definition
	private Map<String, AttributeType> pdtAttrMap = new HashMap<String, AttributeType>();
	private Map<String, ParamType> paramMap = new HashMap<String, ParamType>();

	public ParsedBrowsePrd(BrowseDetailType bpt){
		this.browsePrdTaskType = bpt;
		for (AttributeType at : bpt.getBaseBrowseTask().getUserAttribute()){
			pdtAttrMap.put(at.getName(), at);
		}
		for (ParamType pt:bpt.getBaseBrowseTask().getParam()){
			paramMap.put(pt.getName(), pt);
		}
	}
	
	public BrowseDetailType getBrowsePrdTaskType() {
		return browsePrdTaskType;
	}
	public Map<String, AttributeType> getPdtAttrMap() {
		return pdtAttrMap;
	}

	public Map<String, ParamType> getParamMap() {
		return paramMap;
	}
}
