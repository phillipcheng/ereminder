package stanford.algo2.test.week5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.graph.LocationGraph;
import stanford.algo2.week5.TSP;

public class TestTSP {
	private static Logger logger =  LogManager.getLogger(TestTSP.class);
	
	@Test
	public void test1(){
		LocationGraph g = LocationGraph.createFromFile(TestTSP.class.getResourceAsStream("tsp.txt"));
		TSP tsp = new TSP(g);
		float v = tsp.getMinLoop();
		logger.info(String.format("%.4f", v));
	}

	
	@Test
	public void test2(){
		LocationGraph g = LocationGraph.createFromFile(TestTSP.class.getResourceAsStream("tspTC2.txt"));
		TSP tsp = new TSP(g);
		float v = tsp.getMinLoop();
		logger.info(String.format("%.4f", v));//4
	}
	
	@Test
	public void test3(){
		LocationGraph g = LocationGraph.createFromFile(TestTSP.class.getResourceAsStream("tspTC3.txt"));
		TSP tsp = new TSP(g);
		float v = tsp.getMinLoop();
		logger.info(String.format("%.4f", v));//10.4721
	}
	
	@Test
	public void test4(){
		LocationGraph g = LocationGraph.createFromFile(TestTSP.class.getResourceAsStream("tspTC4.txt"));
		TSP tsp = new TSP(g);
		float v = tsp.getMinLoop();
		logger.info(String.format("%.5f", v));// 6.17986
	}
	
	@Test
	public void test9(){
		LocationGraph g = LocationGraph.createFromFile(TestTSP.class.getResourceAsStream("tspTC9.txt"));
		TSP tsp = new TSP(g);
		float v = tsp.getMinLoop();
		logger.info(String.format("%.5f", v));// 26714.9
	}
}
