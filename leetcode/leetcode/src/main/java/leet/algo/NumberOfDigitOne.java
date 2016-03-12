package leet.algo;

public class NumberOfDigitOne {
	
	//Given an integer n, count the total number of digit 1 appearing in all non-negative integers less than or equal to n.
	public int countDigitOne(int n) {
		//2147483647
		int[] a = new int[]{0,1,20,300,4000,50000,600000,7000000,80000000,900000000};
		int[] b = new int[]{1,10,100,1000,10000,100000,1000000,10000000,100000000,1000000000};
		int d;
		int ret=0;
		int i=0;
		int in = n;
		while (in>0){
			in=in/10;
			i++;
		}//i is the number of digits
		while (i>=1){
			int nt = (int) Math.pow(10, i-1);
			if (nt>1){
				d = n/nt;
				n = n%nt;
			}else{
				d = n;
				n = 0;
			}
			if (d==0){
				
			}else if (d==1){
				ret+=a[i-1] + 1 + n;
			}else{
				ret+=d*a[i-1]+b[i-1];
			}
			i--;
		}
        return ret;
    }

}
