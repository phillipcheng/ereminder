package leet.algo;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WordDictionary {
	private static Logger logger =  LogManager.getLogger(WordDictionary.class);
	
	class TrieNode {
		private boolean isWord;
	    private String value;
	    private TrieNode[] children = new TrieNode[26];//to speed up using array instead of map
	    
	    public TrieNode(String v, boolean isWord) {
	        this.value = v;
	        this.isWord = isWord;
	    }
	    
	    public TrieNode getChild(char ch){
	    	return children[ch-'a'];
	    }
	    public TrieNode addChild(char ch, boolean isWord){
	    	String v = value + ch;
	    	TrieNode tn = new TrieNode(v, isWord);
	    	children[ch-'a']=tn;
	    	return tn;
	    }
	    public boolean isWord(){
	    	return isWord;
	    }
	    public void setWord(boolean isword){
	    	this.isWord = isword;
	    }
	}
	
	private TrieNode root;

    public WordDictionary() {
        root = new TrieNode("", false);
    }

    // Adds a word into the data structure.
    public void addWord(String word) {
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

    private boolean search(TrieNode start, String word){
    	if (word.length()==0){
    		if (start.isWord){
    			return true;
    		}else{
    			return false;
    		}
    	}else{
        	char ch = word.charAt(0);
	    	if (ch!='.'){
	    		TrieNode n = start.getChild(ch);
	    		if (n==null){
	    			return false;
	    		}else{
	    			return search(n, word.substring(1));
	    		}
	    	}else{
	    		boolean found = false;
	    		for (ch='a'; ch<='z'; ch++){
	    			TrieNode n = start.getChild(ch);
	    			if (n!=null){
	    				found = search(n, word.substring(1));
	    				if (found){
	    					return true;
	    				}
	    			}
	    		}
	    		return found;
	    	}
    	}
    }
    
    // Returns if the word is in the data structure. A word could
    // contain the dot character '.' to represent any one letter.
    public boolean search(String word) {
    	return search(root, word);
    }

}
