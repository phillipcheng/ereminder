package algo.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


//permutation utils
public class PermUtil {
	private static Logger logger =  LogManager.getLogger(PermUtil.class);
	
	//choose m from n (0..n-1)
	//return list of bitset(n), each has m bit set
	public static List<BitSet> choose(int n, int m){
		List<BitSet> bsl = new ArrayList<BitSet>();
		if (m==0){
			//all 0
			BitSet bs = new BitSet(n);
			bsl.add(bs);
		}else if (n==m){
			//all 1
			BitSet bs = new BitSet(n);
			bs.flip(0, n);
			bsl.add(bs);
		}else{
			List<BitSet> bsl1 = choose(n-1, m-1);
			for (BitSet bs1:bsl1){
				BitSet bs = (BitSet) bs1.clone();
				bs.set(n-1, true);
				bsl.add(bs);
			}
			
			List<BitSet> bsl2 = choose(n-1, m);
			for (BitSet bs2:bsl2){
				BitSet bs = (BitSet) bs2.clone();
				bs.set(n-1, false);//adding the leading bit
				bsl.add(bs);
			}
		}
		//logger.info(String.format("choose %d from %d result: %s", m, n, bsl));
		return bsl;
	}
}
