package cy.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class UndirectedGraph<T>{
	T src;
	//adjacent edges 
	public Map<T, HashSet<T>> edges = new HashMap<T, HashSet<T>>();
	public Map<T, Integer> distTo = new HashMap<T, Integer>();
	
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
		}
		if (!al.contains(b)){
			al.add(b);
			edges.put(a, al);
		}
		
		//add a to b's neighbor
		al = edges.get(b);
		if (al==null){
			al = new HashSet<T>();
		}
		if (!al.contains(a)){
			al.add(a);
			edges.put(b, al);
		}
	}
	
	public void setSource(T n){		
		src = n;
		edges.put(src, null);
		distTo.clear();
		distTo.put(src, 0);
	}
	
	
	public ArrayList<ArrayList<T>> getPath(T dest){
		if (dest.equals(src)){
			ArrayList<ArrayList<T>> aal = new ArrayList<ArrayList<T>>();
			ArrayList<T> al = new ArrayList<T>();
			al.add(src);
			aal.add(al);
			return aal;
		}
		
		if (distTo.containsKey(dest)){
			int sp = distTo.get(dest);
			HashSet<T> neighbors = edges.get(dest);
			ArrayList<ArrayList<T>> aal = new ArrayList<ArrayList<T>>();
			Iterator<T> its = neighbors.iterator();
			while (its.hasNext()){
				T neighbor = its.next();
				if (distTo.get(neighbor)==(sp-1)){
					ArrayList<ArrayList<T>> bbl = getPath(neighbor);
					for (int j=0; j<bbl.size(); j++){
						ArrayList<T> al = bbl.get(j);
						al.add(dest);
						aal.add(al);
					}
				}
			}
			return aal;
		}else{
			ArrayList<ArrayList<T>> aal = new ArrayList<ArrayList<T>>();
			return aal;
		}
		
	}
	
	Queue<T> queue = new LinkedList<T>();
	
	public void bfs(T start){
		int d = distTo.get(start);
		if (edges.containsKey(start)){
			HashSet<T> neighbors = edges.get(start);
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