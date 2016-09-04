package leet.algo.test;

import java.util.List;

import leet.algo.GroupAnagrams;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestGroupAnagrams {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	
	@Test
	public void test1(){
		GroupAnagrams ga = new GroupAnagrams();
		List<List<String>> sll = ga.groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"});
		logger.info(sll);
	}

}
