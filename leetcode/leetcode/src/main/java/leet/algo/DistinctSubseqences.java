package leet.algo;

public class DistinctSubseqences {
	
	public int numDistinct(String s, String t) {
		int n = s.length();
		int m = t.length();
		int[][] A = new int[n+1][m+1];
		for (int i=0; i<n+1; i++){
			A[i][m] = 0;
		}
		for (int j=0; j<m+1; j++){
			A[n][j] = 0;
		}
		for (int i=n-1; i>=0; i--){
			for (int j=m-1; j>=0; j--){
				if (s.charAt(i)==t.charAt(j)){
					if (j==t.length()-1){
						A[i][j] = A[i+1][j] + 1;
					}else{
						A[i][j] = A[i+1][j+1] + A[i+1][j];
					}
				}else{
					A[i][j] = A[i+1][j];
				}
			}
		}
        return A[0][0];
    }
}
