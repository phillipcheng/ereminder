package leet.algo;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Given a string S, you are allowed to convert it to a palindrome by adding characters in front of it. 
//Find and return the shortest palindrome you can find by performing this transformation.
public class ShortestPalindrome {
	private static Logger logger =  LogManager.getLogger(ShortestPalindrome.class);
	boolean[][] dynTable;
	boolean[] isP; //isP[i] : whether s[0,i] is palindrome
	
	public boolean isPRecur(int i, int j, String s){
		if (i==j) return true;
		if (i==j-1) return (s.charAt(i)==s.charAt(j));
		return isPRecur(i+1, j-1, s) && (s.charAt(i)==s.charAt(j));
	}
	
	public boolean isP(int i, String s){
		if (i==0) return true;
		if (i==1) return (s.charAt(0)==s.charAt(1));
		int il,ir;//init left and init right
		boolean v;
		if (i%2==0){
			v=true;
			il = i/2;
			ir = i/2;
		}else{
			il = i/2;
			ir = i/2+1;
			v = s.charAt(il)==s.charAt(ir);
		}
		int inc=0;
		while(ir+inc<i){
			inc++;
			v = v && (s.charAt(il-inc)==s.charAt(ir+inc));
		}
		return v;
	}
	
	public void buildArray(String s){
		int n = s.length();
		isP = new boolean[n];
		for (int i=0; i<n; i++){
			isP[i] = isP(i, s);
		}
		//logger.info(Arrays.toString(isP));
	}
	
	public void buildDynTable(String s){
		int n = s.length();
		dynTable = new boolean[n][n];//whether string[i,j] is palindrome
		for (int i=0; i<n; i++){
			dynTable[i][i]=true;
		}
		for (int i=0; i<n; i++){
			for (int j=i-1; j>=0; j--){
				if (i-j==1){
					dynTable[j][i]=(s.charAt(i)==s.charAt(j));
				}else{
					dynTable[j][i]=(s.charAt(i)==s.charAt(j)) && dynTable[j+1][i-1];
				}
			}
		}
	}
	
	public String shortestPalindrome(String s) {
		buildArray(s);
		int i=0;
		StringBuffer sb = new StringBuffer();
		for (i=s.length()-1; i>=0; i--){
			if (isP[i]){
				break;
			}else{
				sb.append(s.charAt(i));
			}
		}
		return sb.toString()+s;
    }
}
