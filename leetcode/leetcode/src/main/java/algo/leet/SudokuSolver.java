package algo.leet;

import java.util.ArrayList;


public class SudokuSolver {
	public static boolean isDebug=false;
	
	ArrayList<ToBeFill> tbFillList = new ArrayList<ToBeFill>();
	
	public void printBoard(char[][] board, char[][] originBoard){
		for (int i=0; i<9; i++){
			for (int j=0; j<9; j++ ){
				if (board[i][j] == originBoard[i][j]){
					if (board[i][j]!='.'){
						System.out.print("-" + board[i][j] + ",");
					}else{
						System.out.print(" " + '.' + ",");
					}
				}else{
					System.out.print("+" + board[i][j] + ",");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void solveSudoku(char[][] board) {
		char[][] originBoard = new char[9][9];
		for (int i=0; i<board.length; i++){
			for (int j=0; j<board[i].length; j++){
				originBoard[i][j]=board[i][j];
			}
		}
		
		for (int i=0; i<board.length; i++){
			for (int j=0; j<board[i].length; j++){
				if (board[i][j]=='.'){
					tbFillList.add(new ToBeFill(i,j));
				}
			}
		}
		
		int i=0;
		while (i<tbFillList.size() && i>=0){
			ToBeFill tbf = tbFillList.get(i);
			char c=board[tbf.x][tbf.y];
			char start='0';
			if (c!='.'){
				//returned point, starting from current value
				start = (char) (c+1);
			}else{
				start = '1';
			}
			for (c=start; c<='9'; c++){
				tbf.v=c;
				if (ToBeFill.isFit(board, tbf)){
					board[tbf.x][tbf.y]=c;
					i++;
					if (isDebug){
						printBoard(board, originBoard);
						System.out.println("next i:" + i);
					}
					break;
				}
			}
			if (c>'9'){
				//no fit found for this tbf, go 1 back, clean this point
				if (board[tbf.x][tbf.y]!='.')
					board[tbf.x][tbf.y]='.';
				i--;
				if (isDebug){
					System.out.println("next i:" + i);
				}
			}
		}
		
		if (i<0){
			System.out.println("not able to solve");
		}else{
			if (isDebug)
				printBoard(board, originBoard);
		}
	}

}
