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
	
	public static final String[] units = new String[]{"万股","百万","万元","千元","（元）","元","(t)","(m)"};
	public static final int[] numUnits = new int[]{10000,1000000,10000,1000,1,1,1000,1000000};
	public static final SimpleDateFormat[] sdfs = new SimpleDateFormat[]{
			new SimpleDateFormat("MM/dd/yyyy"),
			new SimpleDateFormat("yyyy-MM-dd"), 
			new SimpleDateFormat("yyyy"),
			new SimpleDateFormat("MMM d, yyyy")};
	public static final String NO_DATE = "n/a";
	
	public static final SimpleDateFormat outsdf = new SimpleDateFormat("yyyy-MM-dd");
	
	static {
		for (int i=0; i<units.length; i++){
			unitMap.put(units[i], numUnits[i]);
		}
	}
	
	public static Pattern negP = Pattern.compile("\\((.*)\\)");
	public static Pattern endingP = Pattern.compile("(.*)\\((.*)\\)");
	
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
		//remove the ending parenthesis
		m = endingP.matcher(str);
		if (m.matches()){
			str = m.group(1);
		}
		//multiple scale
		for (int i=0;i<units.length;i++){
			int idx = str.lastIndexOf(units[i]);
			if (idx>-1){
				str = str.substring(0, idx);
				float f = Float.parseFloat(str.trim());
				f = f * numUnits[i];
				return Float.toString(f);
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
			ret = v.replace(",", ";").replaceAll("\\r\\n|\\r|\\n", " ").replace("\u0020", " ").replace("\u00a0"," ");
		}else if (AbstractCrawlItemToCSV.DATA_TYPE_DATE.equals(dataType)){
			if (v.equals(NO_DATE)){
				ret = "";
			}else{
				Date d = null;
				for (SimpleDateFormat sdf:sdfs){
					try{
						d = sdf.parse(v);
						break;
					}catch(Exception e){
						continue;
					}
				}
				if (d!=null){
					ret = outsdf.format(d);
				}
			}
		}else{
			logger.error(String.format("str type not supported. %s", dataType));
		}
		logger.debug(String.format("get value for %s of type:%s. ret:%s", v, dataType, ret));
		return ret;
	}
}
