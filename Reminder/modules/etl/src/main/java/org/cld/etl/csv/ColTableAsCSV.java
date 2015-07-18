package org.cld.etl.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;

public class ColTableAsCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(ColTableAsCSV.class);
	
	//column table to csv
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String keyid = (String) ci.getParam(FIELD_NAME_KEYID);
		List<String> ls = (List<String>)ci.getParam(FIELD_NAME_DATA);
		int colnum = (int) ci.getParam(FIELD_NAME_COLNUM);
		String dataType = DATA_TYPE_NUMBER;
		String dt = (String) ci.getParam(DATA_TYPE_KEY);
		if (dt!=null){
			dataType = dt;
		}
		boolean hasHeader=true;
		Boolean bHasHeader = (Boolean)ci.getParam(KEY_HASHEADER);
		if (bHasHeader!=null){
			hasHeader = bHasHeader.booleanValue();
		}
		
		boolean genHeader = false;
		Boolean bGenHeader = (Boolean) ci.getParam(KEY_GENHEADER);
		if (bGenHeader!=null){
			genHeader = bGenHeader.booleanValue();
		}
		List<String[]> retlist = new ArrayList<String[]>();
		try{
			List<String> csvList = TableUtil.colTableToCSV(ls, colnum, hasHeader, genHeader, dataType);
			for (String csv: csvList){
				retlist.add(new String[]{keyid, csv});
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return retlist;
	}
}
