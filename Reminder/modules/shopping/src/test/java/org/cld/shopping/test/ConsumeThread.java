package org.cld.shopping.test;

import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ConsumeThread extends Thread {
	private static Logger logger =  LogManager.getLogger(ConsumeThread.class);
	

	public int mark;
	
	public ConsumeThread(int mark){
		this.mark = mark;
	}
	
	public void run(){
		
		try{
			String url = null;
			try{
				while((url=ThreadTest.allLinkQueue.remove())!=null){
					//logger.info("thread:" + mark + " category finished:" + url);
				}				
			}catch(NoSuchElementException e){
				logger.info("book analyze thread " + mark + " finished successfully.");
				ThreadTest.COUNTDOWN.countDown();
				logger.info("countdown:" + ThreadTest.COUNTDOWN.getCount());
			}
		}catch(Throwable t){
			logger.error("book analyze thread " + mark + " got error, finished.", t);
		}finally{
			
		}
	}
}
