package leet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExpressionAddOperator {
	private static Logger logger =  LogManager.getLogger(ExpressionAddOperator.class);
	
	class ExpValue{
		long[] operands;//length of operands is 1+ length of operators
		char[] operators;
		
		public ExpValue(long[] operands, char[] operators){
			this.operands = operands;
			this.operators = operators;
		}
		public ExpValue append(long operand, char operator){
			long[] newopd = new long[operands.length+1];
			char[] newopr = new char[operators.length+1];
			ExpValue ev = new ExpValue(newopd, newopr);
			System.arraycopy(this.operands, 0, newopd, 1, operands.length);
			System.arraycopy(this.operators, 0, newopr, 1, operators.length);
			newopd[0]=operand;
			newopr[0]=operator;
			return ev;
		}
		public void reverseOp(){
			for (int i=0; i<operators.length; i++){
				if (operators[i]=='+'){
					operators[i]='-';
				}else if (operators[i]=='-'){
					operators[i]='+';
				}
			}
		}
		public String toString(){
			StringBuffer sb = new StringBuffer();
			if (operands.length==0){
				return "";
			}else if (operands.length==1){
				return operands[0]+"";
			}else{
				sb.append(operands[0]);
				for (int i=0; i<operators.length; i++){
					sb.append(operators[i]).append(operands[i+1]);
				}
			}
			return sb.toString();
		}
	}
	
	public List<ExpValue> getExp(long a, String b, long target){
		//logger.info(String.format("getExp: a:%d, b:%s, target:%d", a, b, target));
		List<ExpValue> evl = new ArrayList<ExpValue>();
		if (b.length()==0){
			if (a == target){
				ExpValue ev = new ExpValue(new long[]{a}, new char[]{});
				evl.add(ev);
			}
		}else{//b.length>=1
			for (int i=0; i<b.length(); i++){
				long ba = Long.parseLong(b.substring(0, i+1));
				String left = "";
				if (i+1<b.length()){
					left = b.substring(i+1);
				}
				//now we can play with a, ba, left
				long ntarget = target;
				char op='*';
				List<ExpValue> nevl = null;
				//1. a*ba,...
				ntarget = target;
				op='*';
				nevl = getExp(a*ba, left, ntarget);
				for (ExpValue ev:nevl){
					ev.operands[0]=ba;
					evl.add(ev.append(a, op));
				}
				//2. a+ba+-...
				ntarget = target;
				op = '+';
				nevl = getExp(a+ba, left, ntarget);
				for (ExpValue ev:nevl){
					if (ev.operators.length>=1 && ev.operators[0]!='*' || ev.operators.length==0){
						ev.operands[0]=ba;
						evl.add(ev.append(a,op));
					}
				}
				//3. a-ba+-...
				ntarget = target;
				op ='-';
				nevl = getExp(a-ba, left, ntarget);
				for (ExpValue ev:nevl){
					if (ev.operators.length>=1 && ev.operators[0]!='*' || ev.operators.length==0){
						ev.operands[0]=ba;
						evl.add(ev.append(a,op));
					}
				}
				//4. a+ba*...
				ntarget = target-a;
				op ='+';
				nevl = getExp(ba, left, ntarget);
				for (ExpValue ev:nevl){
					if (ev.operators.length>=1 && ev.operators[0]=='*' || ev.operators.length==0){
						evl.add(ev.append(a,op));
					}
				}
				//5. a-ba*..., need reverse operators of the result
				ntarget = a-target;
				op ='-';
				nevl = getExp(ba, left, ntarget);
				for (ExpValue ev:nevl){
					if (ev.operators.length>=1 && ev.operators[0]=='*' || ev.operators.length==0){
						ev.reverseOp();
						evl.add(ev.append(a,op));
					}
				}
				
				if (b.charAt(i)=='0'){//can't split as 01,1 only as 0,11
					break;
				}
			}
		}
		
		return evl;
	}
	
	public List<String> addOperators(String num, long target) {
		List<ExpValue> evl = new ArrayList<ExpValue>();
		if (num.length()>0){
			if (num.charAt(0)=='0'){
				evl.addAll(getExp(0, num.substring(1), target));
			}else{
				for (int i=0; i<num.length(); i++){
					long v = Long.parseLong(num.substring(0, i+1));
					evl.addAll(getExp(v, num.substring(i+1), target));
				}
			}
		}
		TreeSet<String> exps = new TreeSet<String>();
		for (ExpValue ev: evl){
			exps.add(ev.toString());
		}
		List<String> ret = new ArrayList<String>();
		ret.addAll(exps);
		return ret;
    }

}
