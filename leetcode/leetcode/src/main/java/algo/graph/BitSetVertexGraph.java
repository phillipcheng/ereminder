package algo.graph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//we do not explicitly specify the edge cost, each vertex is of a bitset value, and the hamming distance of two nodes is the cost of the edge
public class BitSetVertexGraph {
	private static Logger logger =  LogManager.getLogger(BitSetVertexGraph.class);
	
	private int nv;//# of vertex
	private int nb;//number of bits for each vertex
	private Map<BitSet, Integer> bsMap = new HashMap<BitSet, Integer>(); //bitset to index
	
	public BitSetVertexGraph(int nv, int nb){
		this.nv = nv;
		this.nb = nb;
	}
	public int getNb() {
		return nb;
	}
	public void setNb(int nb) {
		this.nb = nb;
	}
	public int getNv() {
		return nv;
	}
	public void setVertex(int idx, BitSet bs){
		bsMap.put(bs, idx);
	}
	public int getVertexIdx(BitSet bs){
		if (bsMap.containsKey(bs)){
			return bsMap.get(bs);
		}else{
			return -1;
		}
	}
	public Map<BitSet, Integer> getBsMap(){
		return bsMap;
	}
	
	public static BitSetVertexGraph createFromFile(InputStream is) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			String[] wl = line.split(" ");
			int nv = Integer.parseInt(wl[0]);
			int nb = Integer.parseInt(wl[1]);
			BitSetVertexGraph dg = new BitSetVertexGraph(nv, nb);
			logger.info(String.format("#v:%d, #b:%d", dg.getNv(), dg.getNb()));
			int idx = 0;
			while ((line=br.readLine())!=null){
				wl = line.split(" ");
				BitSet bs = new BitSet(nb);
				for (int i=0; i<nb;i++){
					bs.set(i, Integer.parseInt(wl[i])==1);
				}
				dg.setVertex(idx, bs);
				idx++;
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
