import static org.junit.Assert.*;

import org.junit.Test;


public class TavasAndMalekasSuite {

	@Test
	public void test1() {
		assertTrue(TavasAndMalekas.getNumber(6, 2, "ioi", "1 3")==26);
	}

	

	@Test
	public void test2() {
		assertTrue(TavasAndMalekas.getNumber(5, 2, "ioi", "1 2")==0);
	}
}
