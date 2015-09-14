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
 *      , c-h1, c-h2, c-h3
 * r-h1 ,
 * atr-1,   v1,   v2,   v3
 * atr-2,   v1,   v2,   v3
 * atr-3,   v1,   v2,   v3
 * 
 * r-h2 ,
 * atr-1,   v1,   v2,   v3
 * atr-2,   v1,   v2,   v3
 * atr-3,   v1,   v2,   v3
 * 
 * the csv output:
 * 
 * c-h1, r-h1, v1, v2, v3
 * c-h1, r-h2, v1, v2, v3
 * c-h2, r-h1, v1, v2, v3
 * c-h2, r-h2, v1, v2, v3
 * c-h3, r-h1, v1, v2, v3
 * c-h3, r-h2, v1, v2, v3
 */
public class CellTableAsCSV extends AbstractCrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(CellTableAsCSV.class);
	private static final String FIELD_NAME_COLHEADER="ColHeader";
	private static final String FIELD_NAME_ROWHEADER="RowHeader";
	
	
	@Override
	public String[][] getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		init(ci, paramMap);
		List<String> ls = (List<String>)ci.getParam(FIELD_NAME_DATA);
		int rownum = (int) ci.getParam(FIELD_NAME_ROWNUM);
		List<String> colHeaders = (List<String>) ci.getParam(FIELD_NAME_COLHEADER);
		List<String> rowHeaders = (List<String>) ci.getParam(FIELD_NAME_ROWHEADER);
		List<String[]> csvs = new ArrayList<String[]>();
		for (int j=0; j<colHeaders.size(); j++){
			for (int k=0; k<rowHeaders.size(); k++){
				String colHeader = colHeaders.get(j);
				String rowHeader = rowHeaders.get(k);
				String[] csv = new String[2];
				StringBuffer sb = new StringBuffer();
				sb.append(colHeader);
				sb.append(",");
				sb.append(rowHeader);
				sb.append(",");
				for (int i=0; i<rownum; i++){
					sb.append(ls.get(j*k+k*rownum+i));
					if (i<rownum-1){
						sb.append(",");
					}
				}
				csv[0] = keyid;
				csv[1] = sb.toString();
				csvs.add(csv);
			}
		}
		
		String[][] retlist = new String[csvs.size()][];
		return csvs.toArray(retlist);
	}
}
