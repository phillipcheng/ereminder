package algo.graph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeListGraph {
	private static Logger logger =  LogManager.getLogger(EdgeListGraph.class);
	
	public List<Edge> edgeList = new ArrayList<Edge>();
	private int nv;
	
	public List<Edge> getEdgeList() {
		return edgeList;
	}
	public void setEdgeList(List<Edge> edgeList) {
		this.edgeList = edgeList;
	}
	public int getNv() {
		return nv;
	}
	public void setNv(int nv) {
		this.nv = nv;
	}
	
	public void addEdge(Edge edge){
		edgeList.add(edge);
	}

	public static EdgeListGraph createFromFile(InputStream is) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			EdgeListGraph dg = new EdgeListGraph();
			dg.setNv(Integer.parseInt(line));
			logger.info(String.format("#v: %d", dg.getNv()));
			while ((line=br.readLine())!=null){
				String[] wl = line.split(" ");
				int from = Integer.parseInt(wl[0]);
				int to = Integer.parseInt(wl[1]);
				int weight = Integer.parseInt(wl[2]);
				Edge edge =new Edge(from, to, weight); 
				dg.addEdge(edge);
				//logger.info(String.format("edge added:%s", edge));
			}
			br.close();
			return dg;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}

}
