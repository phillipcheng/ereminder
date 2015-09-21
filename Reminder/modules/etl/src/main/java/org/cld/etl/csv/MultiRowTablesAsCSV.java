package org.cld.etl.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.AbstractCrawlItemToCSV;

public class MultiRowTablesAsCSV extends AbstractCrawlItemToCSV{
	private static Logger logger =  LogManager.getLogger(MultiRowTablesAsCSV.class);
	

	
	/**
	 * 
	 * @param vl: row table to csv
	 * @param colnum
	 * @return
	 */
	public static List<String> rowTableToCSV(List<String> vl, int colnum, boolean hasHeader, 
			List<String> dataTypes, int dateIdx, String dateCompareWithValue, Date startDate, Date endDate){
		List<String> retList = new ArrayList<String>();
		if (vl!=null){
			StringBuffer sb = null;
			int row=0;
			int coln=0;
			for (int i=0; i<vl.size(); ){
				row = i / colnum;
				coln = i % colnum;
				if (row==0 && !hasHeader || row>0){//has header skip first row
					if (coln == 0){
						if (sb!=null){
							retList.add(sb.toString());
						}
						sb = new StringBuffer();
					}
					if (coln>0){
						sb.append(",");
					}
					String dataType = dataTypes.get(coln);
					try{
						String v = TableUtil.getValue(vl.get(i), dataType);
						if (coln==dateIdx && !checkDate(v, dateCompareWithValue, startDate, endDate)){
							sb = null;
							i = (row+1) * colnum;
							continue;
						}
						sb.append(v);
					}catch(Exception e){
						logger.error("", e);
					}
				}
				i++;
			}
			if (sb!=null && (coln==(colnum-1))){// a full line
				retList.add(sb.toString());
			}
		}
		
		return retList;
	}
	
	//multiple column table to csv, each data1, data2...
	@Override
	public String[][] getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		init(ci, paramMap);
		List<Integer> colnums = (List<Integer>) ci.getParam(FIELD_NAME_COLNUM);
		List<String> rowcsvs = (List<String>) ci.getParam(FIELD_NAME_ROWCSV);
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
				strs = rowTableToCSV(ls, colnum, hasHeader, dataTypes, dIdx, dateCompareWithValue, startDate, endDate);
			}
			if (strs!=null){
				if (rowcsvs!=null){
					String csvname = rowcsvs.get(i);
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
