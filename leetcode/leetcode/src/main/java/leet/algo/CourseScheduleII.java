package leet.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CourseScheduleII {
	class NoWeightDirectedGraph{
		//adjacent edges 
		public Map<Integer, TreeSet<Integer>> edges = new HashMap<Integer, TreeSet<Integer>>();//from, to set
		public Map<Integer, TreeSet<Integer>> toMap = new HashMap<Integer, TreeSet<Integer>>();//to, from set (find source)
		
		public NoWeightDirectedGraph(){
		}
		
		public String toString(){
			return "edges:" + edges + "\n";
		}
		
		public void addNode(int n){
			edges.put(n, null);
			toMap.put(n, null);
		}
		
		//update the edges for both a and b
		public void addEdge(int from, int to){
			//add to to from's neighbor
			TreeSet<Integer> al = edges.get(from);
			if (al==null){
				al = new TreeSet<Integer>();
			}
			if (!al.contains(to)){
				al.add(to);
				edges.put(from, al);
			}
			//
			TreeSet<Integer> bl = toMap.get(to);
			if (bl==null){
				bl = new TreeSet<Integer>();
			}
			if (!bl.contains(from)){
				bl.add(from);
				toMap.put(to, bl);
			}
		}
		
		public List<Integer> getSrcs(){
			List<Integer> srcs = new ArrayList<Integer>();
			for (int to:toMap.keySet()){
				if (toMap.get(to)==null){
					srcs.add(to);
				}
			}
			return srcs;
		}
		
		public boolean dfs(int start, List<Integer> output, boolean[] visited, Set<Integer> visitedPath){
			visited[start]=true;
			visitedPath.add(start);
			TreeSet<Integer> myDist = edges.get(start);
			if (myDist!=null){
				for (int dist:myDist){
					if (visitedPath.contains(dist)) return false;
					if (!visited[dist]){
						if (!dfs(dist, output, visited, visitedPath)){
							return false;
						}
						visitedPath.remove(dist);
					}
				}
			}
			if (output.contains(start)){
				return false;
			}else{
				output.add(start);
				return true;
			}
		}
	}
	
	public int[] findOrder(int numCourses, int[][] prerequisites) {
		int[] result;
		if (prerequisites.length==0 || prerequisites.length==1 && prerequisites[0].length==0){
			result = new int[numCourses];
			for (int i=0; i<numCourses; i++){
				result[i] = i;
			}
			return result;
		}
		NoWeightDirectedGraph graph = new NoWeightDirectedGraph();
		for (int i=0; i<numCourses; i++){
			graph.addNode(i);
		}
		for (int[] pair:prerequisites){
			graph.addEdge(pair[1], pair[0]);
		}
		
		List<Integer> output = new ArrayList<Integer>();
		boolean[] visited = new boolean[numCourses];
		List<Integer> srcs = graph.getSrcs();
		for (int src:srcs){
			Set<Integer> si = new TreeSet<Integer>();
			boolean ret = graph.dfs(src, output, visited, si);
			if (!ret){
				return new int[]{};
			}
		}
		if (output.size()==numCourses){
			result = new int[numCourses];
			for (int i=0; i<numCourses; i++){
				result[i] = output.get(numCourses-1-i);
			}
			return result;
		}
        return new int[]{};
    }
}
