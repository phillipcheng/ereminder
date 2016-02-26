package leet.algo;

import java.util.Iterator;
import java.util.TreeMap;

/*
 Given n non-negative integers a1, a2, ..., an, where each represents a point at coordinate (i, ai). 
 n vertical lines are drawn such that the two end points of line i is at (i, ai) and (i, 0). 
 Find two lines, which together with x-axis forms a container, such that the container contains the most water.

 Note: You may not slant the container.
 */
class MinMax{
	int min;
	int max;
	
	MinMax(int min, int max){
		this.min = min;
		this.max = max;
	}
}

public class ContainerWithMostWater {
	public static final boolean isDebug = false;
	void log(Object s){
		if (isDebug){
			System.out.println(s);
		}
	}
	public int min(int a, int b){
		if (a<=b){
			return a;
		}else{
			return b;
		}
	}
	
	public int max(int a, int b){
		if (a>=b){
			return a;
		}else{
			return b;
		}
	}
	
	public int maxAreaNSquare(int[] height) {
		//find max( (i-j)*(min(ai,aj)) )
		int maxArea=0;
		
		int area=0;
		for (int i=0;i<height.length;i++){
			for (int j=i+1; j<height.length; j++){
				area = (j-i)*min(height[i], height[j]);
				if (maxArea<area){
					maxArea = area;
				}
			}
		}
		
        return maxArea;
    }
	
	public int maxAreaNLgN(int[] height) {
		long start, end1, end;
	
		if (isDebug) {
			start = System.nanoTime();
		}
		//using TreeMap to add line by height, since added in order, so the values are in order
		TreeMap<Integer, MinMax> tm = new TreeMap<Integer, MinMax>();
		for (int i=0; i<height.length;i++){
			MinMax mm = tm.get(height[i]);
			if (mm!=null){
				if (mm.max<i){
					mm.max=i;
				}
				if (mm.min>i){
					mm.min=i;
				}
			}else{
				mm = new MinMax(i,i);
				tm.put(height[i], mm);
			}
		}
		
		if (isDebug) {
			end1 = System.nanoTime();
			System.out.println("time for sort:" + (end1-start));
		}
		
		
		Iterator<Integer> it = tm.descendingKeySet().iterator();
		int preMin=0, min=0;
		int preMax=0, max=0;
		int maxArea=0;
		int area=0;
		int count=0;
		int fi=0;
		int fj=0;
		while (it.hasNext()){
			int h = it.next();
			MinMax l = tm.get(h);
			min = l.min;
			max=l.max;
			if (count==0){
				//1st time
				area = (max-min)*h;
				maxArea = area;
			}else{
				max = max(max, preMax);
				min = min(min, preMin);
				area = (max - min) * h;
				
				if (area>maxArea){
					maxArea = area;
					if (isDebug) {
						fi = min;
						fj = max;
					}
				}
			}
			
			preMin=min;
			preMax=max;
			count++;
		}
		
		if (isDebug) {
			end = System.nanoTime();
			System.out.println("time:" + (end-start) + ", area:" + area);
			log(fi+"," +fj);
		}
		
		return maxArea;
    }
	
	public int maxArea(int[] height) {
		return maxAreaNLgN(height);
	}

}
