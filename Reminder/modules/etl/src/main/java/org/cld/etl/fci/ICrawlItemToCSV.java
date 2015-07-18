package org.cld.etl.fci;

import java.util.List;
import java.util.Map;

import org.cld.datastore.entity.CrawledItem;

public interface ICrawlItemToCSV {
	public static final String FIELD_NAME_KEYID="stockid";
	public static final String FIELD_NAME_DATA="data";
	
	public static final String FIELD_NAME_COLNUM="ColNum";
	public static final String FIELD_NAME_ROWNUM="RowNum";
	
	public static final String KEY_GENHEADER="GenHeader";
	public static final String KEY_HASHEADER="HasHeader";
	
	public static final String DATA_TYPE_KEY="DataType";
	public static final String DATA_TYPE_NUMBER="Number";
	public static final String DATA_TYPE_TEXT="Text";
	
	//return list of csv value
	//2 tuple: key, value
	//3 tuple: key, value, fileName
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap);
}
