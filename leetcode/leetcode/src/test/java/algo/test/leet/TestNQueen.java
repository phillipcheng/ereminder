package algo.test.leet;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import algo.leet.NQueen;

public class TestNQueen {

	@Test
	public void test() {
		NQueen nq = new NQueen();
		ArrayList<String[]> al = nq.solveNQueens(8);
		System.out.println(al.size());
	}

}
