package org.cld.stock.sina;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FRUtil {
	private static Logger logger =  LogManager.getLogger(FRUtil.class);
	
	public static final Map<String, Integer> unitMap = new HashMap<String, Integer>();
	static {
		unitMap.put("元", 1);
		unitMap.put("千元", 1000);
		unitMap.put("万元", 10000);
		unitMap.put("百万", 1000000);
		unitMap.put("万股", 10000);
	}
	public static final String[] units = new String[]{"万股","百万","万元","千元","元"};
	
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
				try{
					double l = Double.parseDouble(str);
					l = l * unitMap.get(unit);
					str = Double.toString(l);
				}catch(Exception e){
					logger.error("",e);
				}
				break;
			}
		}
		return str;
	}
	
	/**
	 * 
	 * @param vl: rows by rows
	 * @param colnum
	 * @return
	 */
	public static List<String> tableRowToCSV(List<String> vl, int colnum, boolean genHeader){
		List<String> retList = new ArrayList<String>();
		if (vl!=null){
			for (int i=0; i<colnum; i++){
				StringBuffer sb = new StringBuffer();
				int j=i;
				if (i==0 && !genHeader){
					continue;
				}
				while (j<vl.size()){
					String str = vl.get(j);
					str = FRUtil.getFRNumber(str);
					if (j>i){
						sb.append(",");
					}
					sb.append(str);
					
					j+=colnum;
				}
				retList.add(sb.toString());
			}
		}
		return retList;
	}
}
