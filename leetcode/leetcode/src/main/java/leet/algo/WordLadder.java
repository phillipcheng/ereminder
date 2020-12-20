package leet.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WordLadder {
	private static Logger logger =  LogManager.getLogger(WordLadder.class);
	
	public class UndirectedGraph<T>{
		T src;
		//adjacent edges 
		public Map<T, HashSet<T>> edges = new HashMap<T, HashSet<T>>();
		public Map<T, Integer> distTo = new HashMap<T, Integer>(); //distance to each node from source
		
		public UndirectedGraph(){
		}
		
		public String toString(){
			return "source:" + src + "\n" + 
					"edges:" + edges + "\n" + 
					"distTo:" + distTo + "\n";
		}
		
		public void addNode(T n){
			edges.put(n, null);
		}
		
		//update the edges for both a and b
		public void addEdge(T a, T b){
			//add b to a's neighbor
			HashSet<T> al = edges.get(a);
			if (al==null){
				al = new HashSet<T>();
				edges.put(a, al);
			}
			al.add(b);
			
			//add a to b's neighbor
			al = edges.get(b);
			if (al==null){
				al = new HashSet<T>();
				edges.put(b, al);
			}
			al.add(a);
		}
		
		public void setSource(T n){		
			src = n;
			edges.put(src, null);
			distTo.clear();
			distTo.put(src, 0);
		}
		
		public List<List<T>> getPath(T dest){
			List<List<T>> aal = new ArrayList();
			if (dest.equals(src)){
				List<T> al = new ArrayList<T>();
				al.add(src);
				aal.add(al);
			}else if (distTo.containsKey(dest)){
				int sp = distTo.get(dest);
				HashSet<T> neighbors = edges.get(dest);
				for (T neighbor: neighbors){
					if (distTo.get(neighbor)==(sp-1)){
						List<List<T>> bbl = getPath(neighbor);
						for (int j=0; j<bbl.size(); j++){
							List<T> al = bbl.get(j);
							al.add(dest);
							aal.add(al);
						}
					}
				}
			}
			return aal;
		}
		
		Queue<T> queue = new LinkedList<T>();
		
		public void bfs(T start){
			queue.add(start);
			while (!queue.isEmpty()){
				T current = queue.remove();
				int d = distTo.get(current);
				HashSet<T> neighbors = edges.get(current);
				if (neighbors != null){
					for(T neighbor:neighbors){
						if (!distTo.containsKey(neighbor)){//not visited
							queue.add(neighbor);
							distTo.put(neighbor, d+1);
						}
					}
				}else{
					System.err.println("no neighbor found for vertice:" + current);
				}
			}
		}
	}
	
	public UndirectedGraph<String> setup(String beginWord, String endWord, Set<String> wordList){
		//long startTime = System.nanoTime();
		UndirectedGraph<String> g = new UndirectedGraph<String>();
		g.setSource(beginWord);
		for (String s:wordList){
			g.addNode(s);
		}
		g.addNode(endWord);
		int size = g.edges.keySet().size();
		String[] vArray = new String[size];
		g.edges.keySet().toArray(vArray);
		
		//long time1 = System.nanoTime();
		//logger.info("time:" + (time1-startTime));
		for (String from:vArray){
			for (int i=0; i<from.length(); i++){
				for (char ch='a'; ch<='z'; ch++){
					if (ch!=from.charAt(i)){
						char[] chs = from.toCharArray();
						chs[i]=ch;
						String newStr = new String(chs);
						if (g.edges.keySet().contains(newStr)){
							g.addEdge(from, newStr);
						}
					}
				}
			}
		}
		//long time2 = System.nanoTime();
		//logger.info("time:" + (time2-time1));
		g.bfs(beginWord);
		//long time3 = System.nanoTime();
		//logger.info("time:" + (time3-time2));
		return g;
	}
	
	public int ladderLength(String beginWord, String endWord, Set<String> wordList) {
		UndirectedGraph<String> g = setup(beginWord, endWord, wordList);
		if (g.distTo.containsKey(endWord)){		
			return g.distTo.get(endWord)+1;
		}else{
			return 0;
		}
    }
	
    public List<List<String>> findLadders(String beginWord, String endWord, Set<String> wordList) {
    	UndirectedGraph<String> g = setup(beginWord, endWord, wordList);
    	return g.getPath(endWord);
    }
}
