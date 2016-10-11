package leet.algo;

public class BitwiseAndOfNumberRange {
	
	public int rangeBitwiseAnd(int m, int n) {
		if (m==0) return 0;
		int mf=1;
		while (m!=n){
			m>>=1;
			n>>=1;
			mf<<=1;
		}
		return mf*m;
    }
}
