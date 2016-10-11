package leet.algo;

import java.util.ArrayList;
import java.util.List;

import leet.algo.test.TestPow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Pow {
	private static Logger logger =  LogManager.getLogger(TestPow.class);
	
	public double myPow(double x, int n) {
		List<Long> twos = new ArrayList<Long>();
        long m = n;
        boolean sign = true;
        if (n<0){
        	sign = false;
        	m = -1l * n;
        }else if (n==0){
        	return 1;
        }
        while (m/2>=1){
        	twos.add(m%2);
        	m = m/2;
        }
        twos.add((long) 1);
        logger.info(twos);
        //
        double v = x;
        double ret = 1;
        for (int i=0; i<twos.size(); i++){
        	if (i==0){
        		v = x;
        	}else{
        		v= v*v;
        	}
        	if (twos.get(i)==1){
        		ret *=v;
        	}
        }
        if (!sign){
        	ret = 1/ret;
        }
        return ret;
    }
}
