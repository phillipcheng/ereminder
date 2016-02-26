package algo.test.util;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.PermUtil;

public class TestPermUtil {
	private static Logger logger =  LogManager.getLogger(TestPermUtil.class);
	
	@Test
	public void testPerm1(){
		List<BitSet> bsl2 = PermUtil.choose(3, 2);
		logger.info(String.format("size:%d, res2:%s", bsl2.size(), bsl2));
	}
	
	@Test
	public void testPerm2(){
		List<BitSet> bsl2 = PermUtil.choose(24, 2);
		List<BitSet> bsl1 = PermUtil.choose(24, 1);
		logger.info(String.format("size:%d, res2:%s", bsl2.size(), bsl2));
		logger.info(String.format("size:%d, res1:%s", bsl1.size(), bsl1));
	}
	
	@Test
	public void testPerm3(){
		List<BitSet> bsl2 = PermUtil.choose(25, 12);
		logger.info(String.format("size:%d", bsl2.size()));
	}
	
	@Test
	public void testPerm4(){
		List<BitSet> bsl2 = PermUtil.choose(25, 24);
		logger.info(String.format("size:%d", bsl2.size()));
	}
}
