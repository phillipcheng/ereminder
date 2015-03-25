package org.cld.util;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HtmlUtil {

	public static Logger logger = LogManager.getLogger(HtmlUtil.class);
	
	public static String genOptionList(Collection<String> options, String selected){
		StringBuffer sb = new StringBuffer();
		logger.info("input: options:" + options);
		logger.info("input: selected:" + selected);
		for (String opt:options){
			String option = "";
			if (selected.equals(opt)){
				option = "<option selected=\"selected\" value=\"";
				option += opt + "\">" + opt + "</option>";
			}else{
				option = "<option value=\"";
				option += opt + "\">" + opt + "</option>";
			}
			sb.append(option);
		}
		return sb.toString();
	}
}
