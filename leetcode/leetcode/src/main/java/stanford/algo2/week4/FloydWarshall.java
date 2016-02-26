package stanford.algo2.week4;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.graph.AdjacentEdgeWeightedGraph;

public class FloydWarshall {
	private static Logger logger =  LogManager.getLogger(FloydWarshall.class);
	AdjacentEdgeWeightedGraph g;
	
	public FloydWarshall(AdjacentEdgeWeightedGraph g){
		this.g = g;
	}
	
	public int[][][] getApSp(){
		int n = g.getVSize();
		int[][][] A = new int[n+1][n+1][2];//save space, we need only 2 layer
		//init
		for (int i=1; i<=n; i++){
			for (int j=1; j<=n; j++){
				if (i==j){
					A[i][j][0]=0;
				}else{
					int w = g.getEdge(i, j);
					if (w==Integer.MIN_VALUE){//no edge
						A[i][j][0] = Integer.MAX_VALUE;
					}else{
						A[i][j][0] = w;
					}
				}
				//logger.info(String.format("A[%d][%d][0]:%d", i,j,A[i][j][0]));
			}
		}
		for (int k=1; k<=n; k++){
			for (int i=1; i<=n; i++){
				for (int j=1; j<=n; j++){
					int x = 0;
					if (A[i][k][0] == Integer.MAX_VALUE || A[k][j][0]==Integer.MAX_VALUE){
						x = Integer.MAX_VALUE;
					}else{
						x = A[i][k][0]+A[k][j][0];
					}
					A[i][j][1] = Math.min(A[i][j][0], x);
					if (A[i][j][1]<-200000){
						logger.info(String.format("A[%d][%d][1]:%d, k:%d", i,j,A[i][j][1],k));
					}
				}
			}
			if (k<n){
				for (int i=1; i<=n; i++){
					for (int j=1; j<=n; j++){
						A[i][j][0] = A[i][j][1];
					}
				}
			}//for last time, no copy
		}
		return A;
	}

}
