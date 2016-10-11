package algo.util.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.ListNode;
import algo.util.ListNodeUtil;
import leet.algo.InsertionSortList;
import leet.algo.PartitionList;
import leet.algo.test.TestAdditiveNumber;

public class TestPartitionList {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		PartitionList pl = new PartitionList();
		ListNode ln = ListNodeUtil.getLN("1,4,3,2,5,2");
		ListNode ret = pl.partition(ln, 3);
		logger.info(ListNodeUtil.toString(ret));
	}
	
	@Test
	public void test2(){
		PartitionList pl = new PartitionList();
		ListNode ln = ListNodeUtil.getLN("2,1");
		ListNode ret = pl.partition(ln, 2);
		logger.info(ListNodeUtil.toString(ret));
	}
	
	@Test
	public void test3(){
		PartitionList pl = new PartitionList();
		ListNode ln = ListNodeUtil.getLN("2,1");
		ListNode ret = pl.partition(ln, 1);
		logger.info(ListNodeUtil.toString(ret));
	}

}
