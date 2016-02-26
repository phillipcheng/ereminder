package leet.algo.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.IOUtil;
import leet.algo.ReconstructItinerary;

public class TestReconstructItinerary {

	private static Logger logger =  LogManager.getLogger(TestReconstructItinerary.class);
	@Test
	public void test1(){
		ReconstructItinerary ri = new ReconstructItinerary();
		List<String> ret = ri.findItinerary(new String[][]{new String[]{"MUC", "LHR"}, new String[]{"JFK", "MUC"}, new String[]{"SFO", "SJC"}, new String[]{"LHR", "SFO"}});
		String[] expected = new String[]{"JFK", "MUC", "LHR", "SFO", "SJC"};
		String[] aret = new String[ret.size()];
		ret.toArray(aret);
		assertTrue(Arrays.equals(aret, expected));
	}
	
	@Test
	public void test2(){
		ReconstructItinerary ri = new ReconstructItinerary();
		List<String> ret = ri.findItinerary(new String[][]{new String[]{"JFK", "SFO"}, new String[]{"JFK", "ATL"}, new String[]{"SFO", "ATL"}, new String[]{"ATL", "JFK"}, new String[]{"ATL", "SFO"}});
		String[] expected = new String[]{"JFK","ATL","JFK","SFO","ATL","SFO"};
		String[] aret = new String[ret.size()];
		ret.toArray(aret);
		assertTrue(Arrays.equals(aret, expected));
	}
	
	@Test
	public void test3(){
		ReconstructItinerary ri = new ReconstructItinerary();
		String input = "[[JFK,a],[a,JFK],[JFK,b],[b,d],[d,a],[a,e]]";
		List<String> ret = ri.findItinerary(IOUtil.getStringArrayArray(input));
		String[] expected = new String[]{"JFK","a", "JFK","b","d","a","e"};
		String[] aret = new String[ret.size()];
		ret.toArray(aret);
		assertTrue(Arrays.equals(aret, expected));
	}
	
	@Test
	public void test4(){
		ReconstructItinerary ri = new ReconstructItinerary();
		String input = "[[AXA,EZE],[EZE,AUA],[ADL,JFK],[ADL,TIA],[AUA,AXA],[EZE,TIA],[EZE,TIA],[AXA,EZE],[EZE,ADL],[ANU,EZE],[TIA,EZE],[JFK,ADL],[AUA,JFK],[JFK,EZE],[EZE,ANU],[ADL,AUA],[ANU,AXA],[AXA,ADL],[AUA,JFK],[EZE,ADL],[ANU,TIA],[AUA,JFK],[TIA,JFK],[EZE,AUA],[AXA,EZE],[AUA,ANU],[ADL,AXA],[EZE,ADL],[AUA,ANU],[AXA,EZE],[TIA,AUA],[AXA,EZE],[AUA,SYD],[ADL,JFK],[EZE,AUA],[ADL,ANU],[AUA,TIA],[ADL,EZE],[TIA,JFK],[AXA,ANU],[JFK,AXA],[JFK,ADL],[ADL,EZE],[AXA,TIA],[JFK,AUA],[ADL,EZE],[JFK,ADL],[ADL,AXA],[TIA,AUA],[AXA,JFK],[ADL,AUA],[TIA,JFK],[JFK,ADL],[JFK,ADL],[ANU,AXA],[TIA,AXA],[EZE,JFK],[EZE,AXA],[ADL,TIA],[JFK,AUA],[TIA,EZE],[EZE,ADL],[JFK,ANU],[TIA,AUA],[EZE,ADL],[ADL,JFK],[ANU,AXA],[AUA,AXA],[ANU,EZE],[ADL,AXA],[ANU,AXA],[TIA,ADL],[JFK,ADL],[JFK,TIA],[AUA,ADL],[AUA,TIA],[TIA,JFK],[EZE,JFK],[AUA,ADL],[ADL,AUA],[EZE,ANU],[ADL,ANU],[AUA,AXA],[AXA,TIA],[AXA,TIA],[ADL,AXA],[EZE,AXA],[AXA,JFK],[JFK,AUA],[ANU,ADL],[AXA,TIA],[ANU,AUA],[JFK,EZE],[AXA,ADL],[TIA,EZE],[JFK,AXA],[AXA,ADL],[EZE,AUA],[AXA,ANU],[ADL,EZE],[AUA,EZE]]";
		List<String> ret = ri.findItinerary(IOUtil.getStringArrayArray(input));
		logger.info(ret);
	}
	
	@Test
	public void test5(){
		ReconstructItinerary ri = new ReconstructItinerary();
		String input = "[[EZE,AXA],[TIA,ANU],[ANU,JFK],[JFK,ANU],[ANU,EZE],[TIA,ANU],[AXA,TIA],[TIA,JFK],[ANU,TIA],[JFK,TIA]]";
		List<String> ret = ri.findItinerary(IOUtil.getStringArrayArray(input));
		String[] expected = new String[]{"JFK","ANU","EZE","AXA","TIA","ANU","JFK","TIA","ANU","TIA","JFK"};
		String[] aret = new String[ret.size()];
		ret.toArray(aret);
		assertTrue(Arrays.equals(aret, expected));
		logger.info(ret);		
	}
}
