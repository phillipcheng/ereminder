package algo.graph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdjacentEdgeWeightedGraph {
	private static Logger logger =  LogManager.getLogger(AdjacentEdgeWeightedGraph.class);
	
	private boolean directed=true;
	
	public AdjacentEdgeWeightedGraph(boolean directed){
		this.directed = directed;
	}
	
	private Map<Integer, Map<Integer, Integer>> g = new HashMap<Integer, Map<Integer, Integer>>(); //<fromV, <toT, weight>>
	
	private void addDirEdge(int from, int to, int weight){
		Map<Integer, Integer> edges = null;
		if (g.containsKey(from)){
			edges = g.get(from);
		}else{
			edges = new HashMap<Integer, Integer>();
			g.put(from, edges);
		}
		if (!g.containsKey(to)){
			Map<Integer, Integer> toEdges = new HashMap<Integer, Integer>();
			g.put(to, toEdges);
		}
		edges.put(to, weight);
	}
	
	public void addEdge(int from, int to, int weight){
		if (directed){
			addDirEdge(from, to, weight);
		}else{
			addDirEdge(from, to, weight);
			addDirEdge(to, from, weight);
		}
	}
	
	//return Integer.MIN_VALUE, if not found
	public int getEdge(int from, int to){
		Map<Integer, Integer> neighbors = g.get(from);
		if (neighbors!=null){
			if (neighbors.containsKey(to)){
				return neighbors.get(to);
			}else{
				return Integer.MIN_VALUE;
			}
		}else{
			return Integer.MIN_VALUE;
		}
	}
	
	public Map<Integer, Integer> getNeighbors(int from){
		return g.get(from);
	}
	
	//get a random vertex
	public Integer getV(){
		return g.keySet().iterator().next();
	}
	
	public Set<Integer> getVSet(){
		return g.keySet();
	}
	
	public int getVSize(){
		return g.size();
	}

	//file format: from, to, weight
	//#vertex #edges
	//from to weight
	public static AdjacentEdgeWeightedGraph createFromFileFTW(InputStream is, boolean directed) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			String[] wl = line.split(" ");
			int nv = Integer.parseInt(wl[0]);
			int ne = Integer.parseInt(wl[1]);
			AdjacentEdgeWeightedGraph dg = new AdjacentEdgeWeightedGraph(directed);
			logger.info(String.format("#v: %d, #edge: %d", nv, ne));
			while (--ne>=0){
				line = br.readLine();
				wl = line.split(" ");
				int from = Integer.parseInt(wl[0]);
				int to = Integer.parseInt(wl[1]);
				int weight = Integer.parseInt(wl[2]);
				dg.addEdge(from, to, weight);
			}
			br.close();
			return dg;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	//file format: from : list of (to, weight)
	//from: to,weight to,weight to,weight...
	public static AdjacentEdgeWeightedGraph createFromFileFLTW(InputStream is, boolean directed) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			AdjacentEdgeWeightedGraph dg = new AdjacentEdgeWeightedGraph(directed);
			String line;
			while ((line=br.readLine())!=null){
				String[] wl = line.split("\\s+");
				int from = Integer.parseInt(wl[0]);
				for (int i=1; i<wl.length; i++){
					String[] tw = wl[i].split(",");
					int to = Integer.parseInt(tw[0]);
					int weight = Integer.parseInt(tw[1]);
					dg.addEdge(from, to, weight);
				}
			}
			br.close();
			return dg;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
}
