package leet.algo;

import java.util.Stack;

public class EvalReversePolishNotation {
	
	/*
	 * Evaluate the value of an arithmetic expression in Reverse Polish Notation.
	 * Valid operators are +, -, *, /. Each operand may be an integer or another expression.
	 */

	public int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<Integer>();
        for (int i=0; i<tokens.length; i++){
        	String t = tokens[i];
        	if ("+".equals(t)||"-".equals(t)||"*".equals(t)||"/".equals(t)){
        		int op2 = stack.pop();
        		int op1 = stack.pop();
        		if ("+".equals(t)){
        			stack.push(op1+op2);
        		}else if ("-".equals(t)){
        			stack.push(op1-op2);
        		}else if ("*".equals(t)){
        			stack.push(op1*op2);
        		}else if ("/".equals(t)){
        			stack.push(op1/op2);
        		}
        	}else{
        		stack.push(Integer.parseInt(t));
        	}
        }
        return stack.pop();
    }
}
