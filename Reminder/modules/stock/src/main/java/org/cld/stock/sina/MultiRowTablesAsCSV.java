package org.cld.stock.sina;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;

public class MultiRowTablesAsCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(MultiRowTablesAsCSV.class);

	public static final String FIELD_NAME_KEYID="stockid";
	public static final String FIELD_NAME_DATA="data";//list of data for each row table
	public static final String FIELD_NAME_ROWNUM="RowNum";//row number of each row table
	public static final String FIELD_NAME_ROWCSV="RowCsvName";//csv output name of each row table

	public static final String KEY_GENHEADER="GenHeader";
	
	//multiple column table to csv, each data1, data2...
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String keyid = (String) ci.getParam(FIELD_NAME_KEYID);
		List<Integer> colnums = (List<Integer>) ci.getParam(FIELD_NAME_ROWNUM);
		List<String> colcsvs = (List<String>) ci.getParam(FIELD_NAME_ROWCSV);
		
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
			if (ls!=null && ls.size()>=colnum){
				List<String> strs = FRUtil.rowTableToCSV(ls, colnum, true);//no header
				for (String str:strs){
					csvs.add(new String[]{keyid, str, csvname});
				}
			}
		}
		return csvs;
	}
}
