package cy.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import cy.NQueen;

public class TestNQueen {

	@Test
	public void test() {
		NQueen nq = new NQueen();
		ArrayList<String[]> al = nq.solveNQueens(8);
		System.out.println(al.size());
	}

}
