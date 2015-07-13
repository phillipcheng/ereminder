package org.cld.stock.sina;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;

public class FRFootNoteToCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(FRFootNoteToCSV.class);

	public static final String FIELD_NAME_STOCKID = "stockid";
	public static final String FR_FOOTNOTE_ATTR_NAME = "attrKey";
	public static final String ROW_SPAN="RowSpan";
	public static final String COL_NUM="ColNum";
	public static final String KEY_GENHEADER="GenHeader";
	
	public static final String[] FootNoteSubArea = new String[]{"accountRecievable", "inventory", 
		"recievableAging", "tax", "incomeStructureByIndustry", 
		"incomeStructureByProduct", "incomeStructureByRegion"};
	
	//row to csv
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String stockid = (String) ci.getParam(FIELD_NAME_STOCKID);
		String attrKeyValue = (String) paramMap.get(FR_FOOTNOTE_ATTR_NAME);
		String attrRowspanKeyValue = attrKeyValue + ROW_SPAN;
		String attrColNumKeyValue = attrKeyValue + COL_NUM;
		
		List<String> vl = (List<String>)ci.getParam(attrKeyValue);//list of values
		List<String> rsvl = (List<String>)ci.getParam(attrRowspanKeyValue);//list of rowspan
		int colNum = (int) ci.getParam(attrColNumKeyValue);
		boolean genHeader = false;
		Boolean bGenHeader = (Boolean) ci.getParam(KEY_GENHEADER);
		if (bGenHeader!=null){
			genHeader = bGenHeader.booleanValue();
		}
		List<String[]> retlist = new ArrayList<String[]>();
		StringBuffer sb = new StringBuffer();
		try{
			if (vl!=null && rsvl!=null){
				int idx=0;
				if (genHeader){
					for (int i=0; i<colNum; i++){
						if (i>0){
							sb.append(",");
						}
						sb.append(vl.get(idx+i));
					}
					retlist.add(new String[]{stockid, sb.toString()});
				}
				idx = idx + colNum;
				int row =0;
				while (idx<vl.size()){
					int rs = Integer.parseInt(rsvl.get(row++));
					String date = vl.get(idx++);
					for (int i=0; i<rs; i++){
						sb = new StringBuffer();
						sb.append(date);
						for (int j=0; j<colNum-1; j++){
							sb.append(",");
							String v = vl.get(idx++);
							//replace comma and new line for csv string
							v = v.replace(",", "\\,").replaceAll("\\r\\n|\\r|\\n", " ");
							sb.append(v);
						}
						retlist.add(new String[]{stockid, sb.toString()});
					}
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return retlist;
	}
}
