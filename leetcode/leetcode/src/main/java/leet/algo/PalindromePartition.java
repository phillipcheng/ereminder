package leet.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Given a string s, partition s such that every substring of the partition is a palindrome.
public class PalindromePartition {
	private static Logger logger =  LogManager.getLogger(PalindromePartition.class);
	
	public boolean[][]A;//A(i,j) = s(i,j) is palindrome or not, i<=j
	public Map<String, List<List<String>>> pmap = new HashMap<String, List<List<String>>>();
	
	
	public void fillA(String s){
		int n = s.length();
		A = new boolean[n][n];
		for (int j=0; j<n; j++){
			for (int i=j; i>=0; i--){
				if (i==j){
					A[i][j]=true;
				}else if (j==i+1){
					if (s.charAt(j)==s.charAt(i)){
						A[i][j]=true;
					}else{
						A[i][j]=false;
					}
				}else{
					if (s.charAt(i)==s.charAt(j) && A[i+1][j-1]){
						A[i][j]=true;
					}else{
						A[i][j]=false;
					}
				}
			}
		}
	}

	//start idx: 0 to length-1
	public List<List<String>> getP(String s, int start){
		List<List<String>> ret = null;
		if (pmap.containsKey(s.substring(start))){
			ret = pmap.get(s.substring(start));
		}else{
			ret = new ArrayList<List<String>>();
			if (start==s.length()-1){
				List<String> ls = new ArrayList<String>();
				ls.add(s.charAt(start)+"");
				ret.add(ls);
			}else{
				for (int i=start; i<s.length()-1; i++){
					if (A[start][i]){
						List<List<String>> p = getP(s, i+1);
						if (p.size()>0){
							for (List<String> l:p){
								List<String> ls = new ArrayList<String>();
								ls.addAll(l);
								ls.add(0, s.substring(start, i+1));
								ret.add(ls);
							}
						}
					}
				}
				//treat the last one
				if (A[start][s.length()-1]){
					List<String> ls = new ArrayList<String>();
					ls.add(s.substring(start));
					ret.add(ls);
				}
			}
			pmap.put(s.substring(start), ret);
			//logger.info(String.format("palindrome to put: s:%s with start:%d is %s", s, start, ret));
		}
		//logger.info(String.format("palindrome for s:%s with start:%d is %s", s, start, ret));
		return ret;
	}
	
	public List<List<String>> partition(String s) {
		fillA(s);
		return getP(s, 0);
    }

}
