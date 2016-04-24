package leet.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SkyLineProblem {
	private static Logger logger =  LogManager.getLogger(SkyLineProblem.class);
	
	public List<int[]> getSkyline(int[][] buildings) {
		TreeMap<Integer, Integer> hm = new TreeMap<Integer, Integer>();
		for (int i=0; i<buildings.length; i++){
			int[] bd = buildings[i];
			int x = bd[0];
			int y = bd[1];
			int h = bd[2];
			//
			int maxxh = h;
			for (int j=i-1; j>=0; j--){
				int[] pbd = buildings[j];
				int px = pbd[0];
				int py = pbd[1];
				int ph = pbd[2];
				if (x<py && x>=px){//max h of all x has crossing or starting line
					if (ph>maxxh){
						maxxh = ph;
					}
				}
			}
			//logger.info(String.format("maxh for x:%d is %d", x, maxxh));
			hm.put(x, maxxh);
			//
			int maxyh = 0;
			for (int j=0; j<buildings.length; j++){
				if (j!=i){
					int[] pbd = buildings[j];
					int px = pbd[0];
					int py = pbd[1];
					int ph = pbd[2];
					if (y<py && y>=px){//max h of all y has crossing or starting line
						if (ph>maxyh){
							maxyh = ph;
						}
					}
				}
			}
			hm.put(y, maxyh);
			//logger.info(String.format("maxh for y:%d is %d", y, maxyh));
		}
        List<int[]> output = new ArrayList<int[]>();
        int preh=-1;
        for (int x: hm.keySet()){
        	int h = hm.get(x);
        	if (h!=preh){
        		output.add(new int[]{x, h});
        	}
        	preh = h;
        }
        return output;
    }
}
