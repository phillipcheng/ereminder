package org.cld.etl.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.AbstractCrawlItemToCSV;

/**
 * has fixed row and column number for a table
 * multiple tables are display, very likely the last table has the same column number but different row number
 * 
 * Sample:
 * 
 * r1, c1, c2
 * r2, c1, c2
 * r3, c1, c2
 * 
 *  
 * r1, c1, c2
 * r2, c1, c2
 * r3, c1, c2
 * 
 * 
 * r1, c1, c2
 * r2, c1, c2
 * 
 */
public class SpreadColTableAsCSV extends AbstractCrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(SpreadColTableAsCSV.class);
	
	/**
	 * example
	 * k1 v1 v2 v3
	 * k2 v1 v2 v3
	 * 
	 * @param vl: column table to csv. a column is a row in the table
	 * @param colnum: how many columns in the page table: e.g. colnum = 3
	 * @return
	 */
	public List<String> colTableToCSV(List<String> vl, int colnum, boolean hasHeader, 
			boolean genHeader, String dataType, int colDateIdx){
		List<String> retList = new ArrayList<String>();
		if (vl!=null){
			for (int i=0; i<colnum; i++){
				int row=0; //row index within the colnum (record)
				StringBuffer sb = new StringBuffer();
				int j=i;//j is the total index of vl
				if (hasHeader && i==0 && !genHeader){
					continue;
				}
				
				while (j<vl.size()){
					String str = vl.get(j);
					str = TableUtil.getValue(str, dataType);
					if (hasHeader && i==0 && genHeader){
						//header
					}else{
						//data do the filter
						if (colDateIdx==row &&!checkDate(str)){
							sb = null;
							break;
						}
					}
					if (j>i){
						sb.append(",");
					}
					sb.append(str);
					
					j+=colnum;
					row++;
				}
				if (sb!=null){
					retList.add(sb.toString());
				}
			}
		}
		return retList;
	}
	
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
		init(ci, paramMap);
		String keyid = (String) ci.getParam(FIELD_NAME_KEYID);
		List<String> ls = (List<String>)ci.getParam(FIELD_NAME_DATA);
		String dataType = DATA_TYPE_NUMBER;
		if (ci.getParam(DATA_TYPE_KEY)!=null){
			dataType = (String) ci.getParam(DATA_TYPE_KEY);
		}
		int colnum = (int) ci.getParam(FIELD_NAME_COLNUM);
		int rownum = -1;
		if (ci.getParam(FIELD_NAME_ROWNUM)!=null){
			rownum = (int) ci.getParam(FIELD_NAME_ROWNUM);
		}
		int colDateIdx = -1;
		if (ci.getParam(FIELD_NAME_ColDateIdx)!=null){
			colDateIdx = (int) ci.getParam(FIELD_NAME_ColDateIdx);
		}
		List<String> csvs = new ArrayList<String>();
		List<String> oneTableValues;
		if (ls!=null){
			int valueFullTable = ls.size();//single col table
			if (rownum!=-1){//real spread col table
				valueFullTable = colnum * rownum;
			}
			int startIdx = 0;
			int endIdx = startIdx + valueFullTable;
			while (endIdx<ls.size()){
				oneTableValues = ls.subList(startIdx, endIdx);
				startIdx +=valueFullTable;
				endIdx +=valueFullTable;
				csvs.addAll(colTableToCSV(oneTableValues, colnum, hasHeader, genHeader, dataType, colDateIdx));
			}
			oneTableValues = ls.subList(startIdx, ls.size());
			int leftItems = ls.size()-startIdx;
			int leftCol = colnum;
			if (rownum!=-1){
				leftCol = leftItems/rownum;
			}
			csvs.addAll(colTableToCSV(oneTableValues, leftCol, hasHeader, genHeader, dataType, colDateIdx));
		}
		
		List<String[]> retlist = new ArrayList<String[]>();
		for (String csv:csvs){
			retlist.add(new String[]{keyid, csv});
		}
		return retlist;
	}
}
