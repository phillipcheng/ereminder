package algo.leet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrieNode {
	private boolean isWord;
    private String value;
    private Map<Character, TrieNode> children = new HashMap<Character, TrieNode>();
    
    public TrieNode(String v, boolean isWord) {
        this.value = v;
        this.isWord = isWord;
    }
    
    public TrieNode getChild(char ch){
    	return children.get(ch);
    }
    public TrieNode addChild(char ch, boolean isWord){
    	String v = value + ch;
    	TrieNode tn = new TrieNode(v, isWord);
    	children.put(ch, tn);
    	return tn;
    }
    public boolean isWord(){
    	return isWord;
    }
    public void setWord(boolean isword){
    	this.isWord = isword;
    }
}

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode("", false);
    }

    // Inserts a word into the trie.
    public void insert(String word) {
    	TrieNode node = root;
        for (int i=0; i<word.length(); i++){
        	char ch = word.charAt(i);
        	TrieNode n = node.getChild(ch);
        	if (n == null){
        		n = node.addChild(ch, i==word.length()-1);
        	}else{
        		if (i==word.length()-1){
        			if (!n.isWord()){
        				n.setWord(true);
        			}
        		}
        	}
        	node = n;
        }
    }

    // Returns if the word is in the trie.
    public boolean search(String word) {
    	TrieNode node = root;
        for (int i=0; i<word.length(); i++){
        	char ch = word.charAt(i);
        	TrieNode n = node.getChild(ch);
        	if (n == null){
        		return false;
        	}else{
        		node = n;
        	}
    	}
        return node.isWord();
    }

    // Returns if there is any word in the trie
    // that starts with the given prefix.
    public boolean startsWith(String prefix) {
    	TrieNode node = root;
        for (int i=0; i<prefix.length(); i++){
        	char ch = prefix.charAt(i);
        	TrieNode n = node.getChild(ch);
        	if (n == null){
        		return false;
        	}else{
        		node = n;
        	}
    	}
        return true;
    }
}