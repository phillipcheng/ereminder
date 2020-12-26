package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class Candy1 {

	private static Logger logger =  LogManager.getLogger(Candy1.class);

	private int[] candies;
	private int[] ratings;

	int getLeftRate(int idx){
		if (idx<=0) return ratings[0];
		else return ratings[idx-1];
	}

	int getRightRate(int idx){
		if (idx >= ratings.length-1){
			return ratings[ratings.length-1];
		}else{
			return ratings[idx+1];
		}
	}

	int getLeftCandy(int idx){
		if (idx<=0) return 0;
		else return candies[idx-1];
	}

	int getRightCandy(int idx){
		if (idx >= candies.length-1){
			return 0;
		}else{
			return candies[idx+1];
		}
	}
	
	public int candy(int[] ratings) {
		logger.info(Arrays.toString(ratings));
		if (ratings.length==0){
			return 0;
		}
		this.ratings = ratings;
		this.candies = new int[ratings.length];//default to 0

		SortedMap<Integer, List<Integer>> sm = new TreeMap<>();//key: rate, value: index
		for (int idx=0; idx<ratings.length; idx++){
			int rate = ratings[idx];
			if (sm.containsKey(rate)){
				sm.get(rate).add(idx);
			}else{
				List<Integer> l = new ArrayList<>();
				l.add(idx);
				sm.put(rate, l);
			}
		}
		Iterator<Integer> it = sm.keySet().iterator();
		while (it.hasNext()){//rate from small to big
			int rate = it.next();
			List<Integer> poses = sm.get(rate);
			for (Integer pos: poses){
				int leftRate = getLeftRate(pos);
				int rightRate = getRightRate(pos);
				int leftCandy = getLeftCandy(pos);
				int rightCandy = getRightCandy(pos);

				int candidate1 = 1;
				int candidate2 = 1;
				if (rate>leftRate){
					candidate1 = leftCandy +1;
				}
				if (rate>rightRate){
					candidate2 = rightCandy +1;
				}

				int candy = Math.max(candidate1, candidate2);

				candies[pos]=candy;
			}
		}

		logger.info(Arrays.toString(candies));

        int num =0;
        for (int i=0; i<candies.length; i++){
        	num+=candies[i];
        }
        return num;
    }

    public static void main(String[] args){
		Candy1 c = new Candy1();
		int[] ratings;
		int score;


		ratings = new int[]{1,1};
		Assert.assertTrue(2==c.candy(ratings));

		ratings = new int[]{1,2};
		Assert.assertTrue(3==c.candy(ratings));

		ratings = new int[]{2,1};
		Assert.assertTrue(3==c.candy(ratings));

		ratings = new int[]{1001};
		Assert.assertTrue(1==c.candy(ratings));

		ratings = new int[]{};
		Assert.assertTrue(0==c.candy(ratings));

		ratings = new int[]{1,2,1};
		Assert.assertTrue(4==c.candy(ratings));

		ratings = new int[]{1,2,2};
		Assert.assertTrue(4 == c.candy(ratings));


		ratings = new int[]{29,51,87,87,72,12};
		Assert.assertTrue(12 == c.candy(ratings));
	}

}
