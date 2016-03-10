package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.JumpGame;

import org.junit.Test;

public class TestJumpGame {
	
	@Test
	public void test0(){
		JumpGame jg = new JumpGame();
		assertTrue(jg.canJump(new int[]{0}));
	}
	
	@Test
	public void test1(){
		JumpGame jg = new JumpGame();
		assertTrue(jg.canJump(new int[]{2,3,1,1,4}));
	}
	
	@Test
	public void test2(){
		JumpGame jg = new JumpGame();
		assertFalse(jg.canJump(new int[]{3,2,1,0,4}));
	}
	
	@Test
	public void test3(){
		JumpGame jg = new JumpGame();
		assertTrue(jg.canJump(new int[]{2,0,0}));
	}
	
	@Test
	public void test4(){
		JumpGame jg = new JumpGame();
		assertFalse(jg.canJump(new int[]{1,0,1,0}));
	}
	
	@Test
	public void test20(){
		JumpGame jg = new JumpGame();
		assertTrue(2==jg.jump(new int[]{2,3,1,1,4}));
	}

}
