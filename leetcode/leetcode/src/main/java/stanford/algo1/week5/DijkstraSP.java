package stanford.algo1.week5;

import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.graph.AdjacentEdgeWeightedGraph;
import algo.graph.EdgeWeightComparator;
import algo.graph.UndirectedWithHeapWeightEdge;

public class DijkstraSP {
	private static Logger logger =  LogManager.getLogger(DijkstraSP.class);
	private AdjacentEdgeWeightedGraph g;
	
	public DijkstraSP(AdjacentEdgeWeightedGraph g){
		this.g = g;
	}
	
	//return the array of shortest path from source to dest
	public int[] shortestPath(int source){
		int[] sp = new int[g.getVSize()]; //shortest path output array
		for (int i=0; i<sp.length; i++){
			if (i==source-1){
				sp[i]=0;
			}else{
				sp[i]=Integer.MAX_VALUE;
			}
		}
		Set<Integer> vset = new HashSet<Integer>();
		vset.add(source);
		Set<Integer> bset = new HashSet<Integer>();
		bset.addAll(g.getVSet()); //the set of vertices not in V
		bset.remove(source);
		EdgeWeightComparator ewc = new EdgeWeightComparator();
		PriorityQueue<UndirectedWithHeapWeightEdge> eheap = new PriorityQueue<UndirectedWithHeapWeightEdge>(10, ewc);
		Map<Integer, Integer> toMap = g.getNeighbors(source);
		for (int to:toMap.keySet()){
			int weight = toMap.get(to);
			eheap.add(new UndirectedWithHeapWeightEdge(source, to, weight, weight));
		}
		while (!eheap.isEmpty()){
			UndirectedWithHeapWeightEdge edge = eheap.remove();
			logger.info(String.format("added edge:%s", edge));
			int newNode = 0;
			if (vset.contains(edge.from)){
				sp[edge.to-1] = sp[edge.from-1] + edge.weight;
				newNode = edge.to;
			}else{
				sp[edge.from-1] = sp[edge.to-1] + edge.weight;
				newNode = edge.from;
			}
			toMap = g.getNeighbors(newNode);
			for (int to:toMap.keySet()){
				//add edges in bset to heap
				int weight = toMap.get(to);
				if (bset.contains(to)){
					eheap.add(new UndirectedWithHeapWeightEdge(newNode, to, weight, sp[newNode-1]+weight));
				}
				//remove edges from heap
				if (vset.contains(to)){
					eheap.remove(new UndirectedWithHeapWeightEdge(newNode, to, weight, 0));
				}
			}
			bset.remove(newNode);
			vset.add(newNode);
		}
		return sp;
	}
}
