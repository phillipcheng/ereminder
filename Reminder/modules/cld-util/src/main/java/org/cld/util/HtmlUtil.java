package org.cld.util;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HtmlUtil {

	public static Logger logger = LogManager.getLogger(HtmlUtil.class);
	
	public static String genOptionList(List<String> options, String selected){
		return genOptionList(options, options, selected);
	}
	
	public static String genOptionList(List<String> names, List<String> values, String selectedValue){
		StringBuffer sb = new StringBuffer();
		logger.info(String.format("options names: %s, values:%s, selectedValue:%s", names, values, selectedValue));
		for (int i=0; i<values.size(); i++){
			String optValue = values.get(i);
			String optName = names.get(i);
			String option = "";
			if (selectedValue.equals(optValue)){
				option = "<option selected=\"selected\" value=\"";
				option += optValue + "\">" + optName + "</option>";
			}else{
				option = "<option value=\"";
				option += optValue + "\">" + optName + "</option>";
			}
			sb.append(option);
		}
		return sb.toString();
	}
}
