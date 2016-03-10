package leet.algo;

public class EditDistance {
	
	public int minDistance(String word1, String word2) {
        int n = word1.length();
        int m = word2.length();
        if (n==0) return m;
        if (m==0) return n;
        int[][] A = new int[n+1][m+1];
        for (int i=1; i<n+1; i++){
        	A[i][0]=i;
        }
        for (int j=1; j<m+1; j++){
        	A[0][j]=j;
        }
        for (int i=1; i<=n; i++){
        	for (int j=1; j<=m; j++){
        		if (word1.charAt(i-1) == word2.charAt(j-1)){
        			A[i][j] = A[i-1][j-1];
        		}else{
        			A[i][j] = Math.min(Math.min(A[i-1][j-1], A[i-1][j]), A[i][j-1])+1;
        		}
        	}
        }
        return A[n][m];
    }
}
