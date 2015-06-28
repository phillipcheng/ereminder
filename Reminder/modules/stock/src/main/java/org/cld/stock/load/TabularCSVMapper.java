package org.cld.stock.load;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 报表日期	20150331	20141231	20140930	20140630	20140331	20131231	20130930	20130630	20130331	20121231	20120930	20120630	20120331	20111231	20110930	20110630	20110331	20101231	20100930	20100630	20100331	20091231	20090930	20090630	20090331	20081231	20080930	20080630	20080331	20071231	20070930	20070630	20070331	20061231	20060930	20060630	20060331	20051231	20050630	20041231	19700101	
单位	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元	元		
一、经营活动产生的现金流量
客户贷款及垫款净减少额	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	
向央行借款净增加额	39730000000	20405000000	230000000	107000000	0	486000000	438000000	222000000	115000000	65000000	60270000	60270000	0	0	0	0	188902184	2000000	-3074728	48295021.13	-48000000	48000000	0	0	0	-10000000	-10000000	-10000000	-10000000	10000000	103494299.3	108892419.48	0	0	0	0	0	0	0	0	0	
客户存款和同业存放款项净增加额	0	353322000000	216207000000	220481000000	101936000000	459886000000	421028000000	306050000000	228021000000	388966000000	359112463000	293609943000	100371519000	313685039000	207466004370.63998	160641104000	37609688303.96	475220654634.2	301507192886.7	143567853741.69	19449219388.620003	331543365926.56	293105679220.37	322490651204.83	195739994809.77002	345180181134.15	163026459144	67277997422.409996	37659509162.59	197210672222.81998	141051207408.05002	61389979060.38999	10833403635.92	0	53290237019.53999	37821749592.79001	12997277889.45	0	29548180674.07	68239895836.55	0	
其中:客户存款	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	141051207408.05002	61389979060.38999	9735691709	0	52221151508.409996	38096007477.19	11907509442.439999	0	27633018680.61	67063171290.59	0	
同业及其他金融机构存放款	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	1097711926.92	0	1069085511.13	-274257884.4	1089768447.01	0	1915161993.46	1176724545.96	0	
收回存放同业及其他金融机构净额	8803000000	0	0	0	9320000000	17481000000	25263000000	35490000000	30572000000	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	
拆入资金现金流入	45837000000	105400000000	40177000000	0	0	39934000000	0	0	0	7895000000	0	0	54173902000	143674666000	112393698745.96	0	77784068044.29	-312356596355.18994	-224981117628.21002	-95697866384.36	-36328223004.51	101611194963.4	32797284272.760002	20716667468.350002	50347473812.92	-74337386151.76001	2420681336.11	36882127267.58	1319504339.57	-67106408388.31	-12154336605.66	6286841218.68	464945588	0	-207643200	1043269945	88000100	0	-983029000	-1048862560	0	
收回的拆出资金净额	0	0	0	0	0	0	7148000000	33370000000	0	0	0	0	0	0	0	131365222000	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	
吸收的卖出回购项净额	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	3685058929.9100003	0	792337632.57	688900103.91	1604958754.34	0	-2269998664.9500003	-973273653.26	0	
收回的买入返售项净额	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	
 */

public class TabularCSVMapper implements ContentMapper{

	private static Logger logger =  LogManager.getLogger(TabularCSVMapper.class);
	
	public static final String FN_STOCKID="stockid";
	public static final String FN_DATE="date";
	public static final String SEP=",";
	
	public void convert(String tid, InputStreamReader is, OutputStreamWriter os){
		try {
			//tid, date, list of all attributes
			BufferedReader bir = new BufferedReader(is);
			BufferedWriter bow = new BufferedWriter(os);
			int lineno=0;
			String line = null;
			String[] dates = null;
			List<String> fieldNames = new ArrayList<String>();
			fieldNames.add(FN_STOCKID);
			fieldNames.add(FN_DATE);
			Map<String, String[]> keyValues = new HashMap<String, String[]>();
			int rowNum=0;
			while ((line = bir.readLine())!=null){
				lineno++;
				if (lineno==1){
					dates = line.split("\\s+");
					rowNum = dates.length;
				}else if (lineno==2){
					//units
				}else{
					String[] content = line.split("\\s+");
					if (content.length==rowNum){
						fieldNames.add(content[0]);
						keyValues.put(content[0], content);
					}
				}
			}
			//output
			for (int i=1; i<dates.length; i++){
				StringBuffer oneRow = new StringBuffer();
				oneRow.append(tid);
				oneRow.append(SEP);
				oneRow.append(dates[i]);
				oneRow.append(SEP);
				for (int j=0; j<fieldNames.size(); j++){
					if (j>1){
						String v = keyValues.get(fieldNames.get(j))[i];
						oneRow.append(v);
						if (j<fieldNames.size()-1)
							oneRow.append(SEP);
					}
				}
				oneRow.append(System.lineSeparator());
				bow.write(oneRow.toString());
			}
		} catch (IOException e) {
			logger.error(e);
		}
		
	}

}
