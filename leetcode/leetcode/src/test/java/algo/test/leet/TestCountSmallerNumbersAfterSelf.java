package algo.test.leet;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestCountSmallerNumbersAfterSelf {

	private static Logger logger =  LogManager.getLogger(TestCountSmallerNumbersAfterSelf.class);
	@Test
	public void test1(){
		CountSmallerNumbersAfterSelf csnas = new CountSmallerNumbersAfterSelf();
		List<Integer> ret = csnas.countSmaller(new int[]{5, 2, 6, 1});
		logger.info(ret);
	}

}
