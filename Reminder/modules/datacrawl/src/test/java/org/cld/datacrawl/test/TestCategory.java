package org.cld.datacrawl.test;

import static org.junit.Assert.*;

import java.util.Date;

import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.CrawledItemId;
import org.junit.Test;

public class TestCategory {

	@Test
	public void testGetRealCatId() {
		String catId = "abc";
		String splitValue="low";
		
		Category c = new Category(new CrawledItemId(catId, null, new Date()), "default");
		
		//c.setSplitValue(splitValue);
		
		c.setCatId(catId, splitValue);
		assertTrue(catId.equals(c.getRealCatId()));
		assertTrue(c.getId().getId().equals(catId+Category.CATID_SEP+splitValue));
	}

}
