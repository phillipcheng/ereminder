package leet.algo;

//max profit multiple transactions
public class BestTimeBuySellStockII {
	
	public int maxProfit(int[] prices) {
		if (prices.length<=1) return 0;
		int n = prices.length;
		int accumProfit = 0;
		int prePrice = prices[0];
		boolean hasStock=false;
		int buyPrice=0;
		for (int i=1; i<n; i++){
			int newPrice = prices[i];
			if (newPrice>prePrice){
				if (!hasStock){
					buyPrice=prePrice;
					hasStock=true;
				}
			}else if (newPrice<prePrice){
				if (hasStock){
					accumProfit += prePrice-buyPrice;
					hasStock=false;
				}
			}
			prePrice=newPrice;
		}
		if (hasStock){
			accumProfit += prePrice-buyPrice;
		}
		return accumProfit;
    }

}
