package leet.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import leet.algo.test.TestAdditiveNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.BoardUtil;

public class MinimumHeightTrees {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	private int max(int[] input){
		int max = Integer.MIN_VALUE;
		for (int i=0; i<input.length; i++){
			if (input[i]>max){
				max = input[i];
			}
		}
		return max;
	}
	private int min(int[] input){
		int min = Integer.MAX_VALUE;
		for (int i=0; i<input.length; i++){
			if (input[i]<min){
				min = input[i];
			}
		}
		return min;
	}
	
	
	public List<Integer> findMinHeightTreesSlow(int n, int[][] edges) {
        int[][] m = new int[n][n];
        for (int i=0; i<edges.length; i++){
        	int a = edges[i][0];
        	int b = edges[i][1];
        	m[a][b]=1;
        	m[b][a]=1;
        	for (int j=0; j<n; j++){
        		if (m[b][j]>0 && j!=a && m[a][j]==0){
        			m[a][j]=m[b][a]+m[b][j];
        			m[j][a]=m[a][j];
        		}
        	}
        	for (int j=0; j<n; j++){
        		if (m[a][j]>0 && j!=b && m[b][j]==0){
        			m[b][j]=m[b][a]+m[a][j];
        			m[j][b]=m[b][j];
        		}
        	}
        	//logger.info(BoardUtil.getBoardString(m));
        }
        int[] max = new int[n];
        for (int i=0; i<n; i++){
        	max[i]= max(m[i]);
        }
        int min = min(max);
        List<Integer> li = new ArrayList<Integer>();
        for (int i=0; i<n; i++){
        	if (max[i]==min){
        		li.add(i);
        	}
        }
        return li;
    }
	
	////////////////////
	
	private List<Integer> getLeaves(Map<Integer,List<Integer>> graph){
		List<Integer> li = new ArrayList<Integer>();
		for (Integer k:graph.keySet()){
			if (graph.get(k).size()==1){
				li.add(k);
			}
		}
		return li;
	}
	
	private List<Integer> shrink(Map<Integer,List<Integer>> graph, List<Integer> leaves){
		List<Integer> newLeaves = new ArrayList<Integer>();
		for (int leaf:leaves){
			List<Integer> nodes = graph.get(leaf);
			int node = nodes.get(0);
			graph.remove(leaf);
			List<Integer> rest = graph.get(node);
			rest.remove(new Integer(leaf));
			if (rest.size()==1){
				newLeaves.add(node);
			}
		}
		return newLeaves;
	}
	
	private void addEdge(Map<Integer,List<Integer>> graph, int a, int b){
		List<Integer> li = graph.get(a);
		if (li==null){
			li = new ArrayList<Integer>();
			li.add(b);
			graph.put(a, li);
		}else{
			li.add(b);
		}
	}
	
	public List<Integer> findMinHeightTrees(int n, int[][] edges) {
		if (n==1) {
			List<Integer> li = new ArrayList<Integer>(); 
			li.add(0);
			return li;
		}
		Map<Integer,List<Integer>> graph = new HashMap<Integer, List<Integer>>();
		for (int[] edge : edges){
			int a = edge[0];
			int b = edge[1];
			addEdge(graph, a, b);
			addEdge(graph, b, a);
		}
		List<Integer> leaves = getLeaves(graph);
		while(graph.size()>2){
			leaves = shrink(graph, leaves);
		}
		return leaves;
	}

}
