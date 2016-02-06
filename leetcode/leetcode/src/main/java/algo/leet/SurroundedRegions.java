package algo.leet;

import algo.util.UnionFind;

//Given a 2D board containing 'X' and 'O', capture all regions surrounded by 'X'.
public class SurroundedRegions {
	
	public void solveWithUF(char[][] board) {
		if (board.length<=0){
			return;
		}
		int x = board.length;
		int y = board[0].length;
		int count = 1 + x * y;
		//cell (i,j) is mapped to i*y+j, the outer space is mapped to 0
		
		UnionFind uf = new UnionFind(count);
		//cell marked with 'O' and connected with outer space will not be captured by 'X'
		for (int i=0; i<x; i++){
			for (int j=0; j<y; j++){
				if (i==0 || j==0 || i==x-1 || j==y-1){
					//the cell is at the border
					if (board[i][j]=='O'){//
						uf.union(0, i*y+j);
					}
				}
				//connect with neighbors, only upper and lefter, since we go from up to down and from left to right
				if (i>0 && board[i][j]=='O' && board[i-1][j]=='O'){
					uf.union(i*y+j, (i-1)*y+j);
				}
				
				if (j>0 && board[i][j]=='O' && board[i][j-1]=='O'){
					uf.union(i*y+j, i*y+j-1);
				}
			}
		}
		//all the 'O' which is not connected with the outer space is captured
		for (int i=0; i<x; i++){
			for (int j=0; j<y; j++){
				if (board[i][j]=='O' && !uf.isConnected(0, i*y+j)){
					board[i][j]='X';
				}
			}
		}
    }
	
	public void solve(char[][] board) {
		solveWithUF(board);
	}
}
