package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Given a string S, you are allowed to convert it to a palindrome by adding characters in front of it. 
//Find and return the shortest palindrome you can find by performing this transformation.
public class ShortestPalindrome {
	private static Logger logger =  LogManager.getLogger(ShortestPalindrome.class);
	
	//return the p array, span for each element, from center to outmost element (on the s2 array)
	public int[] findLongestPalindrome(String s){//Manachers
		char[] s2 = new char[2*s.length()+1];
		for (int i=0; i<s.length(); i++){
			s2[2*i+1]=s.charAt(i);
			s2[2*i]='|';
		}
		s2[2*s.length()]='|';
		int[] p2 = new int[s2.length];
		int c=0, r=0;
		int m=0, n=0; //the left most and right most of the centered at i's palindrome
		for (int i=1; i<s2.length; i++){
			if (i>r){//init
				p2[i]=0; m=i-1; n=i+1;
			}else{
				int i2=2*c-i;
				if (p2[i2]<(r-i)){
					p2[i]=p2[i2];
					m=-1; //signal for not re-calculate p[i]
				}else{
					p2[i]=r-i;
					n=r+1; m=2*i-n;
				}
			}
			while(m>=0 && n<s2.length && s2[m]==s2[n]){
				p2[i]++; m--; n++;
			}
			if ((i+p2[i])>r){
				c=i; r = i + p2[i];
			}
		}
		return p2;
	}
	
	public String shortestPalindrome(String s) {
		int[] p2 = findLongestPalindrome(s);
		int max = Integer.MIN_VALUE;
		for (int i=0; i<p2.length; i++){
			if (p2[i]==i){
				if (p2[i]>max){
					max = p2[i];
				}
			}
		}
		int i=0;
		StringBuffer sb = new StringBuffer();
		for (i=s.length()-1; i>=max; i--){
			sb.append(s.charAt(i));
		}
		return sb.toString()+s;
    }
}
