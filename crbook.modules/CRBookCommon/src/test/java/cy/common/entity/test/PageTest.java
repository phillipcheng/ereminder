package cy.common.entity.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.PatternResult;
import org.junit.Test;

import cy.common.entity.Book;
import cy.common.entity.Page;
import cy.common.entity.Volume;

public class PageTest {
	private static Logger logger =  LogManager.getLogger(PageTest.class);
	
	public static List<Page> generateTestPage(String bookId, int pageNum){
		List<Page> pl = new ArrayList<Page>();
    	for (int i=0; i<5; i++){
    		
    		Page p = new Page();
    		p.setBookid(bookId);
    		p.setPageNum(i);
    		p.setBackgroundUri("http://" + i);
    		//data is generated inside
    		p.dataToJSON();
    		
    		pl.add(p);
    	}
		
		
		return pl;
	}

	@Test
	public void test1(){
		List<Page> lp = generateTestPage("a", 5);
		String str = Page.toTopJSONListString(lp);
		logger.info(str);
	}
}
