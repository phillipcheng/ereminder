package cy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.Trie;

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
