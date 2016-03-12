package leet.algo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class RemoveInvalidParentheses {
	
	public boolean isValid(String s){
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
        Set<String> visited = new HashSet<String>();
        q.add(s);
    	boolean found = false;
        while (!q.isEmpty()){
        	String str = q.poll();
        	if (isValid(str)){
        		ret.add(str);
        		found = true;
        	}
        	if (found) continue;
        	
    		for (int i=0; i<str.length(); i++){
    			//remove char at i from str
    			char ch = str.charAt(i);
    			if (ch==')' || ch=='('){//remove others do not help
    				String nstr = str.substring(0, i) + str.substring(i+1, str.length());
    				if (!visited.contains(nstr)){ 
    					q.add(nstr); 
    					visited.add(nstr);
    				}
    			}
    		}
        			
        }
        List<String> rl = new ArrayList<String>();
        rl.addAll(ret);
        return rl;
    }

}
