package leet.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Given a 2D board and a list of words from the dictionary, find all words in the board.
public class WordSearchII {
	private static Logger logger =  LogManager.getLogger(WordSearchII.class);
	
	class TrieNode {
		private boolean isWord;
	    private String value;
	    private TrieNode children[] = new TrieNode[26];
	    
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
	    	children[ch-'a'] = tn;
	    	return tn;
	    }
	    public boolean isWord(){
	    	return isWord;
	    }
	    public void setWord(boolean isword){
	    	this.isWord = isword;
	    }
	}

	class Trie {
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

	    // Returns if there is any word in the trie
	    // that starts with the given prefix.
	    public TrieNode startsWith(String prefix) {
	    	TrieNode node = root;
	    	TrieNode n = null;
	        for (int i=0; i<prefix.length(); i++){
	        	char ch = prefix.charAt(i);
	        	n = node.getChild(ch);
	        	if (n == null){
	        		break;
	        	}else{
	        		node = n;
	        	}
	    	}
	        return n;
	    }
	}
	
	//prefix in trie, now come to x,y
	public boolean hasWord(char[][] board, boolean[][] history, int x, int y, String prefix, Trie trie, Set<String> matched){
		//logger.info(String.format("at x:%d, y:%d, prefix:%s", x, y, prefix));
		int nx = board.length;
        int ny = board[0].length;
		prefix = prefix + board[x][y];
		TrieNode tn = trie.startsWith(prefix);
		boolean hasNext=false;
		if (tn != null){
			history[x][y]=true;
			if (tn.isWord){
				matched.add(prefix);
			}
			if (x+1<nx && !history[x+1][y]){
				hasNext = hasNext || hasWord(board, history, x+1, y, prefix, trie, matched);
			}
			if (x-1>=0 && !history[x-1][y]){
				hasNext = hasNext || hasWord(board, history, x-1, y, prefix, trie, matched);
			}
			if (y+1<ny && !history[x][y+1]){
				hasNext = hasNext || hasWord(board, history, x, y+1, prefix, trie, matched);
			}
			if (y-1>=0 && !history[x][y-1]){
				hasNext = hasNext || hasWord(board, history, x, y-1, prefix, trie, matched);
			}
		}
		//logger.info(String.format("at x:%d, y:%d, prefix:%s, result:%b", x, y, prefix, hasNext));
		if (hasNext){
			return true;
		}else{
			history[x][y]=false;
			return false;
		}
	}
	
	public List<String> findWords(char[][] board, String[] words) {
		Trie trie = new Trie();
        for (String word:words){
        	trie.insert(word);
        }
        int nx = board.length;
        int ny = board[0].length;
        Set<String> matched = new HashSet<String>();
        for (int x=0; x<nx; x++){
        	for (int y=0; y<ny; y++){
        		boolean[][] visited = new boolean[nx][ny];
        		hasWord(board, visited, x, y, "", trie, matched);
        	}
        }
        List<String> ret = new ArrayList<String>();
        ret.addAll(matched);
        return ret;
    }
}
