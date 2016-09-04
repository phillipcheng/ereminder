package leet.algo;

import java.util.Arrays;

public class HIndex {
	public int hIndex(int[] citations) {
		Arrays.sort(citations);
		int n = citations.length;
		for (int i=0; i<n; i++){
			if (n-i <= citations[i]){
				if (i>0 && citations[i]>=citations[i-1]){
					return n-i;
				}else if (i==0){
					return n-i;
				}
			}
		}
		return 0;
    }

}
