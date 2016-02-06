package algo.leet;

import java.util.Stack;

//Given a string containing just the characters '(', ')', '{', '}', '[' and ']', 
//determine if the input string is valid.
public class ValidParentheses {
	public boolean isValid(String s) {
		Stack<Character> stack = new Stack<Character>();
		char[] chars=s.toCharArray();
		for (int i=0; i<chars.length; i++){
			char ch = chars[i];
			if (stack.isEmpty()){
				if (ch=='(' || ch=='{' || ch=='['){
					stack.push(ch);
				}else{//puting closing parentheses onto empty stack
					return false;
				}
			}else{
				char top = stack.peek();
				if (top==')' || top =='}' || top ==']'){
					return false;
				}else{
					if (top=='('){
						if (ch==')'){
							stack.pop();
						}else if (ch=='(' || ch=='{' || ch=='['){
							stack.push(ch);
						}else{
							return false;
						}
					}else if (top=='['){
						if (ch==']'){
							stack.pop();
						}else if (ch=='(' || ch=='{' || ch=='['){
							stack.push(ch);
						}else{
							return false;
						}
					}else if (top=='{'){
						if (ch=='}'){
							stack.pop();
						}else if (ch=='(' || ch=='{' || ch=='['){
							stack.push(ch);
						}else{
							return false;
						}
					}
				}
			}			
		}
		if (stack.isEmpty()){
			return true;
		}else{
			return false;
		}
		
    }

}
