package org.cld.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CsvUtil {
	private static Logger logger =  LogManager.getLogger(CsvUtil.class);
	
	public static void outputCsv(List<String> csv, String file){
		File f = new File(file);
		BufferedWriter osw = null;
		try {
			osw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			if (csv!=null){
				for (String v:csv){
					if (v!=null){
						osw.write(v);//write out value only
						osw.write("\n");
					}
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			if (osw!=null){
				try{
					osw.close();
				}catch(Exception e){
					logger.error("", e);
				}
			}
		}
	}

}
