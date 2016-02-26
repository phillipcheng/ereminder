package stanford.algo2.week2;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.graph.BitSetVertexGraph;
import algo.util.PermUtil;
import algo.util.UnionFind;

public class KClusteringNoEdge {
	private static Logger logger =  LogManager.getLogger(KClusteringNoEdge.class);
	private BitSetVertexGraph g;
	private UnionFind uf;
	
	public KClusteringNoEdge(BitSetVertexGraph g){
		this.g = g;
		uf = new UnionFind(g.getNv()); //uf idx = vertice.name -1 
	}
	
	List<BitSet> getBs(BitSet bs, List<BitSet> choose){
		//logger.info(String.format("input bs:%s", bs));
		List<BitSet> bsl = new ArrayList<BitSet>();
		for (BitSet si: choose){
			BitSet abs = (BitSet) bs.clone();
			for (int i=0;i<si.length(); i++){
				if (si.get(i)){
					abs.flip(i);
				}
			}
			bsl.add(abs);
			//logger.info(String.format("si is %s, output abs:%s", si, abs));
		}
		return bsl;
	}
	
	//return max cluster value k so that there is a k-clustering with space at least "spacing" 
	public long getMaxCluster(int spacing){
		Set<BitSet> vertices = g.getBsMap().keySet();
		int cl = vertices.size();
		BitSet bs1 = vertices.iterator().next();
		List<BitSet> choose = new ArrayList<BitSet>();
		for (int i=1; i<spacing; i++){
			choose.addAll(PermUtil.choose(bs1.length(), i));
		}
		logger.info(String.format("map size:%d, bits:%d, perm size:%d", g.getBsMap().size(), bs1.length(), choose.size()));
		for (BitSet bs:vertices){
			int bidx = g.getVertexIdx(bs);
			logger.info(String.format("%d", bidx));
			List<BitSet> bsl = getBs(bs, choose);
			for (BitSet abs: bsl){
				int aidx = g.getVertexIdx(abs);
				if (aidx!=-1){
					if (uf.root(bidx)!=uf.root(aidx)){//merge
						uf.union(aidx, bidx);
						cl--;
					}
				}
			}
		}
		return cl;
	}
}
