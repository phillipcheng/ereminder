package org.cld.etl.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;

public class MultiVarRowTablesToCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(MultiVarRowTablesToCSV.class);
	
	public static final String FIELD_NAME_ROWCSV="RowCsvName";//csv output name of each row table
	public static final String FIELD_NAME_ROWSPAN="RowSpan";
	
	/***
	 * 2009-06-30	营业税	5	5	
				城市维护建设税	7	7	
				教育费附加	3	3	
				企业所得税	25	25	
	   2008-12-31	营业税	5	5	
				城市维护建设税	7	7	
				教育费附加	3	3	
	 */
	
	//row to csv
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String stockid = (String) ci.getParam(FIELD_NAME_KEYID);
		List<String> csvnames = (List<String>)ci.getParam(FIELD_NAME_ROWCSV);
		List<Integer> cnl = new ArrayList<Integer>();
		boolean genHeader = false;
		Boolean bGenHeader = (Boolean) ci.getParam(KEY_GENHEADER);
		if (bGenHeader!=null){
			genHeader = bGenHeader.booleanValue();
		}
		
		List<String[]> retlist = new ArrayList<String[]>();
		for (int i=0; i<csvnames.size(); i++){
			String csvname = csvnames.get(i);
			List<String> vl = (List<String>)ci.getParam(FIELD_NAME_DATA+(i+1));
			List<Integer> rsvl = (List<Integer>)ci.getParam(FIELD_NAME_ROWSPAN+(i+1));
			int colNum = (Integer)ci.getParam(FIELD_NAME_COLNUM+(i+1));
			
			StringBuffer sb = new StringBuffer();
			try{
				if (vl!=null && rsvl!=null){
					int idx=0;
					if (genHeader){
						for (int j=0; j<colNum; j++){
							if (j>0){
								sb.append(",");
							}
							sb.append(vl.get(idx+j));
						}
						retlist.add(new String[]{stockid, sb.toString()});
					}
					idx = idx + colNum;
					int row =0;
					while (idx<vl.size()){
						int rs = rsvl.get(row++);
						String date = vl.get(idx++);
						for (int k=0; k<rs; k++){
							sb = new StringBuffer();
							sb.append(date);
							for (int j=0; j<colNum-1; j++){
								sb.append(",");
								String v = vl.get(idx++);
								//replace comma and new line for csv string
								v = v.replace(",", "\\,").replaceAll("\\r\\n|\\r|\\n", " ");
								sb.append(v);
							}
							retlist.add(new String[]{stockid, sb.toString(), csvname});
						}
					}
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}
		return retlist;
	}
}
