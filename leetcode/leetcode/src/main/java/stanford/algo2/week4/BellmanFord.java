package stanford.algo2.week4;

import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.graph.AdjacentEdgeWeightedGraph;

public class BellmanFord {
	private static Logger logger =  LogManager.getLogger(BellmanFord.class);
	private AdjacentEdgeWeightedGraph g;
	
	public BellmanFord(AdjacentEdgeWeightedGraph g){
		this.g = g;
	}
	
	//
	public long[][] sp(int source){
		Set<Integer> vs = g.getVSet();
		int nv = vs.size();
		long[][] A = new long[nv+1][nv+1];//node index starting from 1
		for (int i=1; i<=nv; i++){
			if (i==source){
				A[0][i]=0;
			}else{
				A[0][i]=Integer.MAX_VALUE;
			}
		}
		for (int it=1; it<=nv; it++){
			for (int v=1; v<=nv; v++){
				long min = A[it-1][v];
				Map<Integer,Integer> edges = g.getNeighbors(v);
				for (int to:edges.keySet()){
					int weight = edges.get(to);
					if (A[it-1][to]+weight<min){
						min = A[it-1][to]+weight;
					}
				}
				A[it][v]=min;
				logger.info(String.format("A[%d][%d] is %d", it, v, min));
			}
		}
		return A;
	}

}
