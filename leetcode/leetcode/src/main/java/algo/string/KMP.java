package algo.string;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KMP {
	private static Logger logger =  LogManager.getLogger(KMP.class);
	public static int[] failureFunction(String s){
		int n = s.length();
		int[] f = new int[n];//length of the longest suffix that is a prefix
		int x=0;
		int k=1;
		f[0]=0;
		while (k<n){
			if (s.charAt(x)==s.charAt(k)){
				f[k]=x+1;//
				k++;
				x++;
			}else{
				if (x==0){
					f[k]=0;
					k++;
				}else{
					x=f[x-1];
				}
			}
		}
		return f;
	}
	
	public static List<Integer> search(String s, String p){
		int n = s.length();
		int m = p.length();
		int i = 0;
		int j = 0;
		int[] ff = failureFunction(p);
		List<Integer> ret = new ArrayList<Integer>();
		while (i<n){
			if (s.charAt(i) == p.charAt(j)){
				if (j==m-1){
					ret.add(i+1-m);
					i++;
					j=0;
				}else{
					i++;
					j++;
				}
			}else{
				if (j==0){
					i++;
				}else{
					j = ff[j-1];
				}
			}
			//logger.info(String.format("i:%d, j:%d", i, j));
		}
		return ret;
	}

}
