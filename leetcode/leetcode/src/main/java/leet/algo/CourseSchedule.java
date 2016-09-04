package leet.algo;

import java.util.*;

import leet.algo.test.TestCourseSchedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CourseSchedule {
	private static Logger logger =  LogManager.getLogger(TestCourseSchedule.class);
	
	class DirectedGraph{//do parallel edges
		int n; //number of nodes
		Map<Integer, List<Integer>> graph;//to -> from edge map
		int[] outDegree; //out degree for each node
		public DirectedGraph(int n){
			this.n = n;
			this.graph = new HashMap<Integer, List<Integer>>();
			outDegree = new int[n];
		}
		
		public void addEdge(int to, int from){//
			List<Integer> li = graph.get(to);
			if (li==null){
				li = new ArrayList<Integer>();
				li.add(from);
				graph.put(to, li);
				outDegree[from]++;
			}else{
				if (!li.contains(from)){
					li.add(from);
					outDegree[from]++;
				}
			}
		}
		public boolean hasEdges(){
			return graph.size()>0;
		}
		
		public Set<Integer> initSinkNodes(){
			//all node with outDegree == 0
			Set<Integer> sinkNodes = new HashSet<Integer>();
			for (int i=0; i<n; i++){
				if (outDegree[i]==0){
					sinkNodes.add(i);
				}
			}
			return sinkNodes;
		}
		
		public List<Integer> removeNode(int toNode){
			List<Integer> newSinks = new ArrayList<Integer>();
			List<Integer> fromNodes = graph.get(toNode);
			graph.remove(toNode);
			if (fromNodes!=null){
				for (int fromNode:fromNodes){
					outDegree[fromNode]--;
					if (outDegree[fromNode]==0){
						newSinks.add(fromNode);
					}
				}
			}
			return newSinks;
		}
	}
	
	public boolean canFinish(int numCourses, int[][] prerequisites) {
		if (prerequisites.length==0 || prerequisites[0].length==0) return true;
		DirectedGraph dg = new DirectedGraph(numCourses);
		for (int[] pre:prerequisites){
        	dg.addEdge(pre[0], pre[1]);
        }
		Set<Integer> sinkNodes = dg.initSinkNodes();
		while (!sinkNodes.isEmpty()){
        	//logger.info(String.format("graph edges:%d, sinkNodes:%d:%s", dg.graph.size(), sinkNodes.size(), sinkNodes));
        	Set<Integer> newSinks = new HashSet<Integer>();
        	for (int node: sinkNodes){
        		newSinks.addAll(dg.removeNode(node));
        	}
        	sinkNodes.clear();
        	sinkNodes.addAll(newSinks);
        }
        //logger.info(String.format("graph edges:%s", dg.graph));
        if (dg.hasEdges()){
        	return false;
        }else{
        	return true;
        }
    }
}
