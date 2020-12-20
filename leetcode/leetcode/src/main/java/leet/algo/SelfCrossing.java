package leet.algo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelfCrossing {
	private static Logger logger =  LogManager.getLogger(SelfCrossing.class);
	public boolean isSelfCrossing(int[] x) {
		int n = x.length;
		if (n<=3) return false;
		int idx=-1;
		for (int i=3; i<n; i++){
			if (i<=4){//3 or 4
				if (x[i]>=x[i-2] && x[i-1]<=x[i-3]){
					idx=i;
					break;
				}
				if (i==4 && x[i-1]==x[i-3] && x[i]+x[i-4]>=x[i-2]){//special case for i=4
					idx=i;
					break;
				}
			}else{//i>=5
				if (x[i-2]>=x[i-4]){
					if (x[i-1]>=x[i-3]-x[i-5] && x[i-1]<=x[i-3] && x[i]>=x[i-2]-x[i-4]
							|| x[i-1]<x[i-3]-x[i-5] && x[i]>=x[i-2]){
						idx=i;
						break;
					}
				}else{
					if(x[i-1]>=x[i-3] || x[i]>=x[i-2]){
						idx=i;
						break;
					}
				}
			}
		}
		//logger.info(idx);
		if (idx==-1)
			return false;
		else{
			return true;
		}
    }

}
