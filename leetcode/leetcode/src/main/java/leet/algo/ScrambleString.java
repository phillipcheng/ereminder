package leet.algo;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.BoardUtil;


public class ScrambleString {
	private static Logger logger =  LogManager.getLogger(ScrambleString.class);
	private Set<String> multipleSet(Set<String> s1, Set<String> s2){
		Set<String> ret = new HashSet<String>();
		for (String ss1:s1){
			for (String ss2:s2){
				ret.add(ss1+ss2);
				ret.add(ss2+ss1);
			}
		}
		return ret;
	}
	public boolean isScramble(String s1, String s2) {
		int n = s1.length();
		if (n==0 && s2.length()==0) return true;
		Set<String>[][] A = new Set[n][n];
		for (int i=0; i<n; i++){
			Set<String> ss = new HashSet<String>();
			ss.add(s1.charAt(i)+"");
			A[i][i]=ss;
		}
		for (int i=0; i<n; i++){
			for (int j=i-1;j>=0; j--){
				Set<String> ss = new HashSet<String>();
				for (int k=j; k<i; k++){
					Set<String> set1 = A[j][k];
					Set<String> set2 = A[k+1][i];
					ss.addAll(multipleSet(set1,set2));
				}
				A[j][i]=ss;
				if (j==0 || i==n-1){
					String begin = s2.substring(j, i+1);
					String end = s2.substring(n-1-i, n-j);
					Set<String> sbe = new HashSet<String>();
					sbe.add(begin);
					sbe.add(end);
					ss.retainAll(sbe);
				}
			}
			logger.info(BoardUtil.getBoardString(A));
		}
        return A[0][n-1].contains(s2);
    }

}
