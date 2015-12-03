package org.cld.stock.nasdaq;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NasdaqTestStockConfig {
	private static Logger logger =  LogManager.getLogger(NasdaqTestStockConfig.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final String MarketId_NASDAQ_Test="NASDAQ_Test";
	public static final String Test_SD = "2014-01-10";
	public static final String Test_SHORT_SD = "2015-05-01"; //this should before all Test_Dx, since this is used as the start date, and Test_Dx is used as end date.
	public static final String Test_END_D1 = "2015-05-10";
	public static final String Test_END_D2 = "2015-05-20";//only increase date
	public static final String Test_END_D3 = "2015-06-10";//also increase stock
	public static final String Test_END_D4 = "2015-07-01";//only increase date
	public static Date date_Test_SD = null;
	public static Date date_Test_END_D1 = null;
	public static Date date_Test_END_D2 = null;
	public static Date date_Test_END_D3 = null;
	public static Date date_Test_END_D4 = null;
	static{
		try{
			date_Test_SD = sdf.parse(Test_SD);
			date_Test_END_D1 = sdf.parse(Test_END_D1);
			date_Test_END_D2 = sdf.parse(Test_END_D2);
			date_Test_END_D3 = sdf.parse(Test_END_D3);
			date_Test_END_D4 = sdf.parse(Test_END_D4);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	//public static final String[] Test_D1_Stocks = new String[]{"baba", "goog"};
	//public static final String[] Test_D3_Stocks = new String[]{"baba", "goog", "bidu"};

	public static final String[] Test_D1_Stocks = new String[]{"AAPL"};//,"GOOG","TUES","XGTI"
	public static final String[] Test_D3_Stocks = Test_D1_Stocks;
}
