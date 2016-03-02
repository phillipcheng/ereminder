package leet.algo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


//Given a 2D board and a word, find if the word exists in the grid.
public class WordSearch {
	private static Logger logger =  LogManager.getLogger(WordSearch.class);
	
	//
	public List<int[]> getLoc(char[][] board, char ch){
		List<int[]> locList = new ArrayList<int[]>();
		for (int i=0; i<board.length; i++){
			for (int j=0; j<board[i].length; j++){
				if (board[i][j]==ch){
					locList.add(new int[]{i,j});
				}
			}
		}
		return locList;
	}
	
	public List<int[]> checkNext(char[][] board, int[] loc, char ch){
		int x = loc[0];
		int y = loc[1];
		int xw = board.length;
		int yw = board[0].length;
		List<int[]> locList = new ArrayList<int[]>();
		if (x+1<xw && board[x+1][y]==ch){
			locList.add(new int[]{x+1, y});
		}
		if (x-1>=0 && board[x-1][y]==ch){
			locList.add(new int[]{x-1, y});
		}
		if (y+1<yw && board[x][y+1]==ch){
			locList.add(new int[]{x, y+1});
		}
		if (y-1>=0 && board[x][y-1]==ch){
			locList.add(new int[]{x, y-1});
		}
		return locList;
	}
	
	//idx: next match ch idx
	public boolean hasWord(char[][] board, boolean[][] history, int[] start, String word, int idx){
		history[start[0]][start[1]]=true;
		if (idx== word.length()) return true;
		if (idx== 0) return false;
		//logger.info(String.format("loc:%s, value:%c, idx:%d", Arrays.toString(loc), board[loc[0]][loc[1]], idx));
		List<int[]> nexts = checkNext(board, start, word.charAt(idx));
		if (nexts.size()>0){
			for (int[] next:nexts){
				if (!history[next[0]][next[1]]){
					if (hasWord(board, history, next, word, idx+1)){
						return true;
					}
				}
			}
		}
		history[start[0]][start[1]]=false;
		return false;
	}
	
	public boolean exist(char[][] board, String word) {
		if (word.length()==0)
			return false;
		int xw = board.length;
		int yw = board[0].length;
		List<int[]> initLocs = getLoc(board, word.charAt(0));
		for (int[] initLoc: initLocs){
			boolean[][] history = new boolean[xw][yw];
			if (hasWord(board, history, initLoc, word, 1)){
				return true;
			}
		}
        return false;
    }

}
