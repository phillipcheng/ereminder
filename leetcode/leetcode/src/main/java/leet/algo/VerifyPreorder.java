package leet.algo;

import java.util.Arrays;

//Given a string of comma separated values, verify whether it is a correct preorder traversal serialization of a binary tree. 
//Find an algorithm without reconstructing the tree.
public class VerifyPreorder {
	
	//
	//return: up to which index input is matched, -1 for failed
	public int matchPreorder(String[] input, int start, int end){
		//System.err.println(String.format("match preorder %s %d,%d", Arrays.asList(Arrays.copyOfRange(input, start, end+1)), start, end));
		if (start>=end){//only 1 element input
			if ("#".equals(input[start])){
				return start;
			}else{
				return -1;
			}
		}else {
			if ("#".equals(input[start])){
				return start;
			}else{
				//a x x
				int idx = matchPreorder(input, start+1, end);
				if (idx==-1){
					return -1;
				}else{
					if (idx>=end){
						return -1;
					}else{
						return matchPreorder(input, idx+1, end);
					}
				}
			}
		}
	}
	//TODO #sentinel = #non-setinel + 1
	public boolean isValidSerialization(String preorder) {
		String[] input = preorder.split(",");
		int idx = matchPreorder(input, 0, input.length-1);
		if (idx == -1)
			return false;
		else{
			if (idx ==input.length-1){
				return true;
			}else{
				return false;
			}
		}
    }
}
