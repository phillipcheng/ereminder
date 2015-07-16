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
	
	public static List<String> colTableToCSV(List<String> vl, int colnum, boolean genHeader){
		return colTableToCSV(vl, colnum, genHeader, ColTableAsCSV.DATA_TYPE_NUMBER);
	}
	
	/**
	 * example
	 * k1 v1 v2 v3
	 * k2 v1 v2 v3
	 * 
	 * @param vl: column table to csv. a column is a row in the table
	 * @param colnum: how many columns in the page table: e.g. colnum = 3
	 * @return
	 */
	public static List<String> colTableToCSV(List<String> vl, int colnum, boolean genHeader, String dataType){
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
					if (ColTableAsCSV.DATA_TYPE_NUMBER.equals(dataType)){
						str = FRUtil.getFRNumber(str);
					}else if (ColTableAsCSV.DATA_TYPE_TEXT.equals(dataType)){
						//replace comma and new line for string
						str = str.replace(",", "\\,").replaceAll("\\r\\n|\\r|\\n", " ");
					}else{
						logger.error(String.format("str type not supported. %s", dataType));
					}
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
	
	/**
	 * 
	 * @param vl: row table to csv
	 * @param colnum
	 * @return
	 */
	public static List<String> rowTableToCSV(List<String> vl, int colnum, boolean hasHeader){
		List<String> retList = new ArrayList<String>();
		if (vl!=null){
			StringBuffer sb = null;
			for (int i=0; i<vl.size(); i++){
				int row = i / colnum;
				int coln = i % colnum;
				if (row==0 && hasHeader || row>0){
					if (coln == 0){
						if (sb!=null){
							retList.add(sb.toString());
						}
						sb = new StringBuffer();
					}
					if (coln>0){
						sb.append(",");
					}
					sb.append(vl.get(i));
				}
			}
			if (sb!=null){
				retList.add(sb.toString());
			}
		}
		
		return retList;
	}
}
