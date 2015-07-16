package org.cld.stock.sina;

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

	public static final String FIELD_NAME_KEYID="stockid";
	public static final String FIELD_NAME_DATA="data";
	public static final String FIELD_NAME_COLNUM="ColNum";
	public static final String KEY_GENHEADER="GenHeader";
	
	public static final String DATA_TYPE_KEY="DataType";
	public static final String DATA_TYPE_NUMBER="Number";
	public static final String DATA_TYPE_TEXT="Text";
	
	//column table to csv
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String keyid = (String) ci.getParam(FIELD_NAME_KEYID);
		List<String> ls = (List<String>)ci.getParam(FIELD_NAME_DATA);
		int colnum = (int) ci.getParam(FIELD_NAME_COLNUM);
		String dataType = DATA_TYPE_NUMBER;
		dataType = (String) ci.getParam(DATA_TYPE_KEY);
		
		boolean genHeader = false;
		Boolean bGenHeader = (Boolean) ci.getParam(KEY_GENHEADER);
		if (bGenHeader!=null){
			genHeader = bGenHeader.booleanValue();
		}
		List<String[]> retlist = new ArrayList<String[]>();
		try{
			List<String> csvList = FRUtil.colTableToCSV(ls, colnum, genHeader, dataType);
			for (String csv: csvList){
				retlist.add(new String[]{keyid, csv});
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return retlist;
	}
}
