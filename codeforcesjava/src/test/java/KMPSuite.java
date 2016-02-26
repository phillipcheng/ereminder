import static org.junit.Assert.*;

import org.junit.Test;


public class KMPSuite {

	@Test
	public void test1() {
		String s = "ABC ABCDAB ABCDABCDABDE";
		String p = "ABCDABD";
		int idx = KMP.find(s, p);
		System.out.println(idx);
	}

}
