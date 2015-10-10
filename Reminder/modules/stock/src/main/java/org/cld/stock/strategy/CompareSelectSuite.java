package org.cld.stock.strategy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;

public class CompareSelectSuite {
	public static Logger logger = LogManager.getLogger(CompareSelectSuite.class);
	
	public static void selectHSABreakIPO(String thisDay, CrawlConf cconf, String outputFileDir){
		String sqlA = "select stockid, ipoprice from SinaCorpInfo";
		String sqlB = String.format("select stockid, close from SinaMarketDaily where dt='%s'", thisDay);
		new CompareSelect().select("hsaBreakIPO", cconf, outputFileDir, new String[]{sqlA, sqlB, "a>b"});
	}
	
	public static void selectNasdaqBreakIPO(String thisDay, CrawlConf cconf, String outputFileDir){
		String sqlA = "select FQ.stockid, FQ.adjclose from (select stockid, min(dt) as ipoDt from NasdaqFQHistory group by stockid) ipoFQ, NasdaqFQHistory FQ where FQ.stockid=ipoFQ.stockid and FQ.dt=ipoFQ.ipoDt";
		String sqlB = String.format("select stockid, adjclose from NasdaqFQHistory where dt='%s'", thisDay); 
		new CompareSelect().select("nasdaqBreakIPO", cconf, outputFileDir, new String[]{sqlA, sqlB, "a>b"});
	}
	
	public static void selectHSAAllTimeLow(String thisDay, CrawlConf cconf, String outputFileDir){
		String sqlA = String.format("select stockid, min(close) from SinaMarketFQ where dt<='%s' group by stockid", thisDay);
		String sqlB = String.format("select stockid, close from SinaMarketFQ where dt='%s'", thisDay);
		String filter = "a>=b";
		new CompareSelect().select("hsaAllTimeLow", cconf, outputFileDir, new String[]{sqlA, sqlB, filter});
	}
	
	public static void selectNasdaqAllTimeLow(String thisDay, CrawlConf cconf, String outputFileDir){
		String sqlA = String.format("select stockid, min(adjclose) from NasdaqFQHistory where dt<='%s' group by stockid", thisDay);
		String sqlB = String.format("select stockid, adjclose from NasdaqFQHistory where dt='%s'", thisDay);
		String filter = "a>=b";
		new CompareSelect().select("nasdaqAllTimeLow", cconf, outputFileDir, new String[]{sqlA, sqlB, filter});
	}
}
