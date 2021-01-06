package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Fib {
    private static Logger logger =  LogManager.getLogger(Fib.class);
    Map<Integer, Integer> cache = new HashMap<>();

    public int fib(int n) {//n>=30, int overflow
        if (n==0) return 0;
        if (n==1) return 1;
        if (cache.containsKey(n)) {
            return cache.get(n);
        }else{
            int v = fib(n-1) + fib(n-2);
            cache.put(n, v);
            return v;
        }
    }

    Map<Integer, Long> cache1 = new HashMap<>();
    public long fib0(int n) {//n>=7000, stack overflow
        if (n==0) return 0;
        if (n==1) return 1;
        if (cache1.containsKey(n)) {
            return cache1.get(n);
        }else{
            long v = fib(n-1) + fib(n-2);
            cache1.put(n, v);
            return v;
        }
    }

    public BigInteger fib1(int n){//n>=400000, out of heap memory
        if (n==0) return new BigInteger("0");
        if (n==1) return new BigInteger("1");
        BigInteger[] v = new BigInteger[n+1];
        v[0] = new BigInteger("0");
        v[1] = new BigInteger("1");
        for (int i=2; i<=n; i++){
            v[i] = v[i-1].add(v[i-2]);
        }
        return v[n];
    }

    public BigInteger fib2(int n){
        if (n==0) return new BigInteger("0");
        if (n==1) return new BigInteger("1");
        BigInteger vMinus2=new BigInteger("0");
        BigInteger vMinus1=new BigInteger("1");
        BigInteger v = vMinus1.add(vMinus2);
        for (int i=2; i<=n; i++){
            v = vMinus1.add(vMinus2);
            vMinus2 = vMinus1;
            vMinus1 = v;
        }
        return v;
    }

    public static void main(String[] args){
        Fib fib = new Fib();
        int n;
        long v;
        BigInteger bi;

        n=2;
        v = fib.fib(n);
        assert(v==1);

        n=3;
        v = fib.fib(n);
        assert(v==2);

        n=30;
        v = fib.fib(n);
        logger.info(v);

        n=40;
        v = fib.fib0(n);
        logger.info(v);

        n=40000;
        bi = fib.fib1(n);
        logger.info(bi);

        n=400000;
        bi = fib.fib2(n);
        logger.info(bi);
    }
}
