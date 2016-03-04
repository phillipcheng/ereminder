package leet.algo;

//max profit at most 2 transactions
public class BestTimeBuySellStockIII {
	
	//max profit starting from day 0 using 1 transaction
	public int[] maxProfitFrom0(int[] prices) {
		int n = prices.length;
		int[] mp = new int[n];
		if (prices.length==0) return mp;
		int maxProfit = 0;
		mp[0]=0;
		int minPrice = prices[0];
		for (int i=1;i<n; i++){
        	if (prices[i]>minPrice){
        		maxProfit = Math.max(maxProfit, prices[i]-minPrice);
        	}else{
        		minPrice = prices[i];
        	}
        	mp[i]=maxProfit;
        }
		return mp;
    }
	
	//max profit ending to day n using 1 transaction
	public int[] maxProfitToN(int[] prices) {
		int n = prices.length;
		int[] mp = new int[n];
		if (prices.length==0) return mp;
		int maxProfit = 0;
		mp[n-1]=0;
		int maxPrice = prices[n-1];
		for (int i=n-2;i>=0; i--){
        	if (prices[i]<maxPrice){
        		maxProfit = Math.max(maxProfit, maxPrice - prices[i]);
        	}else{
        		maxPrice = prices[i];
        	}
        	mp[i]=maxProfit;
        }
		return mp;
    }
	
	public int maxProfit(int[] prices) {
		if (prices.length<=1) return 0;
		int n = prices.length;
		int[] p0 = maxProfitFrom0(prices);
		int[] pn = maxProfitToN(prices);
		int accumProfit = 0;
		for (int i=0; i<n; i++){
			int p = p0[i] + pn[i];
			if (accumProfit<p){
				accumProfit = p;
			}
		}
		return accumProfit;
    }

}
