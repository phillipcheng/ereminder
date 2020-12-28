package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class MaxProfit {
    private static Logger logger =  LogManager.getLogger(MaxProfit.class);

    public int maxProfitWith1(int[] prices) {
        int buyPrice = Integer.MAX_VALUE;
        int mp = 0;//max profit
        for (int i=0; i<prices.length; i++){
            int price = prices[i];
            if (price < buyPrice){//find a cheaper buy price
                buyPrice = price;
            }
            if ((price - buyPrice) > mp){//has a transaction
                mp = price - buyPrice;
            }
        }
        return mp;
    }

    //7,1,5,3,6,4
    //1,2,3,4,5,6
    public int maxProfitWithUnlimited(int[] prices) {
        boolean hasSell = false;
        int buyPrice = Integer.MAX_VALUE;
        int sellPrice = -1;
        int total = 0;
        for (int i=0; i<prices.length; i++){
            int price = prices[i];
            if (!hasSell){
                if (price < buyPrice){
                    buyPrice = price;
                }
                if (price>buyPrice){
                    hasSell = true;
                    sellPrice = price;
                }
            }else{
                if (price > sellPrice){
                    sellPrice = price;
                }else{
                    total += (sellPrice - buyPrice);
                    buyPrice = price;
                    hasSell = false;
                }
            }
        }
        if (hasSell){
            total += (sellPrice - buyPrice);
        }
        return total;
    }

    //i: max profit: [0, i]
    public int[] maxProfitFrom0(int[] prices) {
        int buyPrice = Integer.MAX_VALUE;
        int n = prices.length;
        int[] ret = new int[n];
        int mp = 0;//max profit
        for (int i=0; i<n; i++){
            int price = prices[i];
            if (price < buyPrice){//find a cheaper buy price
                buyPrice = price;
            }
            if ((price - buyPrice) > mp){//has a transaction
                mp = price - buyPrice;
            }
            ret[i]=mp;
        }
        return ret;
    }

    // i: max profit: [i, n-1]
    public int[] maxProfitToN(int[] prices) {
        int sellPrice = 0;
        int n = prices.length;
        int[] ret = new int[n];
        int mp = 0;//max profit
        for (int i=n-1; i>=0; i--){
            int price = prices[i];
            if (price > sellPrice){//find a higher sell price
                sellPrice = price;
            }
            if ((sellPrice - price) > mp){//has a transaction
                mp = sellPrice - price;
            }
            ret[i]=mp;
        }
        return ret;
    }

    public int maxProfitWith2(int[] prices) {
        int n = prices.length;
        int mp = 0;
        int[] from0 = maxProfitFrom0(prices);
        int[] toN = maxProfitToN(prices);
        for (int i=0; i<n; i++){
            //i: [0, i], [i+1, n-1]
            if (i==n-1){
                mp = Math.max(from0[i], mp);
            }else {
                mp = Math.max(from0[i] + toN[i + 1], mp);
            }
        }
        return mp;
    }

    public int maxProfitWithK(int k, int[] prices) {
        if (prices.length == 0) {
            return 0;
        }

        int n = prices.length;
        k = Math.min(k, n / 2);
        int[][] buy = new int[n][k+1];//buy[i][j] = max at i day using <= j transactions end with buy
        int[][] sell = new int[n][k+1];//sell[i][j] = max at i day using <=j transactions end with sell

        buy[0][0] = -prices[0];
        sell[0][0] = 0;
        for (int i=1; i<=k; i++){
            buy[0][i] = 0;
            sell[0][i] = 0;
        }
        for (int i=1; i<n; i++){
            buy[i][0] = Math.max(buy[i - 1][0], sell[i - 1][0] - prices[i]);
            int p = prices[i];
            for (int j=1; j<k+1; j++){
                buy[i][j] = Math.max(buy[i-1][j], sell[i-1][j] - p);
                sell[i][j] = Math.max(sell[i-1][j], buy[i-1][j-1] + p);
            }
        }
        return Arrays.stream(sell[n - 1]).max().getAsInt();

    }

    public int maxProfitWithCooldown(int[] prices) {//with cooldown
        return 0;
    }

    public int maxProfitWithFee(int[] prices, int fee) {
        return 0;
    }

    public static void main(String[] args){
        MaxProfit maxProfit = new MaxProfit();
        int[] prices;
        int ret;

//        /////////////////
//        prices = new int[]{7,1,5,3,6,4};
//        ret = maxProfit.maxProfitWith1(prices);
//        logger.info(ret);
//        assertTrue(ret==5);
//
//        prices = new int[]{7,6,4,3,1};
//        ret = maxProfit.maxProfitWith1(prices);
//        logger.info(ret);
//        assertTrue(ret==0);
//
//        prices = new int[]{2,1,2,1,0,1,2};
//        ret = maxProfit.maxProfitWith1(prices);
//        logger.info(ret);
//        assertTrue(ret==2);
//
//        /////////////////
//        prices = new int[]{7,1,5,3,6,4};
//        ret = maxProfit.maxProfitWithUnlimited(prices);
//        logger.info(ret);
//        assertTrue(ret==7);
//
//        prices = new int[]{1,2,3,4,5};
//        ret = maxProfit.maxProfitWithUnlimited(prices);
//        logger.info(ret);
//        assertTrue(ret==4);
//
//        prices = new int[]{7,6,4,3,1};
//        ret = maxProfit.maxProfitWithUnlimited(prices);
//        logger.info(ret);
//        assertTrue(ret==0);
//
//        /////////////////
//        prices = new int[]{3,3,5,0,0,3,1,4};
//        ret = maxProfit.maxProfitWith2(prices);
//        logger.info(ret);
//        assertTrue(ret==6);
//
//        prices = new int[]{1,2,3,4,5};
//        ret = maxProfit.maxProfitWith2(prices);
//        logger.info(ret);
//        assertTrue(ret==4);
//
//        prices = new int[]{7,6,4,3,1};
//        ret = maxProfit.maxProfitWith2(prices);
//        logger.info(ret);
//        assertTrue(ret==0);
//
//        prices = new int[]{1};
//        ret = maxProfit.maxProfitWith2(prices);
//        logger.info(ret);
//        assertTrue(ret==0);

        ///////
        prices = new int[]{2,4,1};
        ret = maxProfit.maxProfitWithK(2, prices);
        logger.info(ret);
        assertTrue(ret==2);

        prices = new int[]{3,2,6,5,0,3};
        ret = maxProfit.maxProfitWithK(2, prices);
        logger.info(ret);
        assertTrue(ret==7);
    }

}
