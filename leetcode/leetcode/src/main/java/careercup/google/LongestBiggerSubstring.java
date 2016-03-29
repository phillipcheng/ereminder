package careercup.google;

import algo.string.KMP;

public class LongestBiggerSubstring {
	
	/*
	 Given a string S, print the longest substring P such that P > S lexicographically. 
	 You may assume that such substring exists.
	 */
	
	public String getLongestBiggerSubstring2(String input){//O(n^2)
		int f=1; //the start idx of the substring
		int n = input.length();
		int i=0; //the current idx to compare
		while (f<n){
			char ch = input.charAt(i);//ch of S
			char fch = input.charAt(f+i);//ch of P
			if (fch>ch){
				//found the max
				return input.substring(f,n);
			}else if (fch<ch){
				f++;
				i=0;
			}else{
				i++;
			}
		}
		if (i>0){
			return input.substring(f, f+i+1);
		}else{
			return "";
		}
	}
	
	public String getLongestBiggerSubstring(String input){//O(n) using KMP
		int f=1; //the start idx of the substring
		int n = input.length();
		int i=0; //the current idx to compare
		int[] ff = KMP.failureFunction(input);
		while (f<n){
			char ch = input.charAt(i);//ch of S
			char fch = input.charAt(f+i);//ch of P
			if (fch>ch){
				//found the max
				return input.substring(f,n);
			}else if (fch<ch){
				f += ff[i]+1;
				i=0;
			}else{
				i++;
			}
		}
		if (i>0){
			return input.substring(f, f+i+1);
		}else{
			return "";
		}
	}

}
