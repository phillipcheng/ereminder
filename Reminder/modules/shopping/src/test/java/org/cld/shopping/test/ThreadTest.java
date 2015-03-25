package org.cld.shopping.test;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;


/*
 * stock quote
 */
public class ThreadTest {
	public static int THREAD_SIZE = 8;
	public static CountDownLatch COUNTDOWN = new CountDownLatch(THREAD_SIZE);
	public static ConcurrentLinkedQueue<String> allLinkQueue= new ConcurrentLinkedQueue<String>();
	
//	@Test
//	public void issue() throws Exception {
//		
//		while (true){
//			allLinkQueue.clear();
//		
//			for (int i=0; i<100; i++){
//				allLinkQueue.add("string" + i);
//			}
//			
//			Thread tlist[] = new Thread[THREAD_SIZE];
//			for (int i=0; i<THREAD_SIZE;i++){
//				Thread t = new ConsumeThread(i);
//				tlist[i]=t;
//				t.start();
//			}
//			
//			ThreadTest.COUNTDOWN.await();
//			System.out.println("countdown:" + ThreadTest.COUNTDOWN.getCount());
//		}
//	}
}
