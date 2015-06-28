package org.cld.stock.load;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public interface ContentMapper {
	
	public void convert(String tid, InputStreamReader is, OutputStreamWriter os);

}
