package stanford.algo2.week3;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class Knapsack {
	private static Logger logger =  LogManager.getLogger(Knapsack.class);
	
	int capacity;//
	int ni; //number of items
	int[] values;
	int[] weights;
	Map<Integer, Long>[] v;//
	
	
	public Knapsack(int ni, int capacity){
		this.capacity = capacity;
		this.ni = ni;
		values = new int[ni];
		weights = new int[ni];
		
		//for recursion max value
		v = new Map[ni+1];
	}
	
	public void addItem(int idx, int value, int weight){
		values[idx] = value;
		weights[idx] = weight;
	}
	
	public long maxValueDP(){
		long[][] v = new long[ni+1][capacity+1];//pre-ni, capacity
		for (int i=0;i<=capacity;i++){
			v[0][i]=0;
		}
		for (int i=1; i<=ni; i++){
			for (int c=0; c<=capacity; c++){
				if (weights[i-1]>c){
					v[i][c] = v[i-1][c];
				}else{
					v[i][c] = Math.max(v[i-1][c], values[i-1]+v[i-1][c-weights[i-1]]);
				}
				logger.info(String.format("v[%d][%d] = %d", i, c, v[i][c]));
			}
		}
		return v[ni][capacity];
	}
	
	public long maxValue(int prei, int cap){
		if (prei==0)
			return 0;
		if (v[prei-1]!=null){
			if (v[prei-1].get(cap)!=null){
				return v[prei-1].get(cap);
			}
		}
		long l=0;
		if(weights[prei-1]>cap){
			l = maxValue(prei-1, cap);
		}else{
			l = Math.max(maxValue(prei-1, cap), values[prei-1] + maxValue(prei-1, cap-weights[prei-1]));
		}
		Map<Integer, Long> vw;
		if (v[prei-1]==null){
			vw = new HashMap<Integer, Long>();
			v[prei-1] = vw;
		}else{
			vw = v[prei-1];
		}
		vw.put(cap, l);
		return l;
	}
	
	public long maxValueRecursion(){
		return maxValue(ni, capacity);
	}
	
	public static Knapsack createKnapsack(InputStream is){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			String[] wl = line.split(" ");
			int capacity = Integer.parseInt(wl[0]);
			int ni = Integer.parseInt(wl[1]);
			Knapsack ks = new Knapsack(ni, capacity);
			logger.info(String.format("capacity: %d, #item: %d", capacity, ni));
			while (--ni>=0){
				line = br.readLine();
				wl = line.split(" ");
				int value = Integer.parseInt(wl[0]);
				int weight = Integer.parseInt(wl[1]);
				ks.addItem(ni, value, weight);
			}
			br.close();
			return ks;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}

}
