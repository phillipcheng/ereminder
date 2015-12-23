package cy.common.xml.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import cy.common.entity.Book;
import cy.common.entity.Page;
import cy.common.xml.XmlWorker;

public class XmlTest {

	public static final Logger logger = LogManager.getLogger(XmlTest.class);
	
	private Book getBook(){
		Book b = new Book();
		b.setName("书1");
		b.setCoverUri("http://1.html");
		b.setTotalPage(12);
		b.dataToJSON();
		return b;
	}
	
	private Page getPage(int pagenum){
		Page p = new Page();
		p.setBackgroundUri("http://background=?a&1");
		p.setPageNum(pagenum);
		p.setRewardUri("https://reward=你好");
		p.dataToJSON();
		return p;
	}
	
	
	public void test1() throws Exception{
		Book b = getBook();
		List<Page> plist = new ArrayList<Page>();
		for (int i=0; i<2;i++){
			Page p = getPage(i);
			plist.add(p);
		}
		
		File f = new File("test1.xml");
		if (f.exists()){
			f.delete();
		}
		XmlWorker.writeBookXml(b, plist, f);
	}
	
	
	public void test2() throws Exception{
		Book b = new Book();
		List<Page> plist = new ArrayList<Page>();
		File f = new File("test1.xml");
		XmlWorker.readBookXml(b, plist, f);
		logger.info("book:" + b);
		logger.info("pagelist:" + plist);
	}


}
