package princeton.algo1.interview;

import org.junit.Test;

public class AutoBoxing {
	
	@Test
	public void test1(){
		double a = 2;
		double b = 2;
		Double x = new Double(a);
		Double y = new Double(b);
		assert(a==b && !x.equals(y));
	}
	
	@Test
	public void test2(){
		double a = 0.0;
		double b = -0.0;
		Double x = new Double(a);
		Double y = new Double(b);
		assert(a!=b && x.equals(y));
	}

}
