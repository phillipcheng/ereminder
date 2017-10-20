package leet.algo;

public class LongestPalindromicSubsequence {
	
	public int max(int a, int b){
		if (a>b) return a; else return b;
	}
	
	public int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] lps = new int[n][n];
        for (int i=0; i<n; i++){
        	for (int j=i; j>=0; j--){
        		if (i==j){
        			lps[i][j]=1;
        		}else if (i==j+1){
        			if (s.charAt(i)==s.charAt(j)){
        				lps[i][j]=2;
        			}else{
        				lps[i][j]=1;
        			}
        		}else{
        			if (s.charAt(i)==s.charAt(j)){
        				lps[i][j]=max(max(lps[i-1][j+1]+2, lps[i-1][j]), lps[i][j+1]);
        			}else{
        				lps[i][j]=max(lps[i-1][j], lps[i][j+1]);
        			}
        		}
        	}
        }
        for (int i=0; i<n; i++){
        	for (int j=0; j<n; j++){
        		System.out.print(lps[i][j] + ",");
        	}
        	System.out.println();
        }
        return lps[n-1][0];
    }
	
	public static void main(String[] args){
		LongestPalindromicSubsequence lps = new LongestPalindromicSubsequence();
		int ret;
		ret= lps.longestPalindromeSubseq("bbbba");System.out.println(ret);//4
		ret= lps.longestPalindromeSubseq("ab");System.out.println(ret);//4
	}
	
}
