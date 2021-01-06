package leet.algo;

import static org.junit.Assert.assertTrue;

public class CanPlaceFlowers {

    int getValue(int[] flowbed, int i){
        if (i<0){
            return 0;
        }else if (i>=flowbed.length){
            return 0;
        }else{
            return flowbed[i];
        }
    }
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        int plant=0;
        for (int i=0; i<flowerbed.length; i++){
            if (getValue(flowerbed,i)==0){
                if (getValue(flowerbed, i-1)==0 && getValue(flowerbed, i+1)==0){
                    plant++;
                    flowerbed[i]=1;
                }
            }
        }
        return n<=plant;
    }

    public static void main(String[] args){
        CanPlaceFlowers canPlaceFlowers = new CanPlaceFlowers();
        int[] flowbed;
        int n;
        boolean ret;

        flowbed = new int[]{1,0,0,0,1};
        n=1;
        ret = canPlaceFlowers.canPlaceFlowers(flowbed, n);
        assertTrue(ret);


        flowbed = new int[]{1,0,0,0,1};
        n=2;
        ret = canPlaceFlowers.canPlaceFlowers(flowbed, n);
        assertTrue(!ret);
    }

}
