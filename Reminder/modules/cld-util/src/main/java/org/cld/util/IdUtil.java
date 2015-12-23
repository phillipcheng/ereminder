package org.cld.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IdUtil {
	
	private static final String dateFormat = "yyyyMMddHHmmssSSS";
	//hostId + threadId + time
	public static String getId(String hostId){
		long tid = Thread.currentThread().getId();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date d = new Date();
		String did = sdf.format(d);
		return hostId + "_" + tid + "_" + did;
	}

}
