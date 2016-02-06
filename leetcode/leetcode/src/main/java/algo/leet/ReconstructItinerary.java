package algo.leet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class DirectedGraph<T>{
	T src;
	//adjacent edges 
	public Map<T, List<T>> edges = new HashMap<T, List<T>>();
	
	public DirectedGraph(){
	}
	
	public String toString(){
		return "source:" + src + "\n" + 
				"edges:" + edges + "\n";
	}
	
	public void addNode(T n){
		edges.put(n, null);
	}
	
	public void setSource(T n){		
		src = n;
		edges.put(src, null);
	}
	
	//update the edges for both a and b
	public void addEdge(T from, T to){
		//add to to from's neighbor
		List<T> al = edges.get(from);
		if (al==null){
			al = new ArrayList<T>();
			edges.put(from, al);
		}
		al.add(to);
	}
	
	public void removeEdge(T from, T to){
		List<T> al = edges.get(from);
		if (al!=null){
			if (al.contains(to)){
				al.remove(to);
			}
		}
	}
	
	private boolean hasEdges(T src){
		if (edges.containsKey(src)){
			return edges.get(src).size()>0;
		}else{
			return false;
		}
	}
	
	public void getEulerianPath(T src, Stack<T> stack, List<T> output){
		if (hasEdges(src)){
			T to = edges.get(src).get(0);
			removeEdge(src, to);
			stack.push(src);
			getEulerianPath(to, stack, output);
		}else{
			output.add(0, src);
			if (!stack.empty()){
				getEulerianPath(stack.pop(), stack, output);
			}			
		}
	}
}

public class ReconstructItinerary {
	
	public List<String> findItinerary(String[][] tickets) {
		DirectedGraph<String> dg = new DirectedGraph<String>();
        for (String[] ticket:tickets){
        	String from = ticket[0];
        	String to = ticket[1];
        	dg.addEdge(from, to);
        }
        for (List<String> edges: dg.edges.values()){
        	Collections.sort(edges);
        }
        String start = "JFK";
        List<String> path = new ArrayList<String>();
        Stack<String> stack = new Stack<String>();
        dg.getEulerianPath(start, stack, path);
        return path;
    }
}
