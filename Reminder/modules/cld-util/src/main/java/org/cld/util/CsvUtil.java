package org.cld.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.entity.CrawledItem;

public class CsvUtil {
	private static Logger logger =  LogManager.getLogger(CsvUtil.class);
	
	public static List<String> outputCsv(List<CrawledItem> cil, String file){
		List<String> csvs = new ArrayList<String>();
		for (CrawledItem ci:cil){
			if (ci.getCsvValue()!=null){
				for (String[] csv: ci.getCsvValue()){
					if (csv!=null && csv.length==2){
						csvs.add(csv[1]);
					}
				}
			}
		}
		if (file!=null){
			File f = new File(file);
			BufferedWriter osw = null;
			try {
				osw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
				if (csvs!=null){
					for (String v:csvs){
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
		return csvs;
	}
}
