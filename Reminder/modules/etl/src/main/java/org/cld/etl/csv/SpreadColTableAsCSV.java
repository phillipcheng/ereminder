package org.cld.etl.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.util.entity.CrawledItem;

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
	public static List<String> colTableToCSV(List<String> vl, int colnum, boolean hasHeader, 
			boolean genHeader, List<String> dataTypes, int colDateIdx, Date startDate, Date endDate, AbstractCrawlItemToCSV aci, 
			Set<String> keySet, List<String> keydata, boolean keyPartialMatch){
		List<String> retList = new ArrayList<String>();
		if (vl!=null){
			for (int i=0; i<colnum; i++){
				int row=0; //row index within the colnum (record)
				StringBuffer sb = new StringBuffer();
				int j=i;//j is the total index of vl
				if (hasHeader && i==0 && !genHeader){
					continue;
				}
				int selectedRowIdx=0;//the selected row index
				while (j<vl.size()){
					String str = vl.get(j);
					boolean need=false;
					if (keydata!=null){//do key filtering
						String key = keydata.get(row);
						if(!keySet.contains(key)){
							if (keyPartialMatch){
								for (String ke:keySet){
									if (key.contains(ke)){
										need=true;
										break;
									}
								}
							}else{
								need=false;
							}
						}else{
							need=true;
						}
					}else{
						need=true;
					}
					if (need){
						String dataType = AbstractCrawlItemToCSV.DATA_TYPE_NUMBER;
						if (dataTypes!=null && dataTypes.size()>selectedRowIdx){
							dataType = dataTypes.get(selectedRowIdx);
						}
						str = TableUtil.getValue(str, dataType);
						selectedRowIdx++;
						if (hasHeader && i==0 && genHeader){
							//header
						}else{
							//data do the filter
							if (colDateIdx==row &&!checkDate(str, startDate, endDate, aci)){
								sb = null;
								break;
							}
						}
						if (selectedRowIdx>1){
							sb.append(",");
						}
						sb.append(str);
					}
					
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
	//spread column table(s) to csv, here the concept of row and column are swapped
	@Override
	public String[][] getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		init(ci, paramMap);
		List<String> ls = (List<String>)ci.getParam(FN_DATA);
		int colnum = (int) ci.getParam(FIELD_NAME_COLNUM);
		if (colnum==0){
			return new String[0][];
		}
		List<String> dataTypes = null;
		Object dtSet = ci.getParam(DATA_TYPE_KEY);
		int fieldnum = ls.size()/colnum;
		if (dtSet instanceof List){
			dataTypes=this.getDefaultDataType((List<String>)dtSet, fieldnum);
		}else if (dtSet instanceof String) {
			dataTypes=this.getDefaultDataType((String)dtSet, fieldnum);
		}
		
		int rownum = -1;
		if (ci.getParam(FIELD_NAME_ROWNUM)!=null){
			rownum = (int) ci.getParam(FIELD_NAME_ROWNUM);
		}
		int colDateIdx = -1;
		if (ci.getParam(FIELD_NAME_ColDateIdx)!=null){
			colDateIdx = (int) ci.getParam(FIELD_NAME_ColDateIdx);
		}
		//key matching
		List<String> keys = (List<String>)ci.getParam(FIELD_NAME_KEYS);
		Set<String> keySet = new HashSet<String>();
		if (keys!=null){
			for(String key:keys){
				keySet.add(key.trim());
			}
		}
		List<String> keydata = (List<String>)ci.getParam(FIELD_NAME_KEYDATA);
		boolean keyPartialMatch = true;
		if (ci.getParam(FIELD_NAME_KEYPARTIALMATCH)!=null){
			keyPartialMatch = (boolean) ci.getParam(FIELD_NAME_KEYPARTIALMATCH);
		}
		List<String> csvs = new ArrayList<String>();
		List<String> oneTableValues;
		if (ls!=null && ls.size()>0){
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
				csvs.addAll(colTableToCSV(oneTableValues, colnum, hasHeader, genHeader, dataTypes, colDateIdx, startDate, endDate, this, 
						keySet, keydata, keyPartialMatch));
			}
			oneTableValues = ls.subList(startIdx, ls.size());
			int leftItems = ls.size()-startIdx;
			int leftCol = colnum;
			if (rownum!=-1){
				leftCol = leftItems/rownum;
			}
			csvs.addAll(colTableToCSV(oneTableValues, leftCol, hasHeader, genHeader, dataTypes, colDateIdx, startDate, endDate, 
					this, keySet, keydata, keyPartialMatch));
		}
		
		String[][] retlist = new String[csvs.size()][];
		for (int i=0; i<csvs.size(); i++){
			retlist[i] = new String[]{keyid, csvs.get(i)};
		}
		return retlist;
	}
}
