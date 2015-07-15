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

	public static final String FIELD_NAME_KEYID="stockid";
	public static final String FIELD_NAME_DATA="data";
	public static final String KEY_GENHEADER="GenHeader";
	
	public static final String FIELD_NAME_COLNUM="ColNum";//number of fields for the col table
	public static final String FIELD_NAME_COLCSV="ColCsvName";//number of col csv file
	
	public static final String FIELD_NAME_SECSEPVALUE="SectionSepValue";//value to separate col table and row table
	public static final String FIELD_NAME_RowKeyFromColIdx ="RowKeyFromColIdx";//the attribute needs to put in the row table from col tables
	
	public static final String FIELD_NAME_ROWNUM="RowNum";//number of fields for the row table
	public static final String FIELD_NAME_ROWCSV="RowCsvName";//number of row csv file
	
	
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
		int colnum = (int) ci.getParam(FIELD_NAME_COLNUM);
		int rownum = (int) ci.getParam(FIELD_NAME_ROWNUM);
		String ssv = (String) ci.getParam(FIELD_NAME_SECSEPVALUE);
		int rkfcIdx = (int) ci.getParam(FIELD_NAME_RowKeyFromColIdx);
		String colcsvname = (String) ci.getParam(FIELD_NAME_COLCSV);
		String rowcsvname = (String) ci.getParam(FIELD_NAME_ROWCSV);
		
		List<String> colString = new ArrayList<String>();
		List<String> rowString = new ArrayList<String>();
		
		StringBuffer sb = new StringBuffer();
		
		int idx=0;
		do {
			String rkfc = null;
			//collect column
			for (int i=0; i<colnum; i++){
				if (i>0){
					sb.append(",");
				}
				String v = ls.get(idx+2*i+1);
				if (rkfcIdx == i){
					rkfc = v;
				}
				sb.append(v);//get the value
				colString.add(sb.toString());
			}
			
			idx += colnum * 2;
			int rowIdx=0;
			while (idx<ls.size() && !ssv.equals(ls.get(idx))){
				sb = new StringBuffer();
				if (rowIdx==0){
					if (genHeader){
						sb.append("rowkeyFromCol");
						for (int j=0; j<rownum; j++){
							sb.append(",");
							sb.append(ls.get(idx++));
						}
						rowString.add(sb.toString());
					}
				}else{
					sb.append(rkfc);
					for (int j=0; j<rownum; j++){
						sb.append(",");
						sb.append(ls.get(idx++));
					}
					rowString.add(sb.toString());
				}
			}
			if (idx>=ls.size()){
				break;
			}
		}while(true);
		
		List<String[]> retlist = new ArrayList<String[]>();
		for (String csv:colString){
			retlist.add(new String[]{keyid, csv, colcsvname});
		}
		for (String csv:rowString){
			retlist.add(new String[]{keyid, csv, rowcsvname});
		}
		return retlist;
	}
}
