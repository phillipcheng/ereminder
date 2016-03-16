package leet.algo;

import java.util.Stack;

public class SimplifyPath {
	public String simplifyPath(String path) {
        String[] segs = path.split("/");
        Stack<String> stack = new Stack<String>();
        for (int i=0;i<segs.length; i++){
        	String seg = segs[i];
        	if (".".equals(seg)){
        		
        	}else if ("..".equals(seg)){
        		if (!stack.isEmpty())
        			stack.pop();
        	}else if ("".equals(seg)){
        		
        	}else{
        		stack.push(seg);
        	}
        }
        String ret ="";
        if (stack.isEmpty()){
        	return "/";
        }else{
	        while (!stack.isEmpty()){
	        	String seg = stack.pop();
	        	ret = "/" + seg + ret;
	        }
        }
        return ret;
    }
}
