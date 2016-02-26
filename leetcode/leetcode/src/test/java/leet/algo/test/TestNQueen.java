package leet.algo.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import leet.algo.NQueen;

public class TestNQueen {

	@Test
	public void test() {
		NQueen nq = new NQueen();
		ArrayList<String[]> al = nq.solveNQueens(8);
		System.out.println(al.size());
	}

}
