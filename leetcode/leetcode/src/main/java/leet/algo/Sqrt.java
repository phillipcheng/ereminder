package leet.algo;

public class Sqrt {
	
	public int mySqrt(int x) {
		long min = 0;
        long max = x;
        boolean finish=false;
        long t = 0;
        while (!finish){
	        t = (min+max)/2;
	        if ((t+1)*(t+1)<=x){
	        	min = t+1;
	        }else if (x<t*t){
	        	max = t;
	        }else{
	        	finish=true;
	        }
        }
        return (int)t;
    }

}
