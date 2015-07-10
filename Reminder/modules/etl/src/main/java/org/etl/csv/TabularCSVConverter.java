package org.etl.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Sample input table
 报表日期	20150331	20141231	20140930	20140630	20140331	20131231	20130930	20130630	20130331	20121231	20120930	
单位	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	
一、经营活动产生的现金流量
客户贷款及垫款净减少额	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0		
向央行借款净增加额	39730000000	20405000000	230000000	107000000	0	486000000	438000000	222000000	115000000	65000000	60270000		
客户存款和同业存放款项净增加额	0	353322000000	216207000000	220481000000	101936000000	459886000000	421028000000	306050000000			
其中:客户存款	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0			
同业及其他金融机构存放款	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0		

 * Sample output table (skip: line 单位, skip subtitle rows: 一、经营活动产生的现金流量), transform date field
 id, date,  客户贷款及垫款净减少额, 向央行借款净增加额, 客户存款和同业存放款项净增加额, 其中:客户存款
 passin, 2015-03-31,        0,   39730000000,                        0,           0,
 passin, 2015-12-31,        0,   20405000000,             353322000000,           0,
 
 */

public class TabularCSVConverter {

	private static Logger logger =  LogManager.getLogger(TabularCSVConverter.class);
	
	public static final String FN_ID="id";
	public static final String FN_DATE="date";
	public static final String SEP=",";
	public static final String IN_DATE_FORMAT="yyyyMMdd";
	public static final String OUT_DATE_FOMRAT="yyyy-MM-dd";
	public static final SimpleDateFormat insdf = new SimpleDateFormat(IN_DATE_FORMAT);
	public static final SimpleDateFormat outsdf = new SimpleDateFormat(OUT_DATE_FOMRAT);
	
	public static void convert(String tid, BufferedReader is, BufferedWriter os, boolean outputTitle){
		try {
			//tid, date, list of all attributes
			int lineno=0;
			String line = null;
			String[] dates = null;
			List<String> fieldNames = new ArrayList<String>();
			fieldNames.add(FN_ID);
			fieldNames.add(FN_DATE);
			Map<String, String[]> keyValues = new HashMap<String, String[]>();
			int rowNum=0;
			while ((line = is.readLine())!=null){
				lineno++;
				if (lineno==1){
					dates = line.split("\\s+");
					rowNum = dates.length;
				}else if (lineno==2){
					//units
				}else{
					String[] content = line.split("\\s+");
					if (content.length==rowNum){
						String key = lineno + content[0];
						fieldNames.add(key);
						keyValues.put(key, content);
					}
				}
			}
			StringBuffer oneRow = new StringBuffer();
			if (outputTitle){
				//output title
				for (int j=0; j<fieldNames.size(); j++){
					if (j>1){
						String v = keyValues.get(fieldNames.get(j))[0];
						oneRow.append(v);
						if (j<fieldNames.size()-1)
							oneRow.append(SEP);
					}
				}
				oneRow.append(System.lineSeparator());
				os.write(oneRow.toString());
			}
			//output value
			for (int i=1; i<dates.length; i++){
				oneRow = new StringBuffer();
				oneRow.append(tid);
				oneRow.append(SEP);
				//change format from YYYYMMDD to YYYY-­MM-­DD
				String inDate = dates[i];
				try {
					String outDate = outsdf.format(insdf.parse(inDate));
					oneRow.append(outDate);
					oneRow.append(SEP);
				} catch (ParseException e) {
					logger.error("", e);
				}
				//other fields
				for (int j=0; j<fieldNames.size(); j++){
					if (j>1){
						String v = keyValues.get(fieldNames.get(j))[i];
						oneRow.append(v);
						if (j<fieldNames.size()-1)
							oneRow.append(SEP);
					}
				}
				oneRow.append(System.lineSeparator());
				os.write(oneRow.toString());
			}
		} catch (IOException e) {
			logger.error(e);
		}
		
	}

}
