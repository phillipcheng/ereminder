package org.cld.etl.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.AbstractCrawlItemToCSV;

/**
 * 1 Colum Table followed by 1 Row Table
 * 
 * For example:
 * 
 * C1Name C1Value
 * C2Name C2Value
 * 
 * r1, v1, v2
 * r2, v1, v2
 * 
 * C1Name C1Value
 * C2Name C2Value
 * 
 * r1, v1, v2
 * r2, v1, v2
 * r3, v1, v2
 * r4, v1, v2
 * 
 * Column Table can disappear, then becomes spead row table
 * SectionSepValue is r1
 * 
 */
public class ColTableRowTableAsCSV extends AbstractCrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(ColTableRowTableAsCSV.class);

	public static final String FIELD_NAME_COLDataType="ColDataType";//data type of column table
	public static final String FIELD_NAME_SECSEPVALUE="SectionSepValue";//value to separate col table and row table
	public static final String FIELD_NAME_RowKeyFromColIdx ="RowKeyFromColIdx";//the attribute(s) needs to put in the row table from col tables
	
	public static final String FIELD_NAME_ROWDataType="RowDataType";//data type of row tables
	
	
	public static String processV(String v, String dt){
		if (DATA_TYPE_NUMBER.equals(dt)){
			v = v.replaceAll("[^0-9\\-\\.]", "");
		}
		return v;
	}
	
	//column to csv
	@Override
	public String[][] getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		init(ci, paramMap);
		
		int colDateIdx = -1;
		if (ci.getParam(FIELD_NAME_ColDateIdx)!=null){
			colDateIdx = (int) ci.getParam(FIELD_NAME_ColDateIdx);
		};
		int rowDateIdx = -1;
		if (ci.getParam(FIELD_NAME_RowDateIdx)!=null){
			rowDateIdx = (int) ci.getParam(FIELD_NAME_RowDateIdx);
		};

		List<String> ls = (List<String>)ci.getParam(FIELD_NAME_DATA);
		boolean hasColumnTable = true;
		//for column table
		int colnum = 0;
		List<Integer> rkfcIdx = null;
		String coldatatype = AbstractCrawlItemToCSV.DATA_TYPE_TEXT;
		String colcsvname = null;
		Integer colnumObj = (Integer) ci.getParam(FIELD_NAME_COLNUM);
		if (colnumObj!=null){
			colnum = (int) ci.getParam(FIELD_NAME_COLNUM);
			colcsvname = (String) ci.getParam(FIELD_NAME_COLCSV);
			String dt = (String)ci.getParam(FIELD_NAME_COLDataType);
			if (dt!=null){
				coldatatype = dt;
			}
			rkfcIdx = (List<Integer>) ci.getParam(FIELD_NAME_RowKeyFromColIdx);
		}else{
			hasColumnTable = false;
		}
		//for row table
		int rownum = (int) ci.getParam(FIELD_NAME_ROWNUM);//actually this is the combined column number of each row
		String ssv = (String) ci.getParam(FIELD_NAME_SECSEPVALUE);
		String rowcsvname = (String) ci.getParam(FIELD_NAME_ROWCSV);
		List<String> rowdatatype = new ArrayList<String>();
		rowdatatype.add(DATA_TYPE_TEXT);//default
		Object dtobj = ci.getParam(FIELD_NAME_ROWDataType);
		if (dtobj!=null){
			if (dtobj instanceof List){
				rowdatatype = (List<String>)dtobj;
			}else if (dtobj instanceof String){
				rowdatatype.clear();
				rowdatatype.add((String)dtobj);
			}
		}
		
		List<String> colString = new ArrayList<String>();
		List<String> rowString = new ArrayList<String>();
		
		if (ls!=null){
			int iter=0;
			int idx=0;
			do {
				boolean skipRowTables = false;
				List<String> rkfcValues = new ArrayList<String>();
				List<String> rkfcKeys = new ArrayList<String>();
				if (hasColumnTable){
					String v = null;
					StringBuffer sb = new StringBuffer();
					StringBuffer ksb = new StringBuffer();
					//collect column
					for (int i=0; i<colnum; i++){
						String k = ls.get(idx+2*i);
						v = ls.get(idx+2*i+1);
						v = processV(v, coldatatype);
						if (rkfcIdx.contains(i)){
							rkfcValues.add(v);
							rkfcKeys.add(k);
						}
						
						if (i==colDateIdx && !checkDate(v)){//skip this line
							sb = null;
							skipRowTables = true;
							break;
						}
						if (i>0){
							sb.append(",");
							if (iter==0){
								ksb.append(",");
							}
						}
						if (iter==0){
							ksb.append(k);
						}
						
						sb.append(v);//get the value
					}
					if (genHeader && iter==0){
						colString.add(ksb.toString());
					}
					if (sb!=null){
						colString.add(sb.toString());
					}
					idx += colnum * 2;
				}
				int rowIdx=0; //index of row
				while (idx<ls.size() && (!ssv.equals(ls.get(idx).trim()) || rowIdx==0)){
					if (!skipRowTables){
						StringBuffer sb = new StringBuffer();
						if (rowIdx==0){//gen header
							int p=0; //index of attribute within combined row
							if (hasColumnTable){
								for (String rkfck:rkfcKeys){
									if (p>0){
										sb.append(",");
									}
									sb.append(rkfck);
									p++;
								}
							}
							for (int j=0; j<rownum; j++){
								String v = ls.get(idx++);
								if (hasColumnTable)
									sb.append(",");
								else if (j>0){
									sb.append(",");
								}
								
								sb.append(v);
								p++;
							}
							if (genHeader && iter==0){
								rowString.add(sb.toString());
								//logger.debug("rowtable keys:" + sb.toString());
							}
						}else{
							int p=0;//index of attribute within combined row
							boolean skipRow = false;
							if (hasColumnTable){
								for (String rkfcv:rkfcValues){
									if (p==rowDateIdx && !checkDate(rkfcv)){//skip this line
										sb = null;//clean line
										idx = idx + rownum;//
										skipRow=true;
										break;
									}
									if (p>0){
										sb.append(",");
									}
									sb.append(rkfcv);
									p++;
								}
							}
							if (!skipRow){
								for (int j=0; j<rownum; j++){
									String v = ls.get(idx++);
									if (p==rowDateIdx && !checkDate(v)){//skip this line
										sb = null;//clean line
										idx = idx -1 + rownum - j;//idx does not need to reset, since idx has been ++, so remove 1 first
										break;
									}
									if (hasColumnTable)
										sb.append(",");
									else if (j>0){
										sb.append(",");
									}
									String rdt = rowdatatype.get(0);
									if (rowdatatype.size()>j){
										rdt = rowdatatype.get(j);
									}
									v = processV(v, rdt);
									sb.append(v);
									p++;
								}
								if (sb!=null){
									rowString.add(sb.toString());
								}
							}
						}
					}else{
						idx = idx + rownum;//
					}
					rowIdx++;
				}
				if (idx>=ls.size()){
					break;
				}
				iter++;
			}while(true);
		}else{
			logger.error(String.format("no %s found.", FIELD_NAME_DATA));
		}
		
		String[][] retlist = new String[colString.size()+rowString.size()][3];
		if (colcsvname!=null){
			for (int i=0; i<colString.size();i++){
				retlist[i][0]=keyid;
				retlist[i][1]=colString.get(i);
				retlist[i][2]=colcsvname;
			}
		}
		int colSize = colString.size();
		if (rowcsvname!=null){
			for (int i=0; i<rowString.size(); i++){
				retlist[colSize+i][0]=keyid;
				retlist[colSize+i][1]=rowString.get(i);
				retlist[colSize+i][2]=rowcsvname;
			}
		}
		return retlist;
	}
}
