package org.cld.stock.strategy;

import java.io.UnsupportedEncodingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.stock.strategy.SelectStrategy.byDayType;

public class CompareSelectSuite {
	/*
	public static Logger logger = LogManager.getLogger(CompareSelectSuite.class);
	
	//can be IPO can be new issue
	public static SelectStrategy getHSABreakIssue(String outputFileDir){
		String sa = "select stockid, ipoprice from SinaCorpInfo";
		String sb = "select stockid, close from SinaMarketFQ where dt='%date'";//BackFQ
		return new CompareSelectStrategy("hsaBreakIPO", outputFileDir, 
				new Object[]{
						new String[]{sa, sb}, 
						new String[]{"(b-a)/a"}, 
						"a>b", 
						"za", 
						"desc"},
				new String[]{});
	}

	public static SelectStrategy getHSAAllTimeLow(String outputFileDir){
		String sa = "select stockid, min(close) from SinaMarketFQ where dt<='%date' group by stockid";
		String sb = "select stockid, close from SinaMarketFQ where dt='%date'";
		String za = "a*1.3";//raise min to 30% more
		String zb = "b/a"; //how much increased since min
		String filter = "b<=za";
		String orderBy = "zb";
		String orderDirection = "asc";
		return new CompareSelectStrategy("hsaAllTimeLow", outputFileDir, 
				new Object[]{
						new String[]{sa, sb}, 
						new String[]{za, zb},
						filter,
						orderBy,
						orderDirection},
				new String[]{});
	}

	public static SelectStrategy getHSARallyRatio(String outputFileDir){
		String sa = "select stockid, min(close) from SinaMarketFQ where dt<='2015-03-06' and dt>='2015-01-01' group by stockid";//box low inc-adjusted close
		String sb = "select stockid, max(close) from SinaMarketFQ where dt<='2015-08-18' and dt>'2015-03-06' group by stockid";//box high inc-adjusted close
		String sc = "select stockid, min(close) from SinaMarketFQ where dt<'%date' and dt>'2015-08-18' group by stockid";//this low inc-adjusted close
		String sd = "select stockid, close from SinaMarketFQ where dt='%date'";//inc-adjusted close
		String se = "select stockid, dt, dilutedeps from (select *, row_number() over (partition by stockid order by dt desc) as rn "
				+ "from SinaFrProfitStatement where datediff('%date',dt)>0) q where rn=1";//eps
		String sf = "select stockid, name from SinaCorpInfo";//name
		String sg = "select stockid, close from SinaMarketDaily where dt='%date'";//close
		String za="(d-c)/(b-a)";//potential
		String zb="eps=e[1]*4/(parseInt(e[0].toString().substring(5,7))/4); if (eps>0) {g/eps} else {0}";//price-earning ratio
		String zc="za*zb";//final index, smaller better
		String filter = "zb>0";
		String orderBy = "zc";
		String orderDirection="asc";
		return new CompareSelectStrategy("hsaRallyRatio", outputFileDir, 
					new Object[]{
						new String[]{sa, sb, sc, sd, se, sf, sg}, //from db per stockid
						new String[]{za, zb, zc}, //calculation
						filter, //
						orderBy,
						orderDirection},
					new String[]{}
				);
	}
	
	public static SelectStrategy getHSAEarningForcast(String outputFileDir) throws UnsupportedEncodingException {
		String sa = "select stockid, cp from SinaFrAchieveNotice where dt='%date' and (atype='预增' or atype='预升') and cp>%1";
		String filter = "true";
		String orderBy="a";
		String orderDirection="desc";
		return new CompareSelectStrategy("hasEarningForcast", outputFileDir, 
				new Object[]{
					new String[]{sa}, //from db per stockid
					new String[]{}, //calculation
					filter, //
					orderBy,
					orderDirection},
				new String[]{"5"}, 
				byDayType.byCalendarDay
			);
	}
	
	public static SelectStrategy getNasdaqAllTimeLow(String thisDay, CrawlConf cconf, String outputFileDir){
		String sqlA = "select stockid, min(adjclose) from NasdaqFQHistory where dt<='%date' group by stockid";
		String sqlB = "select stockid, adjclose from NasdaqFQHistory where dt='%date'";
		String filter = "a>=b";
		return new CompareSelectStrategy("nasdaqAllTimeLow", outputFileDir, 
				new Object[]{
						new String[]{sqlA, sqlB}, 
						new String[]{"(b-a)/a"}, 
						filter, 
						"za", 
						"desc"},
				new String[]{});
	}
	
	public static SelectStrategy getNasdaqBreakIssue(String outputFileDir){
		String sqlA = "select stockid, ipoprice from NasdaqIPO";
		String sqlB = "select FQ.stockid, FQ.adjclose/FQ.close from (select stockid, min(dt) as ipoDt from NasdaqFQHistory group by stockid) ipoFQ, NasdaqFQHistory FQ where FQ.stockid=ipoFQ.stockid and FQ.dt=ipoFQ.ipoDt";
		String sqlC = "select stockid, adjclose from NasdaqFQHistory where dt='%date'"; //ForwardFQ
		return new CompareSelectStrategy("nasdaqBreakIPO", outputFileDir, 
				new Object[]{
						new String[]{sqlA, sqlB, sqlC}, 
						new String[]{"(a*b-c)/c"}, 
						"a*b>c", 
						"za", 
						"desc"},
				new String[]{});
	}
	*/
}
