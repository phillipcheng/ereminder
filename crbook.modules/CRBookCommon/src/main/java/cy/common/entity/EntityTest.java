package cy.common.entity;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.PatternResult;

public class EntityTest {
	private static Logger logger =  LogManager.getLogger(EntityTest.class);
	
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

	public static Volume generateTestVolume(String id){
		Volume v = new Volume();
		v.setCoverUri("http://");
		v.setId(id);
		v.setName(id);
		v.setAuthor("me");
		v.setBookNum(12);
		
		//data is generated inside
		v.dataToJSON();
		
		return v;
	}


}
