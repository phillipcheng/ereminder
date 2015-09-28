package org.cld.etl.fci;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;

public abstract class AbstractCrawlItemToCSV {
	
	public static final String FIELD_NAME_KEYID="stockid";
	public static final String FIELD_NAME_STARTDATE="startDate";
	public static final String FIELD_NAME_ENDDATE="endDate";
	public static final String FIELD_NAME_DATA="data";
	public static final String FN_MARKETID="marketId";
	public static final String FN_YEAR="year";
	public static final String FN_QUARTER="quarter";
	public static final String FN_DATE="date";
	public static final String FN_MONTH="month";
	
	public static final String FIELD_NAME_COLNUM="ColNum";
	public static final String FIELD_NAME_ROWNUM="RowNum";
	public static final String FIELD_NAME_COLCSV="ColCsvName";//name of col csv file
	public static final String FIELD_NAME_ColDateIdx="ColDateIdx";//the idx of the date field of the colum table
	public static final String FIELD_NAME_ROWCSV="RowCsvName";//name of row csv file
	public static final String FIELD_NAME_RowDateIdx="RowDateIdx";//the idx of the date field of the row table
	public static final String FIELD_NAME_STATIC = "static";
	
	public static final String KEY_GENHEADER="GenHeader";
	public static final String KEY_HASHEADER="HasHeader"; //default to true
	
	public static final String DATA_TYPE_KEY="DataType";
	public static final String DATA_TYPE_NUMBER="Number";
	public static final String DATA_TYPE_TEXT="Text";
	public static final String DATA_TYPE_DATE="Date";
	
	public static final String KEY_VALUE_UNDEFINED="unused";
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
	
	protected Date startDate = null;
	protected Date endDate = null;
	protected boolean genHeader = false;
	protected boolean hasHeader = true;
	protected String keyid = KEY_VALUE_UNDEFINED;
	private boolean filtered = false;
	
	private static Logger logger =  LogManager.getLogger(AbstractCrawlItemToCSV.class);
	
	//return list of csv value
	//2 tuple: key, value
	//3 tuple: key, value, fileName
	abstract public String[][] getCSV(CrawledItem ci, Map<String, Object> paramMap);
	
	public void init(CrawledItem ci, Map<String, Object> paramMap){
		try{
			String strDate = (String) ci.getParam(FIELD_NAME_STARTDATE);
			if (strDate!=null){
				startDate = sdf.parse(strDate);
			}
			strDate = (String) ci.getParam(FIELD_NAME_ENDDATE);
			if (strDate!=null){
				endDate = sdf.parse(strDate);
			}
		}catch(Exception e){
			logger.error("", e);
		}
		Boolean bGenHeader = (Boolean) ci.getParam(KEY_GENHEADER);
		if (bGenHeader!=null){
			genHeader = bGenHeader.booleanValue();
		}
		Boolean bHasHeader = (Boolean)ci.getParam(KEY_HASHEADER);
		if (bHasHeader!=null){
			hasHeader = bHasHeader.booleanValue();
		}
		String key = (String) ci.getParam(FIELD_NAME_KEYID);
		if (key!=null){
			keyid = key;
		}
	}
	
	//true: date belongs [startDate, endDate)
	//false, the date is filtered out
	public static boolean checkDate(String date, Date startDate, Date endDate, AbstractCrawlItemToCSV aci){
		try {
			Date d = null;
			if ("".equals(date)){//ignore empty compare date records
				return false;
			}else{
				try {
					d = sdf.parse(date);
				}catch(ParseException pe){
					d = sdf2.parse(date);
				}
				if (startDate!=null){
					if (startDate.after(d)){
						aci.setFiltered(true);
						return false;
					}
				}
				if (endDate!=null){
					if (!d.before(endDate)){
						return false;
					}else{
						return true; //startDate not after d and d before endDate
					}
				}else{
					//endDate is null
					return false;
				}
			}
		}catch(Exception e){
			logger.error("", e);
			return false;//wrong date format
		}
	}
	
	public List<String> getDefaultDataType(List<String> dts, int colnum){
		if (dts == null){
			dts = new ArrayList<String>();
		}
		for (int i=dts.size(); i<colnum; i++){
			dts.add(AbstractCrawlItemToCSV.DATA_TYPE_NUMBER);
		}
		return dts;
	}

	public boolean isFiltered() {
		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}
}
