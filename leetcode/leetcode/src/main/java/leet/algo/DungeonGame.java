package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.BoardUtil;


public class DungeonGame {
	private static Logger logger =  LogManager.getLogger(DungeonGame.class);
	
	public int calculateMinimumHP(int[][] dungeon) {
		//logger.info(BoardUtil.getBoardString(dungeon));
		int n = dungeon.length;
		int m = dungeon[0].length;
		int[][] A = new int[n][m];
		for (int i=0; i<n; i++){
			for (int j=0; j<m; j++){
				A[i][j]=Integer.MAX_VALUE;
			}
		}
		A[n-1][m-1]=Math.max(-1 * dungeon[n-1][m-1],0);
		for (int i=n-1; i>=0; i--){
			for (int j=m-1; j>=0; j--){
				if (j-1>=0){//set left
					A[i][j-1] = Math.max(Math.min(A[i][j-1], A[i][j] - dungeon[i][j-1]),0);
				}
				if (i-1>=0){//set upper
					A[i-1][j] = Math.max(Math.min(A[i-1][j], A[i][j] - dungeon[i-1][j]),0);
				}
				//logger.info(BoardUtil.getBoardString(A));
			}
		}
        return Math.max(A[0][0]+1,1);
    }

}
