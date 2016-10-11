package leet.algo;

import java.util.TreeSet;

public class UglyNumberII {
	
	public int nthUglyNumber(int n) {
		TreeSet<Long> ts = new TreeSet<Long>();
		int i=1;
		long v = 1;
		ts.add(v);
		while (i<n){
			long s = ts.first();
			ts.remove(s);
			ts.add(s*2);
			ts.add(s*3);
			ts.add(s*5);
			v = ts.first();
			i++;
		}
        return (int)v;
    }

}
