package leet.algo;

//Given an integer matrix, find the length of the longest increasing path.
public class LongestIncreasingPathInMatrix {
	public int longestIncreasingPath(int[][] matrix) {
        int a= matrix.length;
        if (a==0) return 0;
        int b = matrix[0].length;//
        if (b==0) return 0;
        int[][] pl = new int[a][b];
        for (int i=0; i<a;i++){
        	for (int j=0; j<b; j++){
        		pl[i][j]=-1;
        	}
        }
        for (int i=0; i<a;i++){
        	for (int j=0; j<b; j++){
        		dfs(pl, matrix, i, j);
        	}
        }
        int max=0;
        for (int i=0; i<a;i++){
        	for (int j=0; j<b; j++){
        		if (max<pl[i][j]){
        			max=pl[i][j];
        		}
        	}
        }
        return max;
    }
	public void dfs(int[][] pl, int[][] matrix, int i, int j){
		int a = matrix.length-1;
		int b = matrix[0].length-1;
		int max=1;
		if (i<a && matrix[i][j]<matrix[i+1][j]){//i+1,j
			if (pl[i+1][j]==-1) dfs(pl, matrix, i+1, j);
			int c = pl[i+1][j]+1;
			if (c>max) max=c;
		}
		if (i>0 && matrix[i][j]<matrix[i-1][j]){//i-1,j
			if (pl[i-1][j]==-1) dfs(pl, matrix, i-1, j);
			int c = pl[i-1][j]+1;
			if (c>max) max=c;
		}
		if (j<b && matrix[i][j]<matrix[i][j+1]){//i,j+1
			if (pl[i][j+1]==-1) dfs(pl, matrix, i, j+1);
			int c = pl[i][j+1]+1;
			if (c>max) max=c;
		}
		if (j>0 && matrix[i][j]<matrix[i][j-1]){//i,j-1
			if (pl[i][j-1]==-1) dfs(pl, matrix, i, j-1);
			int c = pl[i][j-1]+1;
			if (c>max) max=c;
		}
		pl[i][j]=max;
	}
}
