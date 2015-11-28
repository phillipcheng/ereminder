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
 * atr-1,   v11,   v21,   v31
 * atr-2,   v12,   v22,   v32
 * atr-3,   v13,   v23,   v33
 * 
 * r-h2 ,
 * atr-1,   v14,   v24,   v34
 * atr-2,   v15,   v25,   v35
 * atr-3,   v16,   v26,   v36
 * 
 * the csv output:
 * 
 * c-h1, r-h1, v11, v12, v13
 * c-h1, r-h2, v14, v15, v16
 * c-h2, r-h1, v21, v22, v23
 * c-h2, r-h2, v24, v25, v26
 * c-h3, r-h1, v31, v32, v33
 * c-h3, r-h2, v34, v35, v36
 */
public class CellTableAsCSV extends AbstractCrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(CellTableAsCSV.class);
	private static final String FIELD_NAME_COLHEADER="ColHeader";
	private static final String FIELD_NAME_ROWHEADER="RowHeader";
	private static final String FIELD_NAME_CELLROWNUMBER="CellRowNum"; //how many rows per cell
	
	private List<String[]> getCSVOneTable(List<String> ls, List<String> colHeaders, List<String> rowHeaders, int cellrownum, int colnum, List<String> dataTypes){
		List<String[]> csvs = new ArrayList<String[]>();
		if (colHeaders!=null){
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
					for (int i=0; i<cellrownum; i++){
						String v = ls.get( k*colnum*cellrownum + i*colnum + j);
						String dataType = AbstractCrawlItemToCSV.DATA_TYPE_NUMBER;
						if (dataTypes!=null && dataTypes.size()>i){
							dataType = dataTypes.get(i);
						}
						v = TableUtil.getValue(v, dataType);
						sb.append(v);
						if (i<cellrownum-1){
							sb.append(",");
						}
					}
					csv[0] = keyid;
					csv[1] = sb.toString();
					csvs.add(csv);
				}
			}
		}
		return csvs;
	}
	
	@Override
	public String[][] getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		init(ci, paramMap);
		List<String> ls = (List<String>)ci.getParam(FN_DATA);
		int cellrownum = (int)ci.getParam(FIELD_NAME_CELLROWNUMBER);//number of items in the cell not including the row-header and col-header
		List<String> colHeaders = (List<String>) ci.getParam(FIELD_NAME_COLHEADER);
		int colnum = (int)ci.getParam(FIELD_NAME_COLNUM);
		List<String> rowHeaders = (List<String>) ci.getParam(FIELD_NAME_ROWHEADER);
		List<String> dataTypes = (List<String>)ci.getParam(DATA_TYPE_KEY); 
		List<String[]> csvs = new ArrayList<String[]>();
		if (colHeaders!=null){
			//split colHeaders into batches with colnum size
			int batchNum = colHeaders.size()/colnum;
			int batchSize = colnum * rowHeaders.size() * cellrownum;
			for (int i=0; i<batchNum; i++){
				List<String> lsb = ls.subList(i*batchSize, (i+1)*batchSize);
				List<String> colHeadersb = colHeaders.subList(i*colnum, (i+1)*colnum);
				csvs.addAll(getCSVOneTable(lsb, colHeadersb, rowHeaders, cellrownum, colnum, dataTypes));
			}
			//for last left batch
			List<String> lsb = ls.subList(batchNum*batchSize, ls.size());
			List<String> colHeadersb = colHeaders.subList(batchNum*colnum, colHeaders.size());
			csvs.addAll(getCSVOneTable(lsb, colHeadersb, rowHeaders, cellrownum, colnum, dataTypes));
		}
		String[][] retlist = new String[csvs.size()][];
		return csvs.toArray(retlist);
	}
}
