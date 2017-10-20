import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import org.junit.Test;

public class TestAll {

	@Test
	public void testQ822A(){
		String myString = "4 3";
		
		InputStream is = new ByteArrayInputStream( myString.getBytes() );
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		System.setIn(is);
		System.setOut(ps);
		Q822A.main(new String[]{});
		String ret = new String(os.toByteArray());
		
		assertTrue("6\n".equals(ret));
	}
}
