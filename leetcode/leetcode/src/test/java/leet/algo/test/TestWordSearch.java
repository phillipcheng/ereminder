package leet.algo.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.StringUtil;
import leet.algo.WordSearch;
import leet.algo.WordSearchII;

public class TestWordSearch {
	private static Logger logger =  LogManager.getLogger(TestWordSearch.class);
	
	public void printArrayList(List<int[]> locs){
		for (int[] loc: locs){
			logger.info(String.format("init locs: %s", Arrays.toString(loc)));
		}
	}
	
	@Test
	public void test1(){
		char[][] board = new char[][]{{'A','B','C','E'},{'S','F','C','S'}, {'A','D','E','E'}};
		WordSearch ws = new WordSearch();
		boolean b;
		b = ws.exist(board, "ABCCED");
		assertTrue(b);
		b = ws.exist(board, "SEE");
		assertTrue(b);
		b = ws.exist(board, "ABCB");
		assertFalse(b);
		
	}
	
	@Test
	public void test2(){
		char[][] board = new char[][]{{'a','a'}};
		WordSearch ws = new WordSearch();
		boolean b;
		b = ws.exist(board, "aaa");
		assertFalse(b);
		
	}
	
	@Test
	public void test3(){
		char[][] board = new char[][]{{'A','B','C','E'},{'S','F','E','S'},{'A','D','E','E'}};
		WordSearch ws = new WordSearch();
		boolean b;
		b = ws.exist(board, "ABCESEEEFS");
		assertTrue(b);
	}
	
	@Test
	public void test4(){
		char[][] board = new char[][]{{'a','a','a','a'},{'a','a','a','a'},{'a','a','a','a'},{'a','a','a','a'},{'a','a','a','b'}};
		WordSearch ws = new WordSearch();
		boolean b;
		b = ws.exist(board, "aaaaaaaaaaaaaaaaaaaa");
		assertFalse(b);
	}
	
	@Test
	public void testA(){
		char[][] board = new char[][]{{'o','a','a','n'},{'e','t','a','e'},{'i','h','k','r'},{'i','f','l','v'}};
		String[] words = new String[]{"eat", "oath"};
		WordSearchII ws2 = new WordSearchII();
		List<String> sl = ws2.findWords(board, words);
		logger.info(sl);
	}

	@Test
	public void testB(){
		char[][] board = new char[][]{{'a','a','a','a'},{'a','a','a','a'},{'a','a','a','a'}};
		String[] words = new String[]{"aaaaaaaaaaaa", "aaaaaaaaaaaaa", "aaaaaaaaaaab"};
		WordSearchII ws2 = new WordSearchII();
		List<String> sl = ws2.findWords(board, words);
		logger.info(sl);
	}
	
	//0.1s
	@Test
	public void testC(){
		char[][] board = new char[][]{{'s','e','e','n','o','w'},{'t','m','r','i','v','a'}, {'o','b','s','i','b','d'}, {'w','m','y','s','e','n'}, {'l','t','s','n','s','a'},{'i','e','z','l','g','n'}};
		String[] words = StringUtil.readStrings("wordsearch.txt");
		WordSearchII ws2 = new WordSearchII();
		List<String> sl = ws2.findWords(board, words);
		logger.info(sl);
	}

}
