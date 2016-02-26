package stanford.algo2.test.week1;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.graph.AdjacentEdgeWeightedGraph;
import algo.graph.Edge;
import stanford.algo2.week1.PrimMST;

public class TestPrim {
	private static Logger logger =  LogManager.getLogger(TestPrim.class);
	@Test
	public void test1(){
		AdjacentEdgeWeightedGraph dg = AdjacentEdgeWeightedGraph.createFromFileFTW(TestPrim.class.getResourceAsStream("edges.txt"), false);
		PrimMST prim = new PrimMST(dg);
		List<Edge> el = prim.getMST();
		long cost =0;
		for (Edge e:el){
			cost+=e.weight;
			logger.info(String.format("edge:%s", e));
		}
		logger.info(String.format("cost:%d", cost));
	}

}
