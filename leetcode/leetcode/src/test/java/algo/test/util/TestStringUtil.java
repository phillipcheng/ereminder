package algo.test.util;

import org.junit.Test;

import algo.util.IOUtil;

public class TestStringUtil {
	
	@Test
	public void testGetStringArrayArray(){
		String input = "[[AXA,EZE],[EZE,AUA],[ADL,JFK]]";
		String[][] ret = IOUtil.getStringArrayArray(input);
	}

}
