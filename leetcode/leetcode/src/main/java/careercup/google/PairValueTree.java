package careercup.google;

public class PairValueTree {
	
	/*
	Given an arbitrary tree starting at “root” where each node contains a pair of values (x, y), 
	write a boolean function find(Node root, int x, int y) that returns true iff 
	* x is equal to a value "x" of any node n1 in the tree 
	* and y is equal to a value "y" of any node n2 in the tree 
	* and both n1 and n2 are at the same level in the tree 

	boolean find(Node root, int x, int y) 

	Example: 

			(1,120) 
			/ | \ 
			/ | \ 
			/ | \ 
	(5,15) (30,70) 			(80,110) 
			/ | \ 				| 
			/ | \ 				| 
			/ | \ 				| 
	(35, 40) (45,50) (55, 65) (90, 100) 

	find(root, 45, 100) == true 
	find(root, 30, 100) == false 
	find(root, 30, 70) == true 
	find(root, 70, 30) == false
	*/

}
