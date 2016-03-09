package algo.util;

import java.util.Arrays;



public class BoardUtil {
	
	public static String getBoardString(char[][] board){
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (int i=0; i<board.length; i++){
			sb.append(Arrays.toString(board[i])).append("\n");
		}
		return sb.toString();
	}
	
	public static String getBoardString(int[][] board){
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (int i=0; i<board.length; i++){
			sb.append(Arrays.toString(board[i])).append("\n");
		}
		return sb.toString();
	}
	
	public static String getBoardString(boolean[][] board){
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (int i=0; i<board.length; i++){
			sb.append(Arrays.toString(board[i])).append("\n");
		}
		return sb.toString();
	}
	
	public static String getBoardString(Object[][] board){
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (int i=0; i<board.length; i++){
			sb.append(Arrays.toString(board[i])).append("\n");
		}
		return sb.toString();
	}
}
