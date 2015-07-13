package org.cld.stock.sina;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;

public class ColumnNumberAsCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(ColumnNumberAsCSV.class);

	public static final String FIELD_NAME_KEYID="stockid";
	public static final String FIELD_NAME_DATA="data";
	public static final String FIELD_NAME_COLNUM="ColNum";
	public static final String KEY_GENHEADER="GenHeader";
	
	//column to csv
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String keyid = (String) ci.getParam(FIELD_NAME_KEYID);
		List<String> ls = (List<String>)ci.getParam(FIELD_NAME_DATA);
		int colnum = (int) ci.getParam(FIELD_NAME_COLNUM);
		
		boolean genHeader = false;
		Boolean bGenHeader = (Boolean) ci.getParam(KEY_GENHEADER);
		if (bGenHeader!=null){
			genHeader = bGenHeader.booleanValue();
		}
		List<String[]> retlist = new ArrayList<String[]>();
		try{
			List<String> csvList = FRUtil.tableRowToCSV(ls, colnum, genHeader);
			for (String csv: csvList){
				retlist.add(new String[]{keyid, csv});
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return retlist;
	}
}
