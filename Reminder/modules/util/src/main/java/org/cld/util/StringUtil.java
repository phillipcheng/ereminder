package org.cld.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringUtil {
	
	public static final char RMB_1='¥';
	public static final String RMB_2="￥"; //CH_MONEY_SIGN
	public static final String PRICE_SPE=",";
	
	public static final char KeyValue_Sep1='：'; //CH_colon
	public static final char KeyValue_Sep2=':'; //En_colon
	
	public static final char FullStop_1='。';

	public static Logger logger = LogManager.getLogger(StringUtil.class);
	
	public static String sepp = "-"; 
	//parse a 1-4-1 into [1,2,3,4]
	public static float[] parseSteps(String steps){
		if (!steps.contains(sepp)){
			return new float[]{Float.parseFloat(steps)};
		}else{
			String[] a = steps.split(sepp);
			float start = Float.parseFloat(a[0]);
			float step = Float.parseFloat(a[2]);
			float end = Float.parseFloat(a[1]);
			List<Float> fl = new ArrayList<Float>();
			float v = start;
			while(v<=end){
				fl.add(v);
				v += step;
			}
			float[] vf = new float[fl.size()];
			for (int i=0; i<vf.length; i++){
				vf[i] = fl.get(i);
			}
			return vf;
		}
	}
	public static Map<String, String> parseMapParams(String params){
		Map<String, String> paramsMap = new HashMap<String, String>();
		if (params==null){
			return paramsMap;
		}
		String[] strParams = params.split(",");
		for (String strParam:strParams){
			String[] kv = strParam.split(":");
			if (kv.length<2){
				logger.error(String.format("wrong param format: %s", params));
			}else{
				paramsMap.put(kv[0], kv[1]);
			}
		}
		return paramsMap;
	}
	
	public static int getIntegerFrom(String str){
		Scanner in = new Scanner(str).useDelimiter("[^0-9]+");
		return in.nextInt();
	}
	/*
	 * ¥74.50
	 * ￥ 157.80
	 * ￥ 1,157.80
	 * 
	 */
	public static double getRMBValue(String orgString){
		String stripSep = orgString.replaceAll(PRICE_SPE, "");
		String withNoSign = null;
		if (stripSep.indexOf(RMB_1) != -1){
			withNoSign = stripSep.substring(stripSep.indexOf(RMB_1) + 1);
			return Double.parseDouble(withNoSign);
		}else if (stripSep.indexOf(RMB_2) != -1){
			withNoSign = stripSep.substring(stripSep.indexOf(RMB_2) + 1);
			return Double.parseDouble(withNoSign);
		}else{
			logger.error("price symbal not found is:" + orgString);
			return -1;
		}
	}
	
	/*
	 * ISBN: 9787807292807
	 * I S B N：9787534451249
	 */
	public static String getValue(String kv){
		if (kv.indexOf(KeyValue_Sep1)!=-1){
			return kv.substring(kv.indexOf(KeyValue_Sep1) + 1).trim();
		}else if (kv.indexOf(KeyValue_Sep2)!=-1){
			return kv.substring(kv.indexOf(KeyValue_Sep2) + 1).trim();
		}else{
			logger.error("key value seperator not found in:" + kv);
			return kv;
		}
	}
	
	public static String getFirstSentence(String str){
		if (str.indexOf(FullStop_1)!=-1){
			return str.substring(0, str.indexOf(FullStop_1));
		}else{
			return str;
		}
	}
	
	public static String getLastStringBetween(String v, String pre, String post){
		int beginIndex = v.lastIndexOf(pre);
		int endIndex = v.indexOf(post, beginIndex);
		if (endIndex != -1 && beginIndex != -1)
			return v.substring(beginIndex + pre.length(), endIndex);
		else
			return v;
	}
	
	public static boolean isEmpty(String str){
		if (str==null || "".equals(str)){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean nullEquals(Object rhsValue, Object lhsValue){
		if (rhsValue==null && lhsValue==null){
			return true;
		}else if (rhsValue==null){
			return false;
		}else if (lhsValue==null){
			return false;
		}else{
			boolean ret = rhsValue.equals(lhsValue);
			if (ret==false){
				logger.debug(String.format("not equals: \n%s\n%s", rhsValue, lhsValue));
			}
			return rhsValue.equals(lhsValue);
		}
	}
	
	public static boolean urlEqual(String rhsValue, String lhsValue){
		if (isEmpty(rhsValue) && isEmpty(lhsValue)){
			return true;
		}else if (isEmpty(rhsValue)){
			return false;
		}else if (isEmpty(lhsValue)){
			return false;
		}else{
			//remove the trailing #
			int idx;
			if ((idx=rhsValue.lastIndexOf("#"))!=-1){
				rhsValue = rhsValue.substring(0, idx);
			}
			if ((idx=lhsValue.lastIndexOf("#"))!=-1){
				lhsValue = lhsValue.substring(0, idx);
			}
			return rhsValue.equals(lhsValue);
		}
	}
	
	//001
	public static String getStringFromNum(int number, int minDigits){
		String strNum = Integer.toString(number);
		String retNum = "";
		if (strNum.length()<minDigits){
			for (int i=0; i< minDigits-strNum.length(); i++){
				retNum = retNum + "0";
			}
			retNum = retNum + strNum;
			return retNum;
		}else{
			return strNum;
		}
	}
	
	//remove ",", " "
	public static int getNumber(String str){
		String s = str.replace(",", "");
		s = s.replace(" ", "");
		return Integer.parseInt(s);
	}
	
	public static String getStringBetweenFirstPreFirstPost(String v, String pre, String post){
		int beginIndex=0;
		int endIndex = v.length();
		if (pre!=null && !"".equals(pre)){
			beginIndex = v.indexOf(pre);
		}
		if (post!=null && !"".equals(post)){
			endIndex = v.indexOf(post, beginIndex);
		}
		if (endIndex != -1 && beginIndex != -1){
			if (pre==null){
				return v.substring(0, endIndex);
			}else{
				return v.substring(beginIndex + pre.length(), endIndex);
			}
		}
		else
			return v;
	}
	
	public static String getStringBetweenFirstPreLastPost(String v, String pre, String post){
		int beginIndex = v.indexOf(pre);
		int endIndex = v.lastIndexOf(post);
		if (endIndex != -1 && beginIndex != -1)
			return v.substring(beginIndex + pre.length(), endIndex);
		else
			return v;
	}
	
	public static String getStringBetweenLastPreFirstPost(String v, String pre, String post){
		int beginIndex = v.lastIndexOf(pre);
		int endIndex = v.indexOf(post, beginIndex);
		if (endIndex != -1 && beginIndex != -1)
			return v.substring(beginIndex + pre.length(), endIndex);
		else
			return v;
	}
	
	public static String getStringBetweenLastPreLastPost(String v, String pre, String post){
		int beginIndex = v.lastIndexOf(pre);
		int endIndex = v.lastIndexOf(post);
		if (endIndex != -1 && beginIndex != -1)
			return v.substring(beginIndex + pre.length(), endIndex);
		else
			return v;
	}
	
	public static List<String> fromStringList(String rids){
		StringTokenizer st = new StringTokenizer(rids, ",");
		List<String> ridList = new ArrayList<String>();
		while (st.hasMoreTokens()){
			ridList.add(st.nextToken());
		}
		return ridList;
	}
	
	public static String toStringList(String[] list){
		String ret = "";
		for (int i=0; i<list.length; i++){
			if (i<(list.length-1)){
				ret = ret + list[i]+",";
			}else{
				ret = ret + list[i];
			}
		}
		return ret;
	}
	
	public static String getCommomSuffix(String[] strings){
		String[] reverseUrls = new String[strings.length];
		for (int i=0; i<strings.length; i++){
			String url = strings[i];
			reverseUrls[i] = new StringBuffer(url).reverse().toString();
		}
		String cStr = StringUtils.getCommonPrefix(reverseUrls);
		return new StringBuffer(cStr).reverse().toString();
	}
	
	public static String[] getNotNullStringArray(String[] strArray){
		List<String> strList = new ArrayList<String>();
		for (int i=0; i<strArray.length; i++){
			if (strArray[i]!=null && !"".equals(strArray[i])){
				strList.add(strArray[i]);
			}
		}
		String[] strResAry = new String[strList.size()];
		return strList.toArray(strResAry);
	}
	
	//replace "[param]" with value from params map, only support type string, for list type, should call this multiple times
	public static String fillParams(String input, Map<String, Object> params, 
			String prefix, String postfix){//TODO slow
		if (input.contains(prefix) && input.contains(postfix) && params!=null){
			for (String key: params.keySet()){
				Object value = params.get(key);
				String strValue = "null";
				if (value != null){
					strValue = value.toString();
				}
				input = input.replace(prefix + key + postfix, strValue);
			}
		}
		return input;
	}
	
	public static String escapeFileName(String name){
		return name.replaceAll("\\W+", "_");
	}
}
