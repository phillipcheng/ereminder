package leet.algo.test;


import leet.algo.CopyListWithRandomPointer;
import leet.algo.RandomListNode;

import org.junit.Test;

public class TestCopyListWithRandomPointer {
	
	@Test
	public void test1(){
		RandomListNode rln = new RandomListNode(-1);
		CopyListWithRandomPointer cp = new CopyListWithRandomPointer();
		cp.copyRandomList(rln);
	}

}
