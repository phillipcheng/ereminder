package org.cld.etl.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.AbstractCrawlItemToCSV;

/**
 * multiple col table each can has different col, type, etc
 * 
 * Sample:
 * 
 * r1, c1, c2
 * r2, c1, c2
 * r3, c1, c2
 *  
 * r1, c1
 * r2, c1
 * r3, c1
 * 
 * 
 * r1, c1, c2, c3
 * r2, c1, c2, c3
 * 
 */
public class MultiColTablesAsCSV extends AbstractCrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(MultiColTablesAsCSV.class);
	
	//spread column table(s) to csv, here the concept of row and column are swapped
	@Override
	public String[][] getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		init(ci, paramMap);
		List<Integer> colnums = (List<Integer>) ci.getParam(FIELD_NAME_COLNUM);
		List<String> colcsvs = (List<String>) ci.getParam(FIELD_NAME_COLCSV);
		List<Integer> dateIdx = (List<Integer>) ci.getParam(FIELD_NAME_RowDateIdx);
		List<String[]> csvs = new ArrayList<String[]>();
		for (int i=0; i<colnums.size(); i++){
			int colnum = colnums.get(i);
			int dIdx = -1;
			if (dateIdx!=null){
				dIdx = dateIdx.get(i);
			}
			List<String> ls = (List<String>)ci.getParam(FIELD_NAME_DATA+(i+1));
			List<String> dataTypes = (List<String>)ci.getParam(DATA_TYPE_KEY+(i+1));
			dataTypes = getDefaultDataType(dataTypes, colnum);
			
			List<String> strs = null;
			if (ls!=null && ls.size()>=colnum){
				strs = SpreadColTableAsCSV.colTableToCSV(ls, colnum, hasHeader, genHeader, dataTypes, dIdx, dateCompareWithValue, startDate, endDate);
			}
			if (strs!=null){
				if (colcsvs!=null){
					String csvname = colcsvs.get(i);
					for (String str:strs){
						csvs.add(new String[]{keyid, str, csvname});
					}
				}else{
					for (String str:strs){
						csvs.add(new String[]{keyid, str});
					}
				}
			}
		}
		String[][] retCsvs = new String[csvs.size()][];
		for (int i=0; i<csvs.size(); i++){
			retCsvs[i] = csvs.get(i);
		}
		return retCsvs;
		
	}
}
