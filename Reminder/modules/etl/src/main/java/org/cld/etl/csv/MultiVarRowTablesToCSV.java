package org.cld.etl.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.util.entity.CrawledItem;

public class MultiVarRowTablesToCSV extends AbstractCrawlItemToCSV{
	
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
	public String[][] getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		init(ci, paramMap);
		List<String> csvnames = (List<String>)ci.getParam(FIELD_NAME_ROWCSV);
		List<Integer> cnl = new ArrayList<Integer>();
		List<Integer> rowDateIdxList = null;
		if (ci.getParam(FIELD_NAME_RowDateIdx)!=null){
			rowDateIdxList = (List<Integer>) ci.getParam(FIELD_NAME_RowDateIdx);
		};
		
		List<String[]> retlist = new ArrayList<String[]>();
		for (int i=0; i<csvnames.size(); i++){
			String csvname = csvnames.get(i);
			List<String> vl = (List<String>)ci.getParam(FN_DATA+(i+1));
			List<Integer> rsvl = (List<Integer>)ci.getParam(FIELD_NAME_ROWSPAN+(i+1));
			int colNum = (Integer)ci.getParam(FIELD_NAME_COLNUM+(i+1));
			int rowDateIdx=-1;
			if (rowDateIdxList!=null){
				rowDateIdx = rowDateIdxList.get(i);
			}
			StringBuffer sb = new StringBuffer();
			try{
				if (vl!=null && rsvl!=null && rsvl.size()>0){
					int idx=0;
					if (genHeader){
						for (int j=0; j<colNum; j++){
							if (j>0){
								sb.append(",");
							}
							sb.append(vl.get(idx+j));
						}
						retlist.add(new String[]{keyid, sb.toString()});
					}
					idx = idx + colNum;
					int row =0;
					while (idx<vl.size()){
						int rs = rsvl.get(row++);
						String v1 = vl.get(idx++);
						if (rowDateIdx==0 && !checkDate(v1, startDate, endDate, this)){//skip the whole row span block
							idx += rs*(colNum-1);
							continue;
						}
						for (int k=0; k<rs; k++){
							sb = new StringBuffer();
							sb.append(v1);
							for (int j=0; j<colNum-1; j++){
								sb.append(",");
								String v = vl.get(idx++);
								if ((j+1)==rowDateIdx && !checkDate(v, startDate, endDate, this)){
									sb = new StringBuffer();//clean buffer
									idx = idx -1 + colNum-1-j;//skip to next line
									break;
								}
								v = v.replace(",", "\\,").replaceAll("\\r\\n|\\r|\\n", " ");//replace comma and new line for csv string
								sb.append(v);
							}
							retlist.add(new String[]{keyid, sb.toString(), csvname});
						}
					}
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}
		String[][] retCsvs = new String[retlist.size()][];
		for (int i=0; i<retlist.size(); i++){
			retCsvs[i] = retlist.get(i);
		}
		return retCsvs;
	}
}
