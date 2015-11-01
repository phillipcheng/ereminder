package org.cld.util.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.CombPermUtil;
import org.junit.Test;

public class TestCombPermUtil {
	public static final Logger logger = LogManager.getLogger(TestCombPermUtil.class);
	@Test
	public void testEachOne(){
		String[] a=new String[] {"a1","a2","a3"};
		String[] b = new String[]{"b1","b2"};
		String[] c = new String[]{"c1","c2","c3"};
		
		List<Object[]> ab = new ArrayList<Object[]>();
		ab.add(a);
		ab.add(b);
		ab.add(c);
		List<List<Object>> llo = CombPermUtil.eachOne(ab);
		logger.info(llo);
	}

}
