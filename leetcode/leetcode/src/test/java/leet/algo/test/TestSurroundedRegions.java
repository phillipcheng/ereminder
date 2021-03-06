package leet.algo.test;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.BoardUtil;
import leet.algo.SurroundedRegions;

public class TestSurroundedRegions {
	private static Logger logger =  LogManager.getLogger(TestSurroundedRegions.class);
	
	@Test
	public void test1() {
		SurroundedRegions sr = new SurroundedRegions();
		char[][] board = new char[4][4];
		board[0]=new char[]{'X','X','X','X'};
		board[1]=new char[]{'X','O','O','X'};
		board[2]=new char[]{'X','X','O','X'};
		board[3]=new char[]{'X','O','X','X'};
		
		sr.solve(board);
		logger.info(BoardUtil.getBoardString(board));
		
	}
	
	@Test
	public void test2() {
		SurroundedRegions sr = new SurroundedRegions();
		char[][] board = new char[5][4];
		board[0]=new char[]{'X','X','X','X'};
		board[1]=new char[]{'X','O','O','X'};
		board[2]=new char[]{'X','X','O','X'};
		board[3]=new char[]{'X','O','X','X'};
		board[4]=new char[]{'X','X','X','X'};
		
		sr.solve(board);
		logger.info(BoardUtil.getBoardString(board));
		
	}
	
	@Test
	public void test3() {
		SurroundedRegions sr = new SurroundedRegions();
		
	}

}
