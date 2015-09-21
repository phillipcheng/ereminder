package org.cld.etl.csv;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.etl.fci.AbstractCrawlItemToCSV;

public class TableUtil {
	private static Logger logger =  LogManager.getLogger(TableUtil.class);
	
	public static final Map<String, Integer> unitMap = new HashMap<String, Integer>();
	
	public static final String[] units = new String[]{"万股","百万","万元","千元","元","（元）"};
	public static final int[] numUnits = new int[]{10000,1000000,10000,1000,1,1};
	public static final Map<String, Integer> scales = new HashMap<String, Integer>();
	static{
		scales.put("万股", 10000);
		scales.put("百万", 1000000);
		scales.put("万股", 10000);
		scales.put("千元", 1000);
		scales.put("元", 1);
		scales.put("（元）", 1);
		scales.put("(t)", 1000);
		scales.put("(m)", 1000000);
	}
	public static final SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy");
	public static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

	public static final SimpleDateFormat outsdf = new SimpleDateFormat("yyyy-MM-dd");
	
	static {
		for (int i=0; i<units.length; i++){
			unitMap.put(units[i], numUnits[i]);
		}
	}
	
	public static Pattern negP = Pattern.compile("\\((.*)\\)");
	public static Pattern endingP = Pattern.compile("([\\-\\.0-9]+)\\((.*)\\)");
	
	public static String getFRNumber(String instr){
		String str = instr;
		if ("--".equals(str)){
			return "0";
		}
		//remove comma
		if (str.contains(",")){
			str = str.replace(",", "");
		}
		//change () into '-' sign
		Matcher m = negP.matcher(str);
		if (m.matches()){
			str = "-" + m.group(1);
		}
		//remove money sign
		str = str.replace("$", "");
		str = str.replace("\u00a0","");//remove &nbsp;
		str = str.replace("\u0020", "");
		//multiple scale
		m = endingP.matcher(str);
		if (m.matches()){
			float f = Float.parseFloat(m.group(1));
			String unit = m.group(2).trim();
			if (scales.containsKey(unit)){
				f = f * scales.get(unit);
				str = Float.toString(f);
			}else{
				//remove suffix anyway
				str = m.group(1);
			}
		}
		return str;
	}

	
	public static String getValue(String v, String dataType){
		String ret = "";
		if (AbstractCrawlItemToCSV.DATA_TYPE_NUMBER.equals(dataType)){
			ret = TableUtil.getFRNumber(v);
		}else if (AbstractCrawlItemToCSV.DATA_TYPE_TEXT.equals(dataType)){
			//replace comma and new line for string
			ret = v.replace(",", "\\,").replaceAll("\\r\\n|\\r|\\n", " ");
		}else if (AbstractCrawlItemToCSV.DATA_TYPE_DATE.equals(dataType)){
			Date d = null;
			try{
				d = sdf1.parse(v);
			}catch(Exception e){
				try{
					d = sdf2.parse(v);
				}catch(Exception e1){
					logger.error("we can't parse date type string:" + v);
				}
			}
			if (d!=null){
				ret = outsdf.format(d);
			}
		}else{
			logger.error(String.format("str type not supported. %s", dataType));
		}
		logger.debug(String.format("get value for %s of type:%s. ret:%s", v, dataType, ret));
		return ret;
	}
}
