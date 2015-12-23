package org.cld.util;

public class CompareUtil {
	
	/**
	 * 
	 * @param s1
	 * @param s2
	 * @return true means same, false means different
	 */
	public static boolean compareString(String s1, String s2){
		if (s1==null && s2==null){
			return true;
		}else if (s1==null || s2== null){
			return false;
		}else{
			return s1.equals(s2);
		}
	}
	
	public static boolean ObjectDiffers(Object r1, Object r2){
		if (r1==null && r2!=null){
			return true;
		}else if (r1!=null && r2==null){
			return true;
		}else if (r1==null && r2==null){
			return false;
		}else{
			return !r1.equals(r2);
		}
	}

}
