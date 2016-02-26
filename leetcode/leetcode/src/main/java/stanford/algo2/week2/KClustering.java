package stanford.algo2.week2;

import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.graph.Edge;
import algo.graph.EdgeListGraph;
import algo.graph.EdgeWeightComparator;
import algo.util.UnionFind;

public class KClustering {
	private static Logger logger =  LogManager.getLogger(KClustering.class);
	private EdgeListGraph g;
	private PriorityQueue<Edge> eheap;
	private UnionFind uf;
	
	
	public KClustering(EdgeListGraph g){
		this.g = g;
		uf = new UnionFind(g.getNv()); //uf idx = vertice.name -1 
		EdgeWeightComparator ewc = new EdgeWeightComparator();
		eheap = new PriorityQueue<Edge>(10, ewc);
		eheap.addAll(g.getEdgeList());
	}
	
	public long getMaxSpacing(int k){
		int cl = g.getNv();
		while (cl>k){
			Edge edge = eheap.remove();
			if (uf.root(edge.from-1) == uf.root(edge.to-1)){//same cluster already ignore
			}else{//merge
				uf.union(edge.from-1, edge.to-1);
				cl--;
				logger.info(String.format("merge %d with %d, weight:%d, cluster now:%d", edge.from-1, edge.to-1, edge.weight, cl));
			}
		}
		while(true){//reached k cluster, get the distance
			Edge edge = eheap.remove();
			if (uf.root(edge.from-1) == uf.root(edge.to-1)){//same cluster already ignore
			}else{
				return edge.weight;
			}
		}
	}
}
