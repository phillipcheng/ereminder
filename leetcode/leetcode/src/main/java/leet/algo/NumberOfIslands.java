package leet.algo;

import java.util.ArrayList;
import java.util.List;

public class NumberOfIslands {
	
	public void expand(char[][] grid, int i, int j, int n, int m, int[][] parents, int p){
		List<int[]> neighbors = new ArrayList<int[]>();
		if (i>0) neighbors.add(new int[]{i-1, j});
		if (j>0) neighbors.add(new int[]{i, j-1});
		if (i<n-1) neighbors.add(new int[]{i+1, j});
		if (j<m-1) neighbors.add(new int[]{i, j+1});
		parents[i][j]=p;
		for (int[] neigh:neighbors){
			if (grid[neigh[0]][neigh[1]]=='1' && parents[neigh[0]][neigh[1]]==0){
				expand(grid, neigh[0], neigh[1], n, m, parents, p);
			}
		}
	}
	
	public int numIslands(char[][] grid) {
		if (grid.length==0 || grid[0].length==0) return 0;
        int n = grid.length;
        int m = grid[0].length;
        int[][] parents = new int[n][m];
        int p=0;
        for (int i=0; i<n; i++){
        	for (int j=0; j<m; j++){
        		if (grid[i][j]=='1' && parents[i][j]==0){
        			p++;
        			expand(grid, i, j, n, m, parents, p);
        		}
        	}
        }
        return p;
    }
}
