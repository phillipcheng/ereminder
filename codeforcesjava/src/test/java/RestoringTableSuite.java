import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class RestoringTableSuite {

	@Test
	public void test1() {
		System.out.println(String.join(" ", RestoringTable.getOrigins(new String[]{"-1"})));
	}
	
	@Test
	public void test2() {
		System.out.println(String.join(" ", RestoringTable.getOrigins(new String[]{"-1 18 0", "18 -1 0", "0 0 -1"})));
	}
	
	@Test
	public void test3() {
		System.out.println(String.join(" ", RestoringTable.getOrigins(new String[]{
				"-1 128 128 128", 
				"128 -1 148 160",
				"128 148 -1 128",
				"128 160 128 -1"})));
	}

}
