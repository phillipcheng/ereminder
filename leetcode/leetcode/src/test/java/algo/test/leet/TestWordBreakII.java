package algo.test.leet;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

import algo.leet.WordBreakII;
import algo.util.StringUtil;

public class TestWordBreakII {

	@Test
	public void test1() {
		WordBreakII wbi = new WordBreakII();
		HashSet<String> dict = StringUtil.getHashSet("cat, cats, and, sand, dog");
		ArrayList<String> al = wbi.wordBreak("catsanddog", dict);
		System.out.println(al);
		
	}
	
	
	@Test
	public void test3() {
		WordBreakII wbi = new WordBreakII();
		HashSet<String> dict = StringUtil.getHashSet("aa,a");
		ArrayList<String> al = wbi.wordBreak("aa", dict);
		for (int i=0; i<al.size(); i++){
			System.out.println(al.get(i));
		}
		if (al!=null){
			System.out.println(al.size());
		}
		
	}
	
	@Test
	public void test2() {
		WordBreakII wbi = new WordBreakII();
		HashSet<String> dict = StringUtil.getHashSet("a, aa, aaa, aaaa, aaaaa, aaaaaa, aaaaaaa, aaaaaaaa, aaaaaaaaa");
		ArrayList<String> al = wbi.wordBreak("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", dict);
		//for (int i=0; i<al.size(); i++){
			//System.out.println(al.get(i));
		//}
		System.out.println(al.size());
		
	}

}
