package algo.util.test;

import java.util.BitSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.BitSetUtil;


public class TestBitSetUtil {
	private static Logger logger =  LogManager.getLogger(TestBitSetUtil.class);
	
	@Test
	public void test1(){
		BitSet bs = new BitSet(2);
		bs.set(1, true);
		bs.set(0, true);
		logger.info(String.format("bs:%s", bs));
		BitSet bs2 = BitSetUtil.shiftLeft(bs);
		logger.info(String.format("bs2:%s", bs2));
	}

}
