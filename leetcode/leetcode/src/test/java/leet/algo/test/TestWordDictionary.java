package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.WordDictionary;

import org.junit.Test;

public class TestWordDictionary {
	
	@Test
	public void test1(){
		WordDictionary wordDictionary = new WordDictionary();
		wordDictionary.addWord("bad");
		wordDictionary.addWord("dad");
		wordDictionary.addWord("mad");
		assertTrue(wordDictionary.search("bad"));
		assertFalse(wordDictionary.search("pad"));
		assertTrue(wordDictionary.search(".ad"));
		assertTrue(wordDictionary.search("b.."));
		
	}

}
