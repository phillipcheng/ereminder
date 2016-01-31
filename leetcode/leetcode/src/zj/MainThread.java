package zj;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;


public class MainThread {
	
	public static int v;
	
	private static final AtomicInteger uniqueId = new AtomicInteger(0);
	private static final ThreadLocal < Integer > uniqueNum =
	         new ThreadLocal < Integer > () {
        		@Override protected Integer initialValue() {
        			return uniqueId.getAndIncrement();
        			//return 0;
        		}
	};
     
    public static int getTL(){
    	return uniqueNum.get().intValue();
    }
    
    public static void setTL(int v){
    	uniqueNum.set(v);
    }
    
    
			 
	public static int getV(){
		return v;
	}
	
	public static void setV(int v){
		MainThread.v = v;
	}
	
	public static void main(String arg[]) {
		long start = System.nanoTime();
		int size = 30;
		Thread[] tl = new Thread[size];
		for (int i=0; i<size; i++){
			tl[i] = new Thread(new MyRunnable(i));
			tl[i].start();
		}
		
		
		try {
			for (int i=0; i<size; i++){
				tl[i].join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("v:" + MainThread.v);
		System.out.println("u:" + MainThread.getTL());
		long end = System.nanoTime();
		System.out.println("elapse:" + (end - start));
		
	}

}
