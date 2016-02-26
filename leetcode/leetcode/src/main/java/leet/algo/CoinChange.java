package leet.algo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//You are given coins of different denominations and a total amount of money amount.
//Write a function to compute the fewest number of coins that you need to make up that amount. 
//If that amount of money cannot be made up by any combination of the coins, return -1.
public class CoinChange {
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	//endIndex
	public int tryChange(int[] coins, int amount){
		if (map.containsKey(amount)){
			return map.get(amount);
		}
		if (amount==0){
			return 0;
		}
		int findNum=0;
		int min = Integer.MAX_VALUE;
		for (int coin:coins){
			if (amount-coin>=0){
				int cnt = tryChange(coins, amount-coin);
				if (cnt>=0){
					if (1+cnt < min){
						min = 1 + cnt;
					}
					findNum++;
				}
			}
		}
		if (findNum==0){
			map.put(amount, -1);
			return -1;
		}else{
			map.put(amount, min);
			return min;
		}
	}
	
	public int coinChange(int[] coins, int amount) {
        return tryChange(coins, amount);
    }
}
