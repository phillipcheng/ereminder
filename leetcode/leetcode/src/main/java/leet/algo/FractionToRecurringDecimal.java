package leet.algo;

import java.util.ArrayList;
import java.util.List;

public class FractionToRecurringDecimal {
	public String fractionToDecimal(int numerator, int denominator) {
		StringBuffer sb = new StringBuffer();
		long nume = numerator;
		long deno = denominator;
        if (nume<0 && deno>0 || nume>0 && deno<0){
			sb.append("-");
			nume = Math.abs(nume);
			deno = Math.abs(deno);
		}
        long integral = nume/deno;
        nume = nume%deno;
        sb.append(integral);
        List<Long> nl = new ArrayList<Long>(); //list of numerator
        List<Long> ql = new ArrayList<Long>(); //list of quotient
        int i=0;
        while (nume!=0 && !nl.contains(nume)){
        	nl.add(nume);
        	nume = nume * 10;
        	long quotient = nume/deno;
        	ql.add(quotient);
        	nume = nume%deno;
        	i++;
        }
        if (i>0){
        	sb.append(".");
        	if (nume==0){
        		for (int j=0; j<ql.size(); j++){
        			sb.append(ql.get(j));
        		}
        	}else{
        		for (int j=0; j<ql.size(); j++){
        			long n = nl.get(j);
        			long q = ql.get(j);
        			if (n==nume){
        				sb.append("(");
        				sb.append(q);
        			}else{
        				sb.append(q);
        			}
        		}
        		sb.append(")");
        	}
        }
        return sb.toString();
    }
}
