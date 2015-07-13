package org.cld.stock.sina;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;

public class MultiTableColumnNumberAsCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(MultiTableColumnNumberAsCSV.class);

	public static final String FIELD_NAME_KEYID="stockid";
	public static final String FIELD_NAME_DATA="data";
	public static final String FIELD_NAME_COLNUM="ColNum";//of the 1st table (full table)
	public static final String FIELD_NAME_ROWNUM="RowNum";//of the 1st table (full table)
	
	public static final String KEY_GENHEADER="GenHeader";
	
	//column to csv
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String keyid = (String) ci.getParam(FIELD_NAME_KEYID);
		List<String> ls = (List<String>)ci.getParam(FIELD_NAME_DATA);
		int colnum = (int) ci.getParam(FIELD_NAME_COLNUM);
		int rownum = (int) ci.getParam(FIELD_NAME_ROWNUM);
		
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
				csvs.addAll(FRUtil.tableRowToCSV(oneTableValues, colnum, genHeader));
			}
			oneTableValues = ls.subList(startIdx, ls.size());
			int leftItems = ls.size()-startIdx;
			int leftCol = leftItems/rownum;
			csvs.addAll(FRUtil.tableRowToCSV(oneTableValues, leftCol, genHeader));
		}
		
		List<String[]> retlist = new ArrayList<String[]>();
		for (String csv:csvs){
			retlist.add(new String[]{keyid, csv});
		}
		return retlist;
	}
}
