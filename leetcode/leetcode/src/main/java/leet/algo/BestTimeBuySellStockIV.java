package leet.algo;

//max profit at most k transactions
public class BestTimeBuySellStockIV {
	//
	public int maxProfit(int k, int[] prices) {
		int n = prices.length;
		if (n==0 || k==0) return 0;
        int[][] A = new int[k][n];
        for (int k1=1; k1<k; k1++){
        	for (int j=0; j<n; j++){
        		A[k1][0]=0;
        	}
        	for (int i=1; i<n; i++){
        		for (int j=0; j<i; j++){//[j,i] i>=1
        		}
        	}
        }
        return A[k-1][n-1];
    }
}
