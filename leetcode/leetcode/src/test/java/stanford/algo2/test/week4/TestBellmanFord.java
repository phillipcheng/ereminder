package stanford.algo2.test.week4;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.graph.AdjacentEdgeWeightedGraph;
import stanford.algo2.week4.BellmanFord;

public class TestBellmanFord {
	private static Logger logger =  LogManager.getLogger(TestBellmanFord.class);
	
	@Test
	public void test1(){
		AdjacentEdgeWeightedGraph g = AdjacentEdgeWeightedGraph.createFromFileFLTW(TestBellmanFord.class.getResourceAsStream("dijkstraData.txt"), false);
		BellmanFord bf = new BellmanFord(g);
		long[][] sp = bf.sp(1);
		int nv = sp.length;
		logger.info(Arrays.toString(sp[nv-1]));
	}
	
	@Test
	public void test2(){
		AdjacentEdgeWeightedGraph g = AdjacentEdgeWeightedGraph.createFromFileFLTW(TestBellmanFord.class.getResourceAsStream("dijkstra_small.txt"), false);
		BellmanFord bf = new BellmanFord(g);
		long[][] sp = bf.sp(1);
		int nv = sp.length;
		logger.info(Arrays.toString(sp[nv-1]));
	}

}
