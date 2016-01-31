package cy.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import cy.SudokuSolver;

public class TestSudokuSolver {

	@Test
	public void test1() {
		SudokuSolver ss = new SudokuSolver();
		char[][] b = new char[9][9];
		char[] a;
		a= new char[]{'5','3','4','6','7','8','9','1','2'};
		b[0]=a;
		a= new char[]{'6','7','2','1','9','5','3','4','8'};
		b[1]=a;
		a= new char[]{'1','9','8','3','4','2','5','6','7'};
		b[2]=a;
		a= new char[]{'8','5','9','7','6','1','4','2','3'};
		b[3]=a;
		a= new char[]{'4','2','6','8','5','3','7','9','1'};
		b[4]=a;
		a= new char[]{'7','1','3','9','2','4','8','5','6'};
		b[5]=a;
		a= new char[]{'9','6','1','5','3','7','2','8','4'};
		b[6]=a;
		a= new char[]{'2','8','7','4','1','9','6','3','5'};
		b[7]=a;
		a= new char[]{'3','4','5','2','8','6','1','7','9'};
		b[8]=a;
		
		char[][] c = new char[9][9];
		a= new char[]{'5','3','.','.','7','.','.','.','.'};
		c[0]=a;
		a= new char[]{'6','.','.','1','9','5','.','.','.'};
		c[1]=a;
		a= new char[]{'.','9','8','.','.','.','.','6','.'};
		c[2]=a;
		a= new char[]{'8','.','.','.','6','.','.','.','3'};
		c[3]=a;
		a= new char[]{'4','.','.','8','.','3','.','.','1'};
		c[4]=a;
		a= new char[]{'7','.','.','.','2','.','.','.','6'};
		c[5]=a;
		a= new char[]{'.','6','.','.','.','.','2','8','.'};
		c[6]=a;
		a= new char[]{'.','.','.','4','1','9','.','.','5'};
		c[7]=a;
		a= new char[]{'.','.','.','.','8','.','.','7','9'};
		c[8]=a;
		ss.solveSudoku(c);
		
		for (int i=0;i<9;i++){
			for (int j=0; j<9; j++){
				assertTrue(c[i][j]==b[i][j]);
			}
		}
	}

}
