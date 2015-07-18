package org.cld.etl.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;

public class SpreadColTableAsCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(SpreadColTableAsCSV.class);

	
	
	/****
	 * example:
	 * k1 v1 v2 v3
	 * k2 v1 v2 v3
	 * 
	 * k1 v1 v2 v3
	 * k2 v1 v2 v3
	 * colnum: 3
	 * rownum: 2
	 */
	//spread column table(s) to csv
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String keyid = (String) ci.getParam(FIELD_NAME_KEYID);
		List<String> ls = (List<String>)ci.getParam(FIELD_NAME_DATA);
		int colnum = (int) ci.getParam(FIELD_NAME_COLNUM);
		int rownum = (int) ci.getParam(FIELD_NAME_ROWNUM);
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
		List<String> csvs = new ArrayList<String>();
		List<String> oneTableValues;
		if (ls!=null){
			int valueFullTable = colnum * rownum;
			int startIdx = 0;
			int endIdx = startIdx + valueFullTable;
			while (endIdx<ls.size()){
				oneTableValues = ls.subList(startIdx, endIdx);
				startIdx +=valueFullTable;
				endIdx +=valueFullTable;
				csvs.addAll(TableUtil.colTableToCSV(oneTableValues, colnum, hasHeader, genHeader));
			}
			oneTableValues = ls.subList(startIdx, ls.size());
			int leftItems = ls.size()-startIdx;
			int leftCol = leftItems/rownum;
			csvs.addAll(TableUtil.colTableToCSV(oneTableValues, leftCol, hasHeader, genHeader));
		}
		
		List<String[]> retlist = new ArrayList<String[]>();
		for (String csv:csvs){
			retlist.add(new String[]{keyid, csv});
		}
		return retlist;
	}
}
