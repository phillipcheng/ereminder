package cy.common.entity.test;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.PatternResult;
import org.junit.Test;

import cy.common.entity.Book;


public class BookTest {
	private static Logger logger =  LogManager.getLogger(BookTest.class);
	
	public static Book generateTestBook(String id){
		PatternResult pr = new PatternResult();
		pr.setDigitNum(4);
		pr.setPatternPrefix("http://");
		pr.setPatternSuffix(".jpg");
		pr.setPatternType(PatternResult.pt_x_s_yyy_zzz);
		pr.setPostFix("abc");
		pr.setSep("_");
		pr.setStartImageCount(14);
		
		Book b = new Book();
		b.setbUrl("http://");
		b.setCat("9999");
		b.setCoverUri("http://");
		b.setId(id);
		b.setName(id);
		b.setPageBgUrlPattern(pr);
		
		//data is generated inside
		b.dataToJSON();
		
		return b;
	}
	
	@Test
	public void jasonTest1() {
		Book b = generateTestBook("testBook1");
		
		Book b1 = new Book();
		b1.init("1", 0,  "1", 100, 23, null, b.getData(), "9999", 0, 0, 0, "", 0, true);
		logger.info(b1.toString());
		
	}

}
