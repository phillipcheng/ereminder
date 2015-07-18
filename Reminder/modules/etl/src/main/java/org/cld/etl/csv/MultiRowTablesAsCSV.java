package org.cld.etl.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;

public class MultiRowTablesAsCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(MultiRowTablesAsCSV.class);

	public static final String FIELD_NAME_ROWCSV="RowCsvName";//csv output name of each row table
		
	//multiple column table to csv, each data1, data2...
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String keyid = (String) ci.getParam(FIELD_NAME_KEYID);
		List<Integer> colnums = (List<Integer>) ci.getParam(FIELD_NAME_COLNUM);
		List<String> colcsvs = (List<String>) ci.getParam(FIELD_NAME_ROWCSV);
		List<String> dataTypes = (List<String>)ci.getParam(DATA_TYPE_KEY);
		if (dataTypes == null){
			dataTypes = new ArrayList<String>();
			for (int i=0; i<colnums.size(); i++){
				dataTypes.add(ICrawlItemToCSV.DATA_TYPE_NUMBER);
			}
		}
		Boolean hasHeader = (Boolean)ci.getParam(KEY_HASHEADER);
		if (hasHeader==null){
			hasHeader = Boolean.TRUE;
		}
		
		boolean genHeader = false;
		Boolean bGenHeader = (Boolean) ci.getParam(KEY_GENHEADER);
		if (bGenHeader!=null){
			genHeader = bGenHeader.booleanValue();
		}
		List<String[]> csvs = new ArrayList<String[]>();
		for (int i=0; i<colnums.size(); i++){
			int colnum = colnums.get(i);
			List<String> ls = (List<String>)ci.getParam(FIELD_NAME_DATA+(i+1));
			String csvname = colcsvs.get(i);
			String dataType = dataTypes.get(i);
			if (ls!=null && ls.size()>=colnum){
				List<String> strs = TableUtil.rowTableToCSV(ls, colnum, hasHeader, dataType);//no header
				for (String str:strs){
					csvs.add(new String[]{keyid, str, csvname});
				}
			}
		}
		return csvs;
	}
}
