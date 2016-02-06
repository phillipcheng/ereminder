package algo.leet;

import java.util.Arrays;
import java.util.Stack;

//Given a string containing just the characters '(' and ')', 
//find the length of the longest valid (well-formed) parentheses substring.
public class LongestValidParentheses {
	
	public static boolean isDebug = false;
	
	public int longestValidParentheses(String s) {
		char[] input = s.toCharArray();
		
		
		Stack<Integer> stack = new Stack<Integer>();//idx of the char
		int[] validNum = new int[input.length];//idx: input idx, value:length of valid parentheses
		int[] parent = new int[input.length]; //idx: input idx, value: current parent parentheses idx
		int[] sister = new int[input.length];//idx: input idx, value: current sister parentheses idx
		for (int i=0; i<input.length; i++){
			parent[i]=-1;
			sister[i]=-1;
		}
		
		for (int i=0; i<input.length;i++){
			if (!stack.isEmpty()){
				int topIdx = stack.peek();
				if (input[i]=='('){//((
					if (input[topIdx]=='('){
						parent[i]=topIdx;
					}else{//)(
						//do nothing
					}
					stack.push(i);
				}else{//')'
					if(input[topIdx]=='('){//()
						sister[i]=topIdx;
						validNum[topIdx]+=2;
						if (parent[topIdx]!=-1){
							validNum[parent[topIdx]]+=validNum[topIdx];
						}
						int m = topIdx;
						while(sister[m]!=-1){
							m=sister[m];
							if (input[m]=='('){
								validNum[m]+=validNum[topIdx];
							}
						}
						stack.pop();
					}else{//))
						stack.push(i);
					}				
				}
			}else{
				stack.push(i);
			}
			if (i>=1 && input[i-1]==')' && input[i]=='('){
				sister[i]=i-1;
			}
		}
		
		if (isDebug){
			System.out.println("input" + Arrays.toString(input));
			System.out.println("parent" + Arrays.toString(parent));
			System.out.println("sister" + Arrays.toString(sister));
			System.out.println("validNum" + Arrays.toString(validNum));
		}
		//select the longest valid num from validNum Array
		int longest=0;
		for (int i=0; i<validNum.length; i++){
			if (validNum[i]>longest){
				longest=validNum[i];
			}
		}
        return longest;
    }

}
