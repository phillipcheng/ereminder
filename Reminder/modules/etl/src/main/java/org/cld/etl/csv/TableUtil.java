package org.cld.etl.csv;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.etl.fci.AbstractCrawlItemToCSV;

public class TableUtil {
	private static Logger logger =  LogManager.getLogger(TableUtil.class);
	
	public static final Map<String, Integer> unitMap = new HashMap<String, Integer>();
	public static final String[] units = new String[]{"万股","百万","万元","千元","元","（元）"};
	public static final int[] numUnits = new int[]{10000,1000000,10000,1000,1,1};
	
	static {
		for (int i=0; i<units.length; i++){
			unitMap.put(units[i], numUnits[i]);
		}
	}
	
	public static String getFRNumber(String instr){
		String str = instr;
		if ("--".equals(str)){
			str = "0";
		}else if (str.contains(",")){
			str = str.replace(",", "");//remove comma
		}
		//百万,万元,元,千元
		str = str.trim();
		for (int k=0; k<units.length; k++){
			String unit = units[k];
			if (str.endsWith(unit)){
				str = str.substring(0, str.indexOf(unit));
				double l = Double.parseDouble(str);
				l = l * unitMap.get(unit);
				str = Double.toString(l);
				break;
			}
		}
		return str;
	}
	

	
	public static String getValue(String v, String dataType){
		if (AbstractCrawlItemToCSV.DATA_TYPE_NUMBER.equals(dataType)){
			v = TableUtil.getFRNumber(v);
		}else if (AbstractCrawlItemToCSV.DATA_TYPE_TEXT.equals(dataType)){
			//replace comma and new line for string
			v = v.replace(",", "\\,").replaceAll("\\r\\n|\\r|\\n", " ");
		}else{
			logger.error(String.format("str type not supported. %s", dataType));
		}
		return v;
	}
}
