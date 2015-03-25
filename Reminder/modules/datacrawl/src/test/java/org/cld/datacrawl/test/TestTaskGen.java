package org.cld.datacrawl.test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlTaskGenerator;
import org.cld.taskmgr.entity.Task;
import org.junit.Test;

public class TestTaskGen {
	
	private static Logger logger =  LogManager.getLogger(TestTaskGen.class);
	
	@Test
	public void test0(){
		/*
		Category cat = new Category();
		cat.setCatId("test1");
		cat.setFullUrl("nothing");
		cat.setItemNum(1246);
		cat.setPageSize(15);
		cat.setLeaf(true);
		cat.setStype("test");
		List<Task> tl = BrowseTaskGenerator.genTaskForCat(cat, null, null);
		assertTrue(tl.size()==2);
		BrowseDetailTaskConf bct;
		//check first task
		bct = (BrowseDetailTaskConf) tl.get(0);
		logger.info("1st:" + bct);
		assertTrue(bct.getFromPage()== 1);
		assertTrue(bct.getToPage()==67);
		assertTrue(bct.getTotalItem()==1005);
		
		//check last task
		bct = (BrowseDetailTaskConf) tl.get(1);
		logger.info("2nd:" + bct);
		assertTrue(bct.getFromPage()== 68);
		assertTrue(bct.getToPage()==-1);
		assertTrue(bct.getTotalItem()==241);
		*/
	}
	

}
