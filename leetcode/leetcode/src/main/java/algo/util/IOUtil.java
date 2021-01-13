package algo.util;

import java.util.*;

public class IOUtil {
	
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
	
	//input: [[AXA,EZE],[EZE,AUA],[ADL,JFK]]
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

	public static List<List<String>> getStringListList(String input){
		List<List<String>> output = new ArrayList<>();
		input = input.replaceAll("\"", "");
		String[][] o = getStringArrayArray(input);
		for (int i=0; i<o.length; i++){
			String[] arr = o[i];
			output.add(Arrays.asList(arr));
		}
		return output;
	}
	
	//input: [[3,4,5],[3,2,6],[2,2,1]]
	public static int[][] getIntArrayArray(String input){
		if (input.equals("[]")){
			return new int[0][0];
		}
		String trim = input.replaceAll("\\[\\[", "").replaceAll("\\]\\]","");
		String st[] = trim.split("\\],\\[");
		List<int[]> vsl = new ArrayList<int[]>();
		for (String ints:st){
			StringTokenizer st1 = new StringTokenizer(ints, ",");
			List<Integer> l1 = new ArrayList<Integer>();
			while (st1.hasMoreTokens()){
				l1.add(Integer.parseInt(st1.nextToken()));
			}
			int[] la1 = new int[l1.size()];
			for (int i=0; i<la1.length; i++){
				la1[i]=l1.get(i);
			}
			vsl.add(la1);
		}
		int[][] ret = new int[vsl.size()][];
		vsl.toArray(ret);
		return ret;
	}

	public static List<List<Integer>> getIntListList(String input){
		List<List<Integer>> output = new ArrayList<>();
		int[][] o = getIntArrayArray(input);
		for (int i=0; i<o.length; i++){
			int[] arr = o[i];
			List<Integer> li = new ArrayList<>();
			for (int n:arr){
				li.add(n);
			}
			output.add(li);
		}
		return output;
	}

	public static boolean equals(double[] a1, double[] a2){
		if (a1.length!=a2.length) return false;
		for (int i=0; i<a1.length; i++){
			if (Math.abs(a1[i]-a2[i])>0.0001) return false;
		}
		return true;
	}
}
