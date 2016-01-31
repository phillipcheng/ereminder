package cy.util.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.util.UnionFind;

public class TestUnionFind {

	@Test
	public void test0() {
		UnionFind uf = new UnionFind(10);
		uf.union(2,3);
		uf.union(3, 6);
		uf.union(1, 4);
		uf.union(4, 7);
		assertTrue(uf.isConnected(2, 6));
		assertTrue(uf.isConnected(1, 7));
		uf.union(1, 2);
		assertTrue(uf.isConnected(3, 7));		
	}
	
	@Test
	public void test1() {
		UnionFind uf = new UnionFind(2);
		uf.union(0,1);
		System.out.println(uf.toString());
		assertTrue(uf.isConnected(0, 1));
	}
	
	@Test
	public void test2() {
		UnionFind uf = new UnionFind(3);
		uf.union(0,1);
		uf.union(1,2);
		
		System.out.println(uf.toString());
		assertTrue(uf.isConnected(0, 2));
	}
	
	@Test
	public void test3() {
		UnionFind uf = new UnionFind(5);
		uf.union(0,1);
		uf.union(1,2);
		uf.union(2,3);
		uf.union(3,4);
		
		System.out.println(uf.toString());
		assertTrue(uf.isConnected(0, 4));
	}

}
