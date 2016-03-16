package leet.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BasicCalculator {
	
	class Literal{
		int type; //0 for operator, 1 for parenthesis, 2 for integer
		char val; //'(',')','+','-'
		int iVal; //
		public Literal(int type, char val){
			this.type = type;
			this.val = val;
		}
		public Literal(int ival){
			this.type = 2;
			this.iVal = ival;
		}
		public String toString(){
			if (type==2){
				return iVal+"";
			}else{
				return val+"";
			}
		}
	}
	
	private List<Literal> parseInput(String s){
		List<Literal> ll = new ArrayList<Literal>();
		String intStr="";
		for (int i=0; i<s.length(); i++){
			char ch = s.charAt(i);
			if (ch==' '){
				if (!intStr.equals("")){
					int in = Integer.parseInt(intStr);
					Literal l = new Literal(in);
					ll.add(l);
					intStr = "";
				}
			}else if (ch=='+'||ch=='-'){
				if (!intStr.equals("")){
					int in = Integer.parseInt(intStr);
					Literal l = new Literal(in);
					ll.add(l);
					intStr = "";
				}
				Literal l = new Literal(0, ch);
				ll.add(l);
			}else if (ch=='('||ch==')'){
				if (!intStr.equals("")){
					int in = Integer.parseInt(intStr);
					Literal l = new Literal(in);
					ll.add(l);
					intStr = "";
				}
				Literal l = new Literal(1, ch);
				ll.add(l);
			}else{
				intStr +=ch;
			}
			if (i==s.length()-1){
				if (!intStr.equals("")){
					int in = Integer.parseInt(intStr);
					Literal l = new Literal(in);
					ll.add(l);
					intStr = "";
				}
			}
		}
		return ll;
	}
	
	public int calculate(String s) {
		List<Literal> ll = parseInput(s);
		Stack<Literal> stack = new Stack<Literal>();
        for (int i=0; i<ll.size(); i++){
        	Literal l = ll.get(i);
        	if (l.type==0){//operator
        		stack.push(l);
        	}else if (l.type==1){//parenthesis
        		if (l.val == '('){
        			stack.push(l);
        		}else{//met ')'
        			List<Literal> exp = new ArrayList<Literal>();
        			Literal xl = stack.pop();
        			while (xl.val!='('){
        				exp.add(0, xl);
        				xl = stack.pop();
        			}
        			int val=exp.get(0).iVal;
        			for (int j=1; j<exp.size(); j=j+2){
        				Literal opl = exp.get(j);
        				Literal al = exp.get(j+1);
        				if (opl.val=='+'){
        					val = val + al.iVal;
        				}else if (opl.val=='-'){
        					val = val - al.iVal;
        				}else{
        					System.err.print("error");
        				}
        			}
        			stack.push(new Literal(val));
        		}
        	}else{
        		stack.push(l);
        	}
        }
        List<Literal> exp = new ArrayList<Literal>();
		while (!stack.empty()){
			exp.add(0, stack.pop());
		}
		int val=exp.get(0).iVal;
		for (int j=1; j<exp.size(); j=j+2){
			Literal opl = exp.get(j);
			Literal al = exp.get(j+1);
			if (opl.val=='+'){
				val = val + al.iVal;
			}else if (opl.val=='-'){
				val = val - al.iVal;
			}else{
				System.err.print("error");
			}
		}
		return val;
    }

}
