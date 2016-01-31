package cy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import cy.util.UndirectedGraph;

public class WordLadderII {
 
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
	
	public static ArrayList<ArrayList<String>> findLadders(String start, 
    		String end, HashSet<String> dict) {
		
		long time0 = System.nanoTime();
		
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
		
		long time1 = System.nanoTime();
		System.out.println("init time elapsed:" + (time1-time0));
		
		for (int i=0; i<vArray.length; i++){
			String from = vArray[i];
			for (int j=i+1; j<vArray.length; j++){
				String to = vArray[j];
				if (diffOneCharNum(from, to)){
					g.addEdge(from, to);
				}
			}
		}
		
		long time2 = System.nanoTime();
		System.out.println("add edges time elapsed:" + (time2-time1));
		
		g.bfs(start);
		
		long time3 = System.nanoTime();
		System.out.println("bfs time elapsed:" + (time3-time2));
		
		ArrayList<ArrayList<String>> aal =  g.getPath(end);
		
		long time4 = System.nanoTime();
		System.out.println("get path time elapsed:" + (time4-time3));
		
		return aal;
    }
	
	
}
