package leet.algo.test;

import leet.algo.SerDeserBinaryTree;

import org.junit.Test;

public class TestSerDeserBinaryTree {
	
	@Test
	public void test1(){
		SerDeserBinaryTree codec = new SerDeserBinaryTree();
		codec.serialize(codec.deserialize(""));
	}

}
