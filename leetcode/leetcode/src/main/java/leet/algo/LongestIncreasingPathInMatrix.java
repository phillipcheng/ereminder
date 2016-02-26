package leet.algo;

//Given an integer matrix, find the length of the longest increasing path.
public class LongestIncreasingPathInMatrix {
	class Node{//-1 not visited, else the length of inc/dec path
		int inc=-1;
		int dec=-1;
	}
	
	public void dfs(int[][] matrix, Node[][] tree, int i, int j, int row, int col, boolean inc){
		//System.err.println(String.format("dfs row %d, col:%d, inc:%b", i, j, inc));
		int me = matrix[i][j];
		if (inc){
			if (tree[i][j].inc==-1){//1st time visit
				if (i+1<row && me<matrix[i+1][j]){//inc, down
					dfs(matrix,tree,i+1,j,row,col,true);
					if (tree[i+1][j].inc+1>tree[i][j].inc){
						tree[i][j].inc = tree[i+1][j].inc + 1;
					}
				}
				if (i-1>=0 && me<matrix[i-1][j]){//inc, up
					dfs(matrix,tree,i-1,j,row,col,true);
					if (tree[i-1][j].inc+1>tree[i][j].inc){
						tree[i][j].inc = tree[i-1][j].inc + 1;
					}
				}
				if (j+1<col && me<matrix[i][j+1]){//inc, right
					dfs(matrix,tree,i,j+1,row,col,true);
					if (tree[i][j+1].inc+1>tree[i][j].inc){
						tree[i][j].inc = tree[i][j+1].inc + 1;
					}
				}
				if (j-1>=0 && me<matrix[i][j-1]){//inc, left
					dfs(matrix,tree,i,j-1,row,col,true);
					if (tree[i][j-1].inc+1>tree[i][j].inc){
						tree[i][j].inc = tree[i][j-1].inc + 1;
					}
				}
				if (tree[i][j].inc==-1){
					tree[i][j].inc=0;
				}
			}
			
		}else{
			if (tree[i][j].dec==-1){
				if (i+1<row && me>matrix[i+1][j]){//dec, down
					dfs(matrix,tree,i+1,j,row,col,false);
					if (tree[i+1][j].dec+1>tree[i][j].dec){
						tree[i][j].dec = tree[i+1][j].dec + 1;
					}
				}
				if (i-1>=0 && me>matrix[i-1][j]){//dec, up
					dfs(matrix,tree,i-1,j,row,col,false);
					if (tree[i-1][j].dec+1>tree[i][j].dec){
						tree[i][j].dec = tree[i-1][j].dec + 1;
					}
				}
				if (j+1<col && me>matrix[i][j+1]){//dec, right
					dfs(matrix,tree,i,j+1,row,col,false);
					if (tree[i][j+1].dec+1>tree[i][j].dec){
						tree[i][j].dec = tree[i][j+1].dec + 1;
					}
				}
				if (j-1>=0 && me>matrix[i][j-1]){//dec, left
					dfs(matrix,tree,i,j-1,row,col,false);
					if (tree[i][j-1].dec+1>tree[i][j].dec){
						tree[i][j].dec = tree[i][j-1].dec + 1;
					}
				}
				if (tree[i][j].dec==-1){
					tree[i][j].dec=0;
				}
			}
		}
	}
	
	public void printTree(Node[][] tree){
		for (int i=0; i<tree.length; i++){
			StringBuffer sb = new StringBuffer();
			for (int j=0; j<tree[0].length; j++){
				sb.append(tree[i][j].inc).append(":").append(tree[i][j].dec).append(",");
			}
			System.err.println(sb);
		}
	}
	
	public int longestIncreasingPath(int[][] matrix) {
		int row = matrix.length;
		if (matrix.length<=0){
			return 0;
		}
		int col = matrix[0].length;
		Node[][] tree = new Node[row][col];
		for (int i=0; i<row; i++){
			for (int j=0; j<col; j++){
				tree[i][j] = new Node();
			}
		}
		printTree(tree);
		int maxInc=-1;
		int maxDec=-1;
		for (int i=0; i<row; i++){
			for (int j=0; j<col; j++){
				if (tree[i][j].inc == -1){
					dfs(matrix, tree, i, j, row, col, true);
					//printTree(tree);
					if (tree[i][j].inc>maxInc){
						maxInc=tree[i][j].inc;
					}
				}
				if (tree[i][j].dec == -1){
					dfs(matrix, tree, i, j, row, col, false);
					//printTree(tree);
					if (tree[i][j].dec>maxDec){
						maxDec=tree[i][j].dec;
					}
				}
			}
		}
        return Math.max(maxInc, maxDec)+1;
    }
}
