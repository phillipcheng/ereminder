package algo.graph;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class NoWeightDirectedGraph<T>{
	T src;
	//adjacent edges 
	public Map<T, TreeSet<T>> edges = new HashMap<T, TreeSet<T>>();
	public Map<T, Integer> distTo = new HashMap<T, Integer>();
	
	public NoWeightDirectedGraph(){
	}
	
	public String toString(){
		return "source:" + src + "\n" + 
				"edges:" + edges + "\n" + 
				"distTo:" + distTo + "\n";
	}
	
	public void addNode(T n){
		edges.put(n, null);
	}
	
	public void setSource(T n){		
		src = n;
		edges.put(src, null);
		distTo.clear();
		distTo.put(src, 0);
	}
	
	//update the edges for both a and b
	public void addEdge(T from, T to){
		//add to to from's neighbor
		TreeSet<T> al = edges.get(from);
		if (al==null){
			al = new TreeSet<T>();
		}
		if (!al.contains(to)){
			al.add(to);
			edges.put(from, al);
		}
	}
	
	public void removeEdge(T from, T to){
		TreeSet<T> al = edges.get(from);
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
			T to = edges.get(src).first();
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
	
	//generate the distance map
	Queue<T> queue = new LinkedList<T>();
	public void bfs(T start){
		int d = distTo.get(start);
		if (edges.containsKey(start)){
			TreeSet<T> neighbors = edges.get(start);
			if (neighbors != null){
				Iterator<T> its = neighbors.iterator();
				while(its.hasNext()){
					T neighbor = its.next();
					if (!distTo.containsKey(neighbor)){
						queue.add(neighbor);
						distTo.put(neighbor, d+1);
					}
				}
				T next=null; 
				while ((next=queue.poll())!= null){
					bfs(next);
				}	
			}else{
				System.err.println("no neighbor found for vertice:" + start);
			}
		}else{
			System.err.println("no such vertice in the graph:" + start);
		}
	}
}
