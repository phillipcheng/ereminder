package org.cld.stock.test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockUtil;
import org.cld.stock.sina.SinaStockConfig;
import org.junit.Test;

public class TestStock {
	private static Logger logger =  LogManager.getLogger(TestStock.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	@Test
	public void testTZ() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("PST"));
		Date d = sdf.parse("2015-09-24");
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		sdf2.setTimeZone(TimeZone.getTimeZone("EST"));
		logger.info(sdf2.format(d));
		
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
		sdf3.setTimeZone(TimeZone.getTimeZone("CTT"));
		logger.info(sdf3.format(d));
	}
	
	@Test
	public void testOpenDay() throws Exception{
		Date d = sdf.parse("2015-09-30");
		Date nd = StockUtil.getNextOpenDay(d, SinaStockConfig.CNHolidays,TimeZone.getTimeZone("CTT"));
		logger.info(String.format("nd is %s", sdf.format(nd)));
		assertTrue("2015-10-08".equals(sdf.format(nd)));
		
		d = sdf.parse("2015-12-31");
		nd = StockUtil.getNextOpenDay(d, SinaStockConfig.CNHolidays, TimeZone.getTimeZone("CTT"));
		logger.info(String.format("nd is %s", sdf.format(nd)));
		assertTrue("2016-01-04".equals(sdf.format(nd)));
		
		Date fd = sdf.parse("2015-09-30");
		Date td = sdf.parse("2016-01-05");
		
		List<Date> dl = StockUtil.getOpenDayList(fd, td, SinaStockConfig.CNHolidays, TimeZone.getTimeZone("CTT"));
		logger.info(String.format("dl is %s", dl));
		
		fd = sdf.parse("2004-10-01");
		td = sdf.parse("2015-09-26");
		dl = StockUtil.getOpenDayList(fd, td, SinaStockConfig.CNHolidays, TimeZone.getTimeZone("CTT"));
		logger.info(String.format("dl is %s", dl));
	}

}
