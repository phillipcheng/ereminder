package algo.leet;

import java.util.ArrayList;


//Given a triangle, find the minimum path sum from top to bottom. 
//Each step you may move to adjacent numbers on the row below.
public class Triangle {
	
	public static int min(int a, int b){
		if (a<b)
			return a;
		else 
			return b;
	}
	
	public ArrayList<Integer> buildMinimum(ArrayList<ArrayList<Integer>> triangle){
		ArrayList<Integer> al = new ArrayList<Integer>();
		if (triangle.size()==1){
			al.add(triangle.get(0).get(0));
			return al;
		}else{
			//take the last row
			ArrayList<Integer> lastRow = triangle.remove(triangle.size()-1);
			ArrayList<Integer> minSum = buildMinimum(triangle);
			for (int i=0; i<lastRow.size(); i++){
				if (i-1>=0 && i<minSum.size()){
					al.add(min(minSum.get(i-1), minSum.get(i))+lastRow.get(i));
				}else if (i-1<0){
					al.add(minSum.get(i)+lastRow.get(i));
				}else{//i>=minSum.size()
					al.add(minSum.get(i-1)+lastRow.get(i));
				}
			}
			return al;
		}
	}
	
	public int minimumTotal(ArrayList<ArrayList<Integer>> triangle) {
		ArrayList<Integer> al = buildMinimum(triangle);
		int min = Integer.MAX_VALUE;
		for (int i=0; i<al.size(); i++){
			if (min>al.get(i)){
				min=al.get(i);
			}
		}
		return min;
    }
}
