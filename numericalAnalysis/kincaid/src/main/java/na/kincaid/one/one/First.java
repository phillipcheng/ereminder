package na.kincaid.one.one;

import java.util.function.DoubleFunction;

import org.apache.log4j.Logger;

public class First {

	public static Logger  logger = Logger.getLogger(First.class);

	public static void getError(DoubleFunction<Double> f, DoubleFunction<Double> df, double x){
		int i=0, imax=0, n=30;
		double error=0, y=0, h=1, emax=0;
		for (i=0; i<=n; i++){
			h = 0.25 * h;
			y = (f.apply(x+h)-f.apply(x))/h;
			error = Math.abs(df.apply(x)-y);
			logger.info(String.format("i:%d,h:%f,y:%f,error:%f", i, h, y, error));
			if (error>emax){
				emax=error;
				imax=i;
			}
		}
		logger.info(String.format("emax:%f, imax:%d", emax, imax));
	}
	
    public static void main( String[] args ) {
    	DoubleFunction<Double> sinx = x->Math.sin(x);
    	DoubleFunction<Double> cosx = x->Math.cos(x);
    	
    	DoubleFunction<Double> ix = x->1/x;
    	DoubleFunction<Double> dix = x->-1/(x*x);
    	
    	getError(sinx, cosx, 0.5);
    	getError(ix, dix, 0.5);
    }
}
