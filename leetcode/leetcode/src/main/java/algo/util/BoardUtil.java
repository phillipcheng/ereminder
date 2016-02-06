package algo.util;

import java.util.Arrays;

public class BoardUtil {
	
	public static void printBoard(char[][] board){
		for (int i=0; i<board.length; i++){
			System.out.println(Arrays.toString(board[i]));
		}
	}

}
