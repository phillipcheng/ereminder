package stanford.algo2.test.week4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.graph.AdjacentEdgeWeightedGraph;
import stanford.algo2.week4.FloydWarshall;

public class TestAPSP {
	private static Logger logger =  LogManager.getLogger(TestAPSP.class);
	@Test
	public void test1(){
		AdjacentEdgeWeightedGraph g = AdjacentEdgeWeightedGraph.createFromFileFTW(TestAPSP.class.getResourceAsStream("g3.txt"), true);
		FloydWarshall fw = new FloydWarshall(g);
		int[][][] A = fw.getApSp();
		int nv = A.length-1;
		int min=Integer.MAX_VALUE;
		StringBuffer sb = new StringBuffer();
		for (int i=1; i<=nv; i++){
			for (int j=1; j<=nv; j++){
				sb.append(A[i][j][0]).append(",");
				if (A[i][j][0]<min){
					min = A[i][j][0];
				}
			}
			sb.append("\n");
		}
		logger.info(String.format("sp sp is :%d", min));
		logger.info(String.format("n-1 iteration:%s", sb.toString()));
		sb = new StringBuffer();
		for (int i=1; i<=nv; i++){
			sb.append(A[i][i][1]).append(",");
		}
		logger.info(String.format("n iteration:%s", sb.toString()));
	}
	
	@Test
	public void test2(){
		AdjacentEdgeWeightedGraph g = AdjacentEdgeWeightedGraph.createFromFileFTW(TestAPSP.class.getResourceAsStream("simpleg.txt"), true);
		FloydWarshall fw = new FloydWarshall(g);
		int[][][] A = fw.getApSp();
		int nv = A.length-1;
		StringBuffer sb = new StringBuffer();
		for (int i=1; i<=nv; i++){
			for (int j=1; j<=nv; j++){
				sb.append(A[i][j][1]).append(",");
			}
			sb.append("\n");
		}
		logger.info(String.format("n-1 iteration:%s", sb.toString()));
		sb = new StringBuffer();
		for (int i=1; i<=nv; i++){
			sb.append(A[i][i][1]).append(",");
		}
		logger.info(String.format("n iteration:%s", sb.toString()));
	}
}
