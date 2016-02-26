package stanford.algo2.week1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.graph.AdjacentEdgeWeightedGraph;
import algo.graph.Edge;
import algo.graph.EdgeWeightComparator;

public class PrimMST {
	private static Logger logger =  LogManager.getLogger(PrimMST.class);
	AdjacentEdgeWeightedGraph dg;
	EdgeWeightComparator ewc = new EdgeWeightComparator();
	PriorityQueue<Edge> eheap = new PriorityQueue<Edge>(10, ewc);
	Set<Integer> x = new HashSet<Integer>();
	
	public PrimMST(AdjacentEdgeWeightedGraph dg){
		this.dg = dg;
	}
	
	public List<Edge> getMST(){
		List<Edge> elist = new ArrayList<Edge>();
		//init
		int v = dg.getV();
		x.add(v);
		Map<Integer, Integer> edges = dg.getNeighbors(v);
		for (Integer t: edges.keySet()){
			int weight = edges.get(t);
			Edge edge = new Edge(v, t, weight);
			eheap.add(edge);
		}
		while (x.size()<dg.getVSize()){
			//get min edge
			if (eheap.isEmpty()){
				break;
			}else{
				Edge edge = null;
				do {
					edge = eheap.remove();
				}while (!eheap.isEmpty() && x.contains(edge.to) && x.contains(edge.from));
				if (edge!=null){
					elist.add(edge);
					v = edge.to;
					x.add(v);
					edges = dg.getNeighbors(v);
					if (edges!=null){
						for (Integer t: edges.keySet()){
							if (!x.contains(t)){//new edge
								int weight = edges.get(t);
								eheap.add(new Edge(v, t, weight));
							}
						}
					}
				}
			}
		}
		return elist;
	}

}
