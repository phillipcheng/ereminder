package algo.leet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

 //Given n points on a 2D plane, find the maximum number of points that lie on the same straight line.  
class Point {
    int x;
    int y;
    
    Point() { x = 0; y = 0; }
    
    public Point(int a, int b) { 
   	 x = a; y = b;
    }; 
    
    public String toString(){
   	 return x + "," + y;
    }
}

public class SameLine {
	
	//
	HashMap<Integer, ArrayList<Point>> lineGroups = new HashMap<Integer, ArrayList<Point>>();
	//
	HashMap<String, Integer> dupPointNum = new HashMap<String, Integer>();
	
	public boolean sameline (Point a, Point b, Point c){
		int area = (b.x - a.x)*(c.y-a.y) - (b.y-a.y)*(c.x-a.x);
		if (area == 0)
			return true;
		else
			return false;
	}
	
	//(-16,11),(-7,84),(1,76),(3,77)	
	public static Point[] getPoints(String s){
		ArrayList<Point> pl =new ArrayList<Point>();
		StringTokenizer st = new StringTokenizer(s, "(), ", false);
		int a=0, b;
		int num=0;
		while (st.hasMoreTokens()){
			int i = Integer.parseInt(st.nextToken());
			num++;
			if (num % 2 ==1){
				a = i;
			}else{
				b = i;
				pl.add(new Point(a,b));
			}
		}
		System.out.println("# of token:" + num);
		System.out.println("# of items in list:" + pl.size());
		
		Point[] pa = new Point[pl.size()];
		return pl.toArray(pa);
	}
	
	public void printLineGroups(HashMap<Integer, ArrayList<Point>> lg){
		Iterator<Integer> it = lg.keySet().iterator();
		while (it.hasNext()){
			int k = it.next();
			ArrayList<Point> pl = lg.get(k);
			System.out.println(k+":size:" + pl.size() + ":" + pl);			
		}
	}
    
	public String pointToString(Point p){
		return p.x + ":" + p.y;
	}
	
	public int maxPoints(String input){
		Point[] points = getPoints(input);
		return maxPoints(points);
	}
	
	public int maxPoints(Point[] points) {
		if (points.length==0){
			return 0;
		}
		
		ArrayList<Point> upl = new ArrayList<Point>();
		for (int z =0; z<points.length; z++){
			String key = pointToString(points[z]);
			if (dupPointNum.containsKey(key)){
				int val = dupPointNum.get(key);
				val++;
				dupPointNum.put(key, val);
			}else{
				dupPointNum.put(key, 1);
				upl.add(points[z]);
			}
		}
		
		Point[] uniquePoints = new Point[upl.size()];
		upl.toArray(uniquePoints);
		
    	if (uniquePoints.length==1){
    		return dupPointNum.get(pointToString(uniquePoints[0]));
    	}
    	
    	int nGroups = 0;
    	ArrayList<Point> al = new ArrayList<Point>();
    	al.add(uniquePoints[0]);
    	al.add(uniquePoints[1]);
    	lineGroups.put(nGroups, al);
    	nGroups++;
    	
    	int nPoints = 2;
    	
    	for (int j=nPoints; j<uniquePoints.length; j++){
    		Point c = uniquePoints[j];
    		boolean foundGroup = false;
	    	for (int i =0; i<nGroups; i++){
	    		//test which group c belongs
	    		ArrayList<Point> pl = lineGroups.get(new Integer(i));
	    		if (pl != null){
		    		Point a = pl.get(0);
		    		Point b = pl.get(1);
		    		if (sameline(a,b,c)){
		    			//add to this group
		    			pl.add(c);
		    			lineGroups.put(new Integer(i), pl);
		    			foundGroup = true;
		    			break;
		    		}
	    		}else{
	    			System.out.println("pl not exist for group:" + i);
	    		}
	    	}
	    	if (!foundGroup){
	    		//create new groups for 0..j-1 with j
	    		for (int k=0; k<j;k++){
	    			ArrayList<Point> pl = new ArrayList<Point>();
	    			pl.add(uniquePoints[k]);
	    			pl.add(uniquePoints[j]);
	    			lineGroups.put(new Integer(nGroups+k), pl);
	    		}
	    		nGroups +=j;
	    	}
    	}
    	
    	//printLineGroups(lineGroups);
    	
    	int[] numbers = new int[lineGroups.size()];
    	for (int p=0; p<numbers.length; p++){
    		ArrayList<Point> alp = lineGroups.get(new Integer(p));
    		numbers[p] = 0;
    		for (int y=0; y<alp.size();y++){
    			String key = pointToString(alp.get(y));
    			numbers[p]+= dupPointNum.get(key);
    		}
    	}
    	
    	Arrays.sort(numbers);
    	
    	return numbers[numbers.length-1];
    }

}
