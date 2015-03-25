package org.cld.datastore.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.SqlUtil;
import org.junit.Test;

public class SqlUtilTest {
	Logger logger = LogManager.getLogger(SqlUtilTest.class);
	
	@Test
	public void testGenerateInParameters() {
		List<String> params = new ArrayList<String>();
		params.add("999999");
		params.add("999998");
		String inSql = SqlUtil.generateInParameters(params);
		logger.info(inSql);
		assertTrue(inSql.equals("(?,?)"));
	}

}
