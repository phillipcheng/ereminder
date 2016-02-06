package algo.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class StringUtil {
	
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
	
	//"[hot,dot,dog,lot,log]"
	public static HashSet<String> getHashSet(String s){
		HashSet<String> hs = new HashSet<String>();
		StringTokenizer st = new StringTokenizer(s, "[] ,",false);
		while (st.hasMoreTokens()){
			String str = st.nextToken();
			hs.add(str);
		}
		return hs;
	}
	
	//[1, 1+intern, 1+2*intern, 1+3*intern, ... , 1+(num-1)*intern]
	public static int[] genIntArray(int start, int intern, int num){
		int[] a = new int[num];
		for (int i=0; i<a.length; i++){
			a[i] = start + intern*i;
		}
		return a;
	}
	
	//[1+(num-1)*intern, ... , 1+3*intern, 1+2*intern, 1+intern, 1]
	public static int[] genReverseIntArray(int start, int intern, int num){
		int[] a = new int[num];
		for (int i=0; i<a.length; i++){
			a[i] = start + intern*(a.length-1-i);
		}
		return a;
	}
	
	//[1,2]
	public static int[] getArrayFromString(String s){
		StringTokenizer st = new StringTokenizer(s, "[ ],", false);
		ArrayList<Integer> al = new ArrayList<Integer>();
		while (st.hasMoreTokens()){
			al.add(Integer.parseInt(st.nextToken()));
		}
		int[] ret = new int[al.size()];
		for (int i=0; i<ret.length; i++){
			ret[i] = al.get(i);
		}
		return ret;
	}
	
	public static String genRandomString(int len){
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		String chars = "abcdefghijklmnopqrstuvwxyz";
		for (int i=0; i<len; i++){
			sb.append(chars.charAt(r.nextInt(chars.length())));
		}
		return sb.toString();
	}
	//[["AXA","EZE"],["EZE","AUA"],["ADL","JFK"]] and remove all "
	public static String[][] getStringArrayArray(String input){
		String trim = input.replaceAll("\\[", "").replaceAll("\\]","");
		StringTokenizer st = new StringTokenizer(trim, ",");
		int i=0;
		List<String[]> vsl = new ArrayList<String[]>();
		String[] vs = new String[2];
		while(st.hasMoreTokens()){
			if (i==2){
				vsl.add(vs);
				vs = new String[2];
				i=0;
			}
			String v = st.nextToken();
			vs[i]=v;
			i++;
		}
		vsl.add(vs);
		String[][] ret = new String[vsl.size()][];
		vsl.toArray(ret);
		return ret;
	}
}
