package algo.test.util;

import org.junit.Test;

import algo.util.StringUtil;

public class TestStringUtil {
	
	@Test
	public void testGetStringArrayArray(){
		String input = "[[AXA,EZE],[EZE,AUA],[ADL,JFK]]";
		String[][] ret = StringUtil.getStringArrayArray(input);
	}

}
