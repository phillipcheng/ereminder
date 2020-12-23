package leet.algo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static junit.framework.TestCase.assertTrue;

//Given a string which contains only lowercase letters, remove duplicate letters so that every letter appear once and only once.
//You must make sure your result is the smallest in lexicographical order among all possible results.
public class RemoveDuplicateLetters {
	private static Logger logger =  LogManager.getLogger(RemoveDuplicateLetters.class);

	public String removeDuplicateLetters(String s) {
		char[] chars = s.toCharArray();
		Stack<Character> stack = new Stack<>();
		for (int i=0; i<chars.length; i++){
			char c = chars[i];
			if (stack.contains(c)){//
				continue;
			}
			while (!stack.isEmpty() //stack not empty
					&& stack.peek() > c //new charactor is less than the stack top
					&& s.lastIndexOf(stack.peek()) > i) {//there is stack-top valued item in the rest of the string
				stack.pop();
			}

			stack.push(c);
		}

		StringBuffer sb = new StringBuffer();
		for (int i=0; i<stack.size();i++){
			sb.append(stack.elementAt(i));
		}
		return sb.toString();
    }

    public static void main(String[] args){
		RemoveDuplicateLetters rdl = new RemoveDuplicateLetters();

		String result = "";

		result = rdl.removeDuplicateLetters("bcabc");
		logger.info(result);
		assertTrue("abc".equals(result));


		result = rdl.removeDuplicateLetters("cbacdcbc");
		logger.info(result);
		assertTrue("acdb".equals(result));

	}

}
