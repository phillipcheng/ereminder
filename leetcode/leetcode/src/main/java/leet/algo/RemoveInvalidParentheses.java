package leet.algo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class RemoveInvalidParentheses {
	
	private boolean isValid(String s){
		if (s.length()==0) return true;
		Stack<Character> stack = new Stack<Character>();
		for (int i=0; i<s.length(); i++){
			char ch = s.charAt(i);
			if (ch=='('){
				stack.push(ch);
			}else if (ch==')'){
				if (!stack.isEmpty()){
					if (stack.peek()=='('){
						stack.pop();
					}else{
						return false;
					}
				}else{
					return false;
				}
			}
		}
		return stack.isEmpty();
	}
	
	public List<String> removeInvalidParentheses(String s) {
		Set<String> ret = new HashSet<String>();
        Queue<String> q = new LinkedList<String>();
        q.add(s);
        int maxCorrectSize = 0;
        while (!q.isEmpty()){
        	String str = q.poll();
        	if (isValid(str)){
        		if (maxCorrectSize==0){
        			maxCorrectSize = str.length();
        			ret.add(str);
        		}else if (str.length()<maxCorrectSize){//less optimal found, break out
        			break;
        		}else{//more optimal solution found
        			ret.add(str);
        		}
        	}else{
        		if (maxCorrectSize>0){
        			//we have found the max value, no try new children
	        	}else{
	        		if (str.startsWith(")")){//string start with ) must be removed
	        			str = str.substring(1, str.length());
	        		}
	        		if (str.length()==1){
	        			String nstr = "";
	        			if (!q.contains(nstr)){
	        				q.add(nstr);
	        			}
	        		}else{
		        		//adding children
		        		for (int i=1; i<str.length(); i++){
		        			//remove char at i from str
		        			String nstr = str.substring(0, i) + str.substring(i+1, str.length());
		        			if (!q.contains(nstr)){
		        				q.add(nstr);
		        			}
		        		}
	        		}
	        	}
        	}
        }
        List<String> rl = new ArrayList<String>();
        rl.addAll(ret);
        return rl;
    }

}
