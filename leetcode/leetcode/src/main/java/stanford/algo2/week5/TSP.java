package stanford.algo2.week5;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.graph.LocationGraph;
import algo.util.BitSetUtil;
import algo.util.PermUtil;

public class TSP {
	private static Logger logger =  LogManager.getLogger(TSP.class);
	
	LocationGraph g;
	Map<Integer, Float>[] aMap;
	
	public TSP(LocationGraph g){
		this.g = g;
		aMap = new Map[g.getNv()+1];
	}
	
	public static int convert(BitSet bits) {
	    int value = 0;
	    for (int i = 0; i < bits.length(); ++i) {
	      value += bits.get(i) ? (1L << i) : 0L;
	    }
	    return value;
	  }
	
	private float getA(BitSet s, int dest){
		if (dest==1){
			if (s.length()==1 && s.get(0)){
				return 0f;
			}else{
				return Integer.MAX_VALUE;
			}
		}else{
			return aMap[dest].get(convert(s));
		}
	}
	
	private void setA(BitSet s, int dest, float v){
		if (aMap[dest]==null){
			aMap[dest] = new HashMap<Integer, Float>();
		}
		aMap[dest].put(convert(s), v);
	}
	
	public float getMinLoop(){
		int n = g.getNv();
		for (int m=2; m<=n; m++){
			logger.info(String.format("loop %d", m));
			List<BitSet> bsl = PermUtil.choose(n-1, m-1);
			for (BitSet bsx: bsl){
				BitSet bs = BitSetUtil.shiftLeft(bsx);
				bs.set(0, true);//bs is n select m with 1 selected
				//logger.info(String.format("loop m:%d, s:%s", m, bs));
				for (int j=0; j<bs.length(); j++){//0:n-1
					if (bs.get(j) && j!=0){
						bs.clear(j);//bs2 is the set containing 1, trimming dest j, m-1 vertices
						float min=Integer.MAX_VALUE;
						for (int k=0; k<bs.length(); k++){
							if (bs.get(k) && k!=j){
								float v = getA(bs, k+1) + g.cost(k, j);
								if (v<min){
									min = v;
								}
							}
						}
						//logger.info(String.format("setA bs:%s, k:%d, min:%.4f", bs2, j+1, min));
						bs.set(j, true);
						setA(bs, j+1, min);
					}
				}
			}
		}
		float tsp=Integer.MAX_VALUE;
		BitSet all = new BitSet(n);
		all.flip(0, n);//all set
		for (int j=2; j<=n; j++){
			float v = getA(all, j) + g.cost(j-1, 0);
			if (v<tsp){
				tsp = v;
			}
		}
		return tsp;
	}
}
