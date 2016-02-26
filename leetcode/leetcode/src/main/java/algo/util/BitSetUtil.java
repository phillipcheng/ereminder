package algo.util;

import java.util.BitSet;

public class BitSetUtil {
	
	public static BitSet shiftLeft(BitSet bs){
		BitSet out = new BitSet(bs.length()+1);
		for (int i=bs.length()-1; i>=0; i--){
			out.set(i+1, bs.get(i));
		}
		return out;
	}

}
