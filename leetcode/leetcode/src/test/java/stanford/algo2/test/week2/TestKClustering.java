package stanford.algo2.test.week2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.graph.BitSetVertexGraph;
import algo.graph.EdgeListGraph;
import stanford.algo2.week2.KClustering;
import stanford.algo2.week2.KClusteringNoEdge;

public class TestKClustering {
	private static Logger logger =  LogManager.getLogger(TestKClustering.class);
	@Test
	public void test1(){
		EdgeListGraph g = EdgeListGraph.createFromFile(TestKClustering.class.getResourceAsStream("clustering1.txt"));
		KClustering kc = new KClustering(g);
		long space = kc.getMaxSpacing(4);
		logger.info(String.format("space: %d", space));
	}
	
	@Test
	public void test2(){
		EdgeListGraph g = EdgeListGraph.createFromFile(TestKClustering.class.getResourceAsStream("simpleclustering.txt"));
		KClustering kc = new KClustering(g);
		long space = kc.getMaxSpacing(3);
		logger.info(String.format("space: %d", space));
	}
	
	@Test
	public void testVertexGraph1(){
		BitSetVertexGraph bsvg = BitSetVertexGraph.createFromFile(TestKClustering.class.getResourceAsStream("clustering_big.txt"));
		KClusteringNoEdge kc = new KClusteringNoEdge(bsvg);
		long cl = kc.getMaxCluster(3);
		logger.info(String.format("cluster: %d", cl));
	}
	
	@Test
	public void testVertexGraph2(){
		BitSetVertexGraph bsvg = BitSetVertexGraph.createFromFile(TestKClustering.class.getResourceAsStream("clustering_small.txt"));
		KClusteringNoEdge kc = new KClusteringNoEdge(bsvg);
		long cl = kc.getMaxCluster(2);
		logger.info(String.format("cluster: %d", cl));
	}

}
