package leet.algo;

import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrappingRainWater {
	private static Logger logger =  LogManager.getLogger(TrappingRainWater.class);
	
	class Bank{//bank still can hold more water
		int posLeft;
		int posRight;
		int high;
		public Bank(int posLeft, int posRight, int high){
			this.posLeft = posLeft;
			this.posRight = posRight;
			this.high = high;
		}
		public String toString(){
			return String.format("%d-%d:%d", posLeft, posRight, high);
		}
	}
	
	public int trap(int[] height) {
		Stack<Bank> stack = new Stack<Bank>();
		int hold = 0;
        for (int i=0; i<height.length; i++){
        	int nh = height[i];
        	if (stack.empty()){
        		stack.push(new Bank(i, i, nh));
        	}else if (stack.size()==1){
        		if (nh>=stack.peek().high){
        			stack.pop();
        		}
    			stack.push(new Bank(i, i, nh));
        	}else{//now we have 3 Bank we can hold water
        		int preH = stack.peek().high;
        		if (nh<preH){
        			stack.push(new Bank(i, i, nh));
        		}else if (nh==preH){
        			stack.peek().posRight=i;
        		}else{//now we hold water
        			Bank bottom = stack.pop();
        			Bank left = stack.peek();
        			while (left.high<nh){
        				hold += (bottom.posRight-bottom.posLeft+1)*(left.high-bottom.high);
        				left.posRight=bottom.posRight;
        				bottom = stack.pop();
        				if (stack.isEmpty()){
        					break;
        				}else{
        					left = stack.peek();
        				}
        			}
        			if (stack.isEmpty()){
        				stack.push(new Bank(i, i, nh));
        			}else{//left.high>=nh
        				hold += (bottom.posRight-bottom.posLeft+1)*(nh-bottom.high);
        				if (left.high>nh){
        					bottom.high = nh;
        					bottom.posRight = i;
        					stack.push(bottom);
        				}else{
        					left.posRight=i;
        				}
        			}
        		}
        	}
        	//logger.info(String.format("%s, hold:%d, idx:%d", stack, hold, i));
        }
        return hold;
    }

}
