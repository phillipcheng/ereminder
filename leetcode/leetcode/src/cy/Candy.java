package cy;

import java.util.Arrays;


//There are N children standing in a line. Each child is assigned a rating value.

//You are giving candies to these children subjected to the following requirements:

//Each child must have at least one candy.
//Children with a higher rating get more candies than their neighbors.
//What is the minimum candies you must give?
public class Candy {
	public static boolean isDebug = false;
	
	private int[] candies;
	private int[] ratings;
	
	public void giveCandy(int startIdx, int toIdx){
		if (startIdx + 1 == toIdx){
			candies[startIdx] = 1;
		}else{
			int mid = (startIdx + toIdx)/2;
			giveCandy(startIdx, mid);
			giveCandy(mid, toIdx);
			
			//this part is less
			for (int i=mid; i>0; i--){
				if (candies[i-1]<=candies[i] && ratings[i-1]>ratings[i]){
					//raise left part
					candies[i-1]=candies[i]+1;
				}else{
					break;
				}
			}
			
			for (int i=mid; i<toIdx; i++){
				if (candies[i-1]>=candies[i] && ratings[i-1]<ratings[i]){
					//raise right part
					candies[i]=candies[i-1]+1;
				}else{
					break;
				}
			}
		}
		if (isDebug){
			System.out.println(Arrays.toString(candies));
		}
	}
	
	public int candy(int[] ratings) {
		if (isDebug){
			System.out.println(Arrays.toString(ratings));
		}
		if (ratings.length==0){
			return 0;
		}
		this.ratings = ratings;
		this.candies = new int[ratings.length];
        giveCandy(0, ratings.length);
        int num =0;
        for (int i=0; i<candies.length; i++){
        	num+=candies[i];
        }
        return num;
    }

}
