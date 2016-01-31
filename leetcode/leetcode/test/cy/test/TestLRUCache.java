package cy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.LRUCache;

public class TestLRUCache {

	@Test
	public void test1() {
		LRUCache lru = new LRUCache(10);
		lru.set(1, 1);
		assertTrue(1 == lru.get(1));
	}
	
	@Test
	public void test2() {
		LRUCache lru = new LRUCache(10);
		lru.set(1, 1);
		lru.set(1, 2);
		assertTrue(2 == lru.get(1));
	}
	
	@Test
	public void test3() {
		LRUCache lru = new LRUCache(2);
		lru.set(1, 1);
		lru.set(2, 2);
		lru.set(3, 3);
		assertTrue(-1 == lru.get(1));
	}
	
	@Test
	public void test4() {
		LRUCache lru = new LRUCache(2);
		lru.set(1, 1);
		System.out.println(lru.toString());
		lru.set(2, 2);
		System.out.println(lru.toString());
		lru.set(1, 1);
		System.out.println(lru.toString());
		lru.set(3, 3);
		System.out.println(lru.toString());
		assertTrue(1 == lru.get(1));
		assertTrue(-1 == lru.get(2));
	}
	
	@Test
	public void test5() {
		LRUCache lru = new LRUCache(2);
		lru.set(1, 1);
		lru.set(2, 2);
		lru.get(1);
		lru.set(3, 3);
		assertTrue(1 == lru.get(1));
		assertTrue(-1 == lru.get(2));
	}

	@Test
	public void test6() {
		LRUCache lru = new LRUCache(2);
		lru.set(1, 7);
		lru.set(2, 8);
		lru.get(1);
		lru.set(2, 10);
		lru.set(3, 9);
		assertTrue(-1 == lru.get(1));
		assertTrue(10 == lru.get(2));
	}
	
	@Test
	public void test7() {
		LRUCache lru = new LRUCache(1);
		lru.set(2, 1);
		System.out.println(lru.toString());
		assertTrue(1 == lru.get(2));
		System.out.println(lru.toString());
		lru.set(3, 2);
		System.out.println(lru.toString());
		assertTrue(-1 == lru.get(2));
		System.out.println(lru.toString());
		assertTrue(2 == lru.get(3));
		System.out.println(lru.toString());
	}

}
