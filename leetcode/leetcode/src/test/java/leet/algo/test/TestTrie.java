package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.Trie;

public class TestTrie {
	
	@Test
	public void test1(){
		Trie trie = new Trie();
		trie.insert("abc");
		assertTrue(trie.search("abc")==true);
		assertTrue(trie.search("ab")==false);
		trie.insert("ab");
		assertTrue(trie.search("ab")==true);
		trie.insert("ab");
		assertTrue(trie.search("ab")==true);
	}
}
