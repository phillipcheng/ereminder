package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Given a string s, partition s such that every substring of the partition is a palindrome.

//Return the minimum cuts needed for a palindrome partitioning of s.
public class PalindromePartitionII {
	
private static Logger logger =  LogManager.getLogger(PalindromePartitionII.class);
	
	public boolean[][]A;//A(i,j) = s(i,j) is palindrome or not, i<=j
	
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

	public int minCut(String s) {
		fillA(s);
		int[] cut = new int[s.length()];
		for (int i=0; i<cut.length; i++){
			int min = Integer.MAX_VALUE;
			for (int j=0; j<=i; j++){
				if (A[j][i]){
					int x=0;
					if (j==0){
						x = 0;
					}else{
						x = cut[j-1]+1;
					}
					if (x<min){
						min=x;
					}
				}
			}
			cut[i]=min;
		}
		return cut[cut.length-1];
    }
	
	////
	public int[][]B;//B(i,j) minCut number for s(i,j)
	public void fillB(String s){
		int n = s.length();
		B = new int[n][n];
		for (int i=0; i<n; i++){
			B[i][i]=1;
		}
		for (int j=1; j<=n-1; j++){
			for (int i=j-1; i>=0; i--){
				int min = Integer.MAX_VALUE;
				for (int k=i; k<=j-1; k++){
					int v = B[i][k] + B[k+1][j];
					if (v<min){
						min = v;
					}
				}
				if (A[i][j]){
					min = 1;
				}
				B[i][j]=min;
			}
		}
	}
	
	public int minCutSlow(String s) {
		fillA(s);
		fillB(s);
		return B[0][s.length()-1]-1;
    }

}
