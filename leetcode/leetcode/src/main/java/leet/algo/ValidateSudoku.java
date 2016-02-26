package leet.algo;

public class ValidateSudoku {
	
    public boolean isValidSudoku(char[][] board) {
        for (int i=0; i<9; i++){
        	for (int j=0; j<9; j++){
        		if (board[i][j]!='.'){
        			ToBeFill tbf = new ToBeFill(i,j);
        			tbf.v = board[i][j];
        			if (!ToBeFill.isFit(board, tbf))
        				return false;
        		}
        	}
        }
        return true;
    }
}
