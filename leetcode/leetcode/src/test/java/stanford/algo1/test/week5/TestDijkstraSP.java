package stanford.algo1.test.week5;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.graph.AdjacentEdgeWeightedGraph;
import stanford.algo1.week5.DijkstraSP;

public class TestDijkstraSP {
	private static Logger logger =  LogManager.getLogger(TestDijkstraSP.class);
	@Test
	public void test1(){
		AdjacentEdgeWeightedGraph g = AdjacentEdgeWeightedGraph.createFromFileFLTW(TestDijkstraSP.class.getResourceAsStream("dijkstraData.txt"), false);
		DijkstraSP dsp = new DijkstraSP(g);
		int[] sp = dsp.shortestPath(1);
		logger.info(Arrays.toString(sp));
		int[] tos = new int[]{7,37,59,82,99,115,133,165,188,197};
		StringBuffer sb = new StringBuffer();
		int i=0;
		for (int to:tos){
			if (i>0){
				sb.append(",");
			}
			sb.append(sp[to-1]);
			i++;
		}
		logger.info(sb.toString());
	}
	
	@Test
	public void test2(){
		AdjacentEdgeWeightedGraph g = AdjacentEdgeWeightedGraph.createFromFileFLTW(TestDijkstraSP.class.getResourceAsStream("dijkstra_small.txt"), false);
		DijkstraSP dsp = new DijkstraSP(g);
		int[] sp = dsp.shortestPath(1);
		logger.info(Arrays.toString(sp));
	}

}
