package org.cld.stock.sina;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;

public class ColTableRowTableAsCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(ColTableRowTableAsCSV.class);

	public static final String DATA_TYPE_NUMBER="Number";
	public static final String DATA_TYPE_STRING="String";
	
	public static final String FIELD_NAME_KEYID="stockid";
	public static final String FIELD_NAME_DATA="data";
	public static final String KEY_GENHEADER="GenHeader";
	
	public static final String FIELD_NAME_COLNUM="ColNum";//number of fields for the col table
	public static final String FIELD_NAME_COLCSV="ColCsvName";//number of col csv file
	public static final String FIELD_NAME_COLDataType="ColDataType";//data type of column table
	
	public static final String FIELD_NAME_SECSEPVALUE="SectionSepValue";//value to separate col table and row table
	public static final String FIELD_NAME_RowKeyFromColIdx ="RowKeyFromColIdx";//the attribute(s) needs to put in the row table from col tables
	
	public static final String FIELD_NAME_ROWNUM="RowNum";//number of fields for the row table
	public static final String FIELD_NAME_ROWCSV="RowCsvName";//number of row csv file
	public static final String FIELD_NAME_ROWDataType="RowDataType";//data type of row table
	
	public static String processV(String v, String dt){
		if (DATA_TYPE_NUMBER.equals(dt)){
			v = v.replaceAll("[^0-9\\-]", "");
		}
		return v;
	}
	
	//column to csv
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String keyid = (String) ci.getParam(FIELD_NAME_KEYID);
		List<String> ls = (List<String>)ci.getParam(FIELD_NAME_DATA);
		boolean genHeader = false;
		Boolean bGenHeader = (Boolean) ci.getParam(KEY_GENHEADER);
		if (bGenHeader!=null){
			genHeader = bGenHeader.booleanValue();
		}
		boolean hasColumnTable = true;
		//for column table
		int colnum = 0;
		List<Integer> rkfcIdx = null;
		String coldatatype = DATA_TYPE_STRING;
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
		int rownum = (int) ci.getParam(FIELD_NAME_ROWNUM);
		String ssv = (String) ci.getParam(FIELD_NAME_SECSEPVALUE);
		String rowcsvname = (String) ci.getParam(FIELD_NAME_ROWCSV);
		String rowdatatype = DATA_TYPE_STRING;
		String dt = (String)ci.getParam(FIELD_NAME_ROWDataType);
		if (dt!=null){
			rowdatatype = dt;
		}
		
		List<String> colString = new ArrayList<String>();
		List<String> rowString = new ArrayList<String>();
		
		if (ls!=null){
			int iter=0;
			int idx=0;
			do {
				List<String> rkfcValues = new ArrayList<String>();
				List<String> rkfcKeys = new ArrayList<String>();
				if (hasColumnTable){
					String v = null;
					StringBuffer sb = new StringBuffer();
					StringBuffer ksb = new StringBuffer();
					//collect column
					for (int i=0; i<colnum; i++){
						if (i>0){
							sb.append(",");
							if (iter==0){
								ksb.append(",");
							}
						}
						String k = ls.get(idx+2*i);
						if (iter==0){
							ksb.append(k);
						}
						
						v = ls.get(idx+2*i+1);
						v = processV(v, coldatatype);
						if (rkfcIdx.contains(i)){
							rkfcValues.add(v);
							rkfcKeys.add(k);
						}
						sb.append(v);//get the value
					}
					if (genHeader && iter==0){
						colString.add(ksb.toString());
					}
					colString.add("coltable:" + sb.toString());
					logger.debug(sb.toString());
					
					idx += colnum * 2;
				}
				int rowIdx=0;
				while (idx<ls.size() && (!ssv.equals(ls.get(idx).trim()) || rowIdx==0)){
					StringBuffer sb = new StringBuffer();
					if (rowIdx==0){
						if (hasColumnTable){
							int p=0;
							for (String rkfck:rkfcKeys){
								if (p>0){
									sb.append(",");
								}
								sb.append(rkfck);
								p++;
							}
						}
						for (int j=0; j<rownum; j++){
							if (hasColumnTable)
								sb.append(",");
							else if (j>0){
								sb.append(",");
							}
							String v = ls.get(idx++);
							sb.append(v);
						}
						if (genHeader && iter==0){
							rowString.add(sb.toString());
							logger.debug("rowtable keys:" + sb.toString());
						}
					}else{
						if (hasColumnTable){
							int k=0;
							for (String rkfcv:rkfcValues){
								if (k>0){
									sb.append(",");
								}
								sb.append(rkfcv);
								k++;
							}
						}
						for (int j=0; j<rownum; j++){
							if (hasColumnTable)
								sb.append(",");
							else if (j>0){
								sb.append(",");
							}
							String v = ls.get(idx++);
							v = processV(v, rowdatatype);
							sb.append(v);
						}
						rowString.add(sb.toString());
						logger.debug("rowtable values:" + sb.toString());
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
		
		List<String[]> retlist = new ArrayList<String[]>();
		if (colcsvname!=null){
			for (String csv:colString){
				retlist.add(new String[]{keyid, csv, colcsvname});
			}
		}
		if (rowcsvname!=null){
			for (String csv:rowString){
				retlist.add(new String[]{keyid, csv, rowcsvname});
			}
		}
		return retlist;
	}
}
