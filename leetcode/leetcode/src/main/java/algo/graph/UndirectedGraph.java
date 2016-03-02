package algo.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
		int d = distTo.get(start);
		if (edges.containsKey(start)){
			HashSet<T> neighbors = edges.get(start);
			if (neighbors != null){
				for(T neighbor:neighbors){
					if (!distTo.containsKey(neighbor)){//not visited
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