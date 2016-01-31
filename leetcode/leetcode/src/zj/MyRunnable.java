package zj;

public class MyRunnable implements Runnable {
	long id;
	
	public MyRunnable(long id) {
		this.id=id;
	}
	
	@Override
	public void run() {
		int count = 0;
		while(count++<10000){
			//System.out.println("Runnable"+id);
			synchronized(MainThread.class)
			{
				int v = MainThread.getV()+1;
				MainThread.setV(v);
			}
			
			int u = MainThread.getTL()+1;
			MainThread.setTL(u);
			
			
		}
		
		System.out.println("V:" + MainThread.getV());
		System.out.println("u:" + MainThread.getTL());
		
	}

}
