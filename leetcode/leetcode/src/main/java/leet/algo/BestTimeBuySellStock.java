package leet.algo;

//get max profit for only 1 transaction
public class BestTimeBuySellStock {
	
	public int maxProfit(int[] prices) {
		if (prices.length==0) return 0;
		int n = prices.length;
		int maxProfit = 0;
		int minPrice = prices[0];
		for (int i=1;i<n; i++){
        	if (prices[i]>minPrice){
        		maxProfit = Math.max(maxProfit, prices[i]-minPrice);
        	}else{
        		minPrice = prices[i];
        	}
        }
		return maxProfit;
    }

}
