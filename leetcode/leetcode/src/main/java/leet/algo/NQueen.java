package leet.algo;

import java.util.ArrayList;
import java.util.Arrays;

public class NQueen {
	public static boolean isDebug = false;
	
	//b[i] is the place for the i-th row queen
	public String[] getBoard(int b[]){
		String[] ret = new String[b.length];
		for (int i=0; i<b.length; i++){
			String output = "";
			for (int j=0; j<b.length; j++){
				if (j==b[i]){
					output+="Q";
				}else{
					output+=".";
				}
			}
			ret[i]=output;
		}
		return ret;		
	}
	
	//assuming b[0]..b[i-1] are okay, test adding b[i]
	public boolean test(int[] b, int i){
		for (int j=0; j<=i-1; j++){
			if (b[i] == b[j]){
				return false;
			}
			if (Math.abs(i-j)==Math.abs(b[i]-b[j])){
				return false;
			}
		}
		return true;
	}
	
    public ArrayList<String[]> solveNQueens(int n) {
    	ArrayList<String[]> ret = new ArrayList<String[]>();
    	
    	int b[] = new int[n];
    	
    	//initialize
    	for (int j=0; j<n; j++)
    		b[j]=-1;
    	
    	int i = 0;
    	while (i < n && i>=0){
    		//try or retry i-th row
    		if (b[i]==-1){
    			//1st time
    			b[i]=0;    	
        		if (test(b, i)){
        			i++;
        		}
    		}else if (b[i]==n-1){
    			b[i]=-1;
    			i--;
    		}else{
    			b[i]=b[i]+1;
        		if (test(b, i)){
        			i++;
        		}
    		}
    		if (i==n){
    			String[] sa = getBoard(b);
    			if (isDebug){
    				System.out.println(Arrays.toString(sa));
    			}
    			ret.add(sa);
    			
    			i--;
    		}
    	}
    	
        return ret;
    }
    
    public int totalNQueens(int n){
    	ArrayList<String[]> ret = solveNQueens(n);
    	return ret.size();
    }
}
