package leet.algo;

public class UniquePaths {
	
	public int uniquePaths(int m, int n) {
        int[][] np = new int[m][n];
        for (int i=0; i<m; i++){
        	for (int j=0; j<n; j++){
        		if (i==0) np[i][j]=1;
        		else if (j==0) np[i][j]=1;
        		else{
        			np[i][j]=np[i-1][j]+np[i][j-1];
        		}
        	}
        }
        return np[m-1][n-1];
    }

}
