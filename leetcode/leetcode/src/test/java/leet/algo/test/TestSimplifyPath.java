package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.SimplifyPath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestSimplifyPath {
	private static Logger logger =  LogManager.getLogger(TestSimplifyPath.class);
	@Test
	public void test1(){
		SimplifyPath sp = new SimplifyPath();
		String ret = sp.simplifyPath("/home/");
		logger.info(ret);
		assertTrue("/home".equals(ret));
	}
	
	@Test
	public void test2(){
		SimplifyPath sp = new SimplifyPath();
		String ret = sp.simplifyPath("/a/./b/../../c/");
		logger.info(ret);
		assertTrue("/c".equals(ret));
	}
	
	@Test
	public void test3(){
		SimplifyPath sp = new SimplifyPath();
		String ret = sp.simplifyPath("/../");
		logger.info(ret);
		assertTrue("/".equals(ret));
	}
	
	@Test
	public void test4(){
		SimplifyPath sp = new SimplifyPath();
		String ret = sp.simplifyPath("/home//foo/");
		logger.info(ret);
		assertTrue("/home/foo".equals(ret));
	}

}
