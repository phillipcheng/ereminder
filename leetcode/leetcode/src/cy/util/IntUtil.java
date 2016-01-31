package cy.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class IntUtil {

	//[-3, -1, 0, 4]
	public static ArrayList<Integer> getIntArrayListFromString(String str){
		int[] a = StringUtil.getArrayFromString(str);
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (int i=0; i<a.length; i++){
			al.add(a[i]);
		}
		return al;
	}

}
