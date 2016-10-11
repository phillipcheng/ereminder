package leet.algo;

public class UniquePathsII {
	
	public int uniquePathsWithObstacles(int[][] obstacleGrid) {
		int n = obstacleGrid.length;
		if (n==0) return 0;
		int m = obstacleGrid[0].length;
		if (m==0) return 0;
        int[][] np = new int[n][m];
        for (int i=0; i<n; i++){
        	for(int j=0; j<m; j++){
        		if (obstacleGrid[i][j]==1){
        			np[i][j]=0;
        		}else{
	        		if (i==0 && j==0){
	        			np[i][j]=1;
	        		}else if (i==0){
	        			np[0][j]=np[0][j-1];
	        		}else if (j==0){
	        			np[i][0]=np[i-1][0];
	        		}else{
	        			np[i][j]=np[i-1][j]+np[i][j-1];
	        		}
        		}
        	}
        }
        return np[n-1][m-1];
    }
}
