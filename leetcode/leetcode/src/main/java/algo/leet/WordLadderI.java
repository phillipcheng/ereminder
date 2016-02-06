package algo.leet;

import java.util.HashSet;
import java.util.Iterator;

import algo.util.UndirectedGraph;


public class WordLadderI {
	public static boolean diffOneCharNum(String a, String b){		
		int count =0;
		for (int i=0; i<a.length(); i++){
			if (a.charAt(i) != b.charAt(i)){
				count++;
				if (count>=2){
					return false;
				}
			}
		}
		
		if (count == 1){
			return true;
		}
		
		return false;
	}
	
	public int ladderLength(String start, String end, HashSet<String> dict) {
		
		UndirectedGraph<String> g = new UndirectedGraph<String>();
		g.setSource(start);
		Iterator<String> its = dict.iterator();
		while (its.hasNext()){
			String s = its.next();
			g.addNode(s);
		}
		g.addNode(end);
		int size = g.edges.keySet().size();
		String[] vArray = new String[size];
		g.edges.keySet().toArray(vArray);
		
		
		for (int i=0; i<vArray.length; i++){
			String from = vArray[i];
			for (int j=i+1; j<vArray.length; j++){
				String to = vArray[j];
				if (diffOneCharNum(from, to)){
					g.addEdge(from, to);
				}
			}
		}
		
		g.bfs(start);
		
		if (g.distTo.containsKey(end)){		
			return g.distTo.get(end)+1;
		}else{
			return 0;
		}
    }

}
