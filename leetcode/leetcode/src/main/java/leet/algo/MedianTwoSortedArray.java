package leet.algo;

import java.util.Arrays;

public class MedianTwoSortedArray {
	
	public static final boolean isDebug=false;
	int count;
	/*
	 * find the dth number from the combined sorted arrays: a[fromA, toA], b[fromB, toB] 
	 * d can be float: 1.5-th means the average from 1st and 2nd
	 */
	public double findDth(int a[], int b[], int fromA, int toA, int fromB, int toB, float d){	
		if(isDebug){
			int[] ac = Arrays.copyOfRange(a, fromA, toA+1);
			System.out.println(Arrays.toString(ac));
			int[] bc = Arrays.copyOfRange(b, fromB, toB+1);			
			System.out.println(Arrays.toString(bc));
			System.out.println("d:" + d);
		}
		count++;
		if (a[fromA]<=b[fromB]){
			if (a[toA]<=b[fromB]){
				int aLength = toA-fromA+1;
				if ((int)d == d){//d is integer
					if (d<=aLength){
						return a[fromA + (int)d-1];
					}else{
						return b[fromB + (int)d - aLength -1];
					}
				}else{
					int dl = (int) d;
					int dh = dl +1;
					if (dl<aLength){
						return (a[fromA + dl - 1]+ a[fromA + dh -1])/2f;
					}else if (dl==aLength){
						return (a[fromA + dl - 1] + b[fromB])/2f;
					}else{
						return (b[fromB + dl - 1 - aLength] + b[fromB + dh -1 - aLength])/2f; 
					}
				}
			}else{
				if (d ==  1){
					System.out.println("Never Enter.");
					return a[fromA];
				}else if ((int)d == 1){//1.5
					if (a[fromA+1]>b[fromB]){
						//a[fromA], b[fromB], a[fromA+1]
						return (a[fromA] + b[fromB])/2f;
					}else{
						//a[fromA], a[fromA+1], b[fromB]
						return (a[fromA] + a[fromA+1])/2f;
					}
				}else if (d == 2){
					if (a[fromA+1]>b[fromB]){
						//a[fromA], b[fromB], a[fromA+1]
						return b[fromB];
					}else{
						//a[fromA], a[fromA+1], b[fromB]
						return a[fromA+1];
					}
				}else if ((toB - fromB) ==0){
					//d>2, a[fromA]<=b[fromB]<a[toA], fromB=toB
					int bV = b[fromB];
					int ip = Arrays.binarySearch(a, fromA, toA, b[fromB]);
					if (ip < 0){//
						ip = -1 * ip -1;
					}
					ip++;
					if ((int)d==d){
						if (d<ip){
							return a[fromA + (int)d -1];
						}else if (d==ip){
							return bV; 
						}else{//d>ip
							return a[fromA + (int)d -2 ];
						}
					}else{
						int ld = (int)d;//ld>=2
						int hd = ld + 1;
						if (ip > hd){
							return (a[fromA + ld -1] + a[fromA + hd -1])/2f;
						}else if (ip==hd || ip == ld){
							return (a[fromA + ld -1] + bV)/2f;
						}else {//ip<ld
							if (ip == ld-1){
								return (a[fromA + ld -1] + bV)/2f;
							}else{
								return (a[fromA + ld-2] + a[fromA+ld-1])/2f;
							}
						}
					}
				}else if (d==2.5 && (toA-fromA==1) && (toB-fromB==1)){
					if (a[toA]>=b[toB]){
						return (b[fromB]+b[toB])/2f;
					}else{
						return (a[toA]+b[fromB])/2f;
					}
				}else{
				
					//d>2, a[fromA]<=b[fromB]<a[toA] 
					//toA-fromA>1, toB-fromB>1
					double medianAIdx = (fromA+toA)/2f;
					int lowAIdx, highAIdx;
					int reduceA;
					double medianA;
					if ((int)medianAIdx == medianAIdx){//lowIdx, medianIdx, highIdx
						medianA = a[(int)medianAIdx];
						lowAIdx = (int) medianAIdx;
						highAIdx =(int) medianAIdx;
						reduceA = (int) ((toA-fromA)/2);
					}else{//x.5
						lowAIdx = (int)medianAIdx;
						highAIdx = (int)medianAIdx+1;
						medianA = (a[lowAIdx] + a[highAIdx]) /2f;
						reduceA = (int) ((toA-fromA)/2);
					}
					
					double medianBIdx = (fromB+toB)/2f;
					int lowBIdx, highBIdx;
					double medianB;
					int reduceB;
					if ((int)medianBIdx == medianBIdx){//lowIdx, medianIdx, highIdx
						medianB = b[(int)medianBIdx];
						lowBIdx = (int) medianBIdx;
						highBIdx = (int) medianBIdx;
						reduceB = (int) ((toB-fromB)/2);
					}else{//x.5
						lowBIdx = (int)medianBIdx;
						highBIdx = (int)medianBIdx+1;
						medianB = (b[lowBIdx] + b[highBIdx]) /2f;
						reduceB = (int) ((toB-fromB)/2);
					}
					
					if (medianA < medianB){
						return findDth(a, b, lowAIdx, toA, fromB, highBIdx, d-reduceA);
					}else{
						return findDth(a, b, fromA, highAIdx, lowBIdx, toB, d-reduceB);
					}			 
				}
				
			}
		}else{
			return findDth(b, a, fromB, toB, fromA, toA, d);
		}
	}
	
	public double median(int A[]){
		int l = A.length;
		if (l%2==0){
			int m = l/2;
			return (A[m-1]+A[m])/2f;
		}else{
			return A[l/2];
		}
	}
	
    public double findMedianSortedArrays(int A[], int B[]) {
    	double ret =0d;
    	if (A.length==0 && B.length>0){
    		return median(B);
    	}else if (A.length>0 && B.length==0){
    		return median(A);
    	}else{
	    	float d = (A.length + B.length + 1)/2f;
	    	ret = findDth(A, B, 0, A.length-1, 0, B.length-1, d);
    	}
    	System.out.println(count);
    	return ret;
    }
}
