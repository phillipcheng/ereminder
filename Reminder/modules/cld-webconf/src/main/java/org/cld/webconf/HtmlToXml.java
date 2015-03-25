package org.cld.webconf;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HtmlToXml {
	
	public static final String TAG = ".tg";
	public static final String ATTR_PREFIX="@";
	public static final String SIMPLE_PREFIX="#";
	public static final String COMPLEX_CLOSING="/";

	private static Logger logger = LogManager.getLogger("cld.jsp");
	
	public static String toXml(Map paramMap){
		//the current position of each attr/simple-type in the corresponding value list
		Map<String, Integer> attrPosMap = new HashMap<String, Integer>();
		
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		String[] allTags = (String[]) paramMap.get(TAG);
		for (int i=0; i<allTags.length; i++){
			String tag = allTags[i];
			if (tag.startsWith(ATTR_PREFIX) || tag.startsWith(SIMPLE_PREFIX)){
				String tagName = tag.substring(1);
				String[] valList = null;
				if (tag.startsWith(ATTR_PREFIX)|| tag.startsWith(SIMPLE_PREFIX) ){
					valList= (String[]) paramMap.get(tag);
				}else{
					valList=(String[])paramMap.get(tagName);
				}
				if (valList==null){
					logger.error("valList is null for tag:" + tag);
				}
				int pos=0;
				if (attrPosMap.containsKey(tag)){
					pos = attrPosMap.get(tag);
				}else{
					pos = 0;
				}
				String val = valList[pos];
				val = StringEscapeUtils.escapeXml(val);
				pos++;
				attrPosMap.put(tag, pos);
				if (tag.startsWith(ATTR_PREFIX)){//gen xml attribute
					sb.append(String.format(" %s=\"%s\" ", tagName, val));
					//need to look ahead for one tag, if last attribute need to close the opening entity
					String nextTag = allTags[i+1];
					if (!nextTag.startsWith(ATTR_PREFIX)){
						sb.append(">");
					}
				}else{//gen xml simple type
					sb.append(String.format("<%s>%s</%s>", tagName, val, tagName));
				}				
			}else if (tag.startsWith(COMPLEX_CLOSING)){
				sb.append(String.format("<%s>", tag));
			}else{
				//complex opening
				sb.append("<" + tag + " ");
				//also need to look ahead for one tag, if no attribute need to close the opening entity
				String nextTag=allTags[i+1];
				if (!nextTag.startsWith(ATTR_PREFIX)){
					sb.append(">");
				}
			}
		}
		
		return sb.toString();
	}

}
