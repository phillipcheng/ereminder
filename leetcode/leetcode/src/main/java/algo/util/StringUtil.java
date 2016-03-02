package algo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringUtil {
	private static Logger logger =  LogManager.getLogger(StringUtil.class);
	
	public static int diffCharNum(String a, String b){
		int count =0;
		for (int i=0; i<a.length(); i++){
			if (a.charAt(i) != b.charAt(i)){
				count++;
			}
		}
		return count;
	}
	/**
	 * compare the string except for the idx char 
	 * @param idx
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean maskedMatch(int idx, String a, String b){
		if (a.length()!=b.length()) return false;
		if (idx<0 || idx > a.length()) return false;
		String beforeA, afterA, beforeB, afterB;
		if (idx==0){
			beforeA = "";
			beforeB = "";
		}else{
			beforeA = a.substring(0, idx);
			beforeB = b.substring(0, idx);
		}
		
		if (idx == a.length()){
			afterA="";
			afterB="";
		}else{
			afterA = a.substring(idx+1, a.length());
			afterB = b.substring(idx+1, a.length());
		}
		
		return afterA.equals(afterB) && beforeA.equals(beforeB);
	}
	
	public static String[] readStrings(String fileName){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(StringUtil.class.getClassLoader()
                    .getResourceAsStream(fileName)));
			String input = br.readLine();
			String[] ret = input.replace("\"", "").split(",");
			br.close();
			return ret;
		}catch(Exception e){
			logger.error("",e);
			return null;
		}
	}
}
