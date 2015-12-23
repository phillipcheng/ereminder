package org.cld.util.test;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestRegex {
	
	public static final Logger logger = LogManager.getLogger(TestRegex.class);
	
	@Test
	public void test1(){
		
		 Pattern p=null;
		 Matcher m = null;
		 boolean b;
		 
//		 p = Pattern.compile("a*b");
//		 m = p.matcher("aaaaab");
//		 b = m.matches();
//		 assertTrue(b);
		 
		 p=Pattern.compile(".*-([\\d]+)-1\\.html");
		 //p = Pattern.compile(".*-pg([\\d]+)\\.html.*");
		 //p = Pattern.compile(".*page_index=([\\d]+).*");
		 //p = Pattern.compile("page_index=(\\d)");
		 
		 //m = p.matcher("http://category.dangdang.com/all/?category_path=01.30.05.09.00.00&page_index=2");
		 //m = p.matcher("page_index=2");
		 //m = p.matcher("http://category.dangdang.com/all/?category_path=01.30.05.09.00.00&page_index=122&");
		 //m = p.matcher("http://category.dangdang.com/cid4005729-pg211.html##");
		 m=p.matcher("http://list.jd.com/1318-1467-1507-0-0-0-0-0-0-0-1-1-5-1.html");
		 boolean found = false;
		 while (m.find()) {
             String str = String.format("I found the text" +
                 " \"%s\" starting at " +
                 "index %d and ending at index %d.%n",
                 m.group(),
                 m.start(),
                 m.end());
             logger.info(str);
             found = true;

    		 logger.info("group 1:" + m.group(1));
    		 logger.info("group 1: start at:" + m.start(1));
         }
		 if(!found){
             logger.info("No match found.");
         }
		 //String num = m.group(0);
		 //assertTrue("21".equals(num));
	}

}
