package org.cld.stock.analyze.test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.analyze.AnalyzeBase;
import org.cld.stock.analyze.AnalyzeConf;
import org.cld.stock.analyze.SelectStrategyByStockTask;
import org.cld.stock.analyze.TradeSimulator;
import org.cld.stock.common.CandleQuote;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.common.TradeHour;
import org.cld.stock.mapper.ext.NasdaqFileFQMinuteMapper;
import org.cld.stock.strategy.BuySellResult;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.StrategyConst;
import org.cld.taskmgr.TaskUtil;
import org.cld.util.JsonUtil;
import org.junit.Test;

public class TestStock {
	private static Logger logger =  LogManager.getLogger(TestStock.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@Test
	public void testNormalHour() throws Exception{
		String line = "08/04/2011,16:42,1.22,1.22,1.22,1.22,13889";
		NasdaqFileFQMinuteMapper fileMapper = NasdaqFileFQMinuteMapper.getInstance();
		CandleQuote cq = (CandleQuote) fileMapper.getObject(line);
		assertFalse(StockUtil.filterByTradeHour(cq, TradeHour.Normal));
	}
	@Test
	public void testClone() throws Exception{
		String sn = "strategy.simple.properties";
		List<SelectStrategy> ssl = SelectStrategy.genList(new PropertiesConfiguration(sn), sn, "nasdaq");
		SelectStrategy bs = ssl.get(0);
		logger.info(JsonUtil.ObjToJson(bs));
		SelectStrategy abs = (SelectStrategy) JsonUtil.deepClone(bs);
		logger.info(JsonUtil.ObjToJson(abs));
	}
	
	@Test
	public void testBuySell() throws Exception {
		String pFile = "analyze.local.properties";
		AnalyzeConf aconf = (AnalyzeConf) TaskUtil.getTaskConf(pFile);
		Date startDate = msdf.parse("2015-05-01 10:00");
		Date endDate = msdf.parse("2015-12-01 20:20");
		String sn = "simpleone";
		AnalyzeBase.validateAllStrategyByStock(pFile, aconf, "nasdaq", startDate, endDate, sn, null, TradeHour.Normal);
	}
	
	@Test
	public void testBuySellOverTrade() throws Exception {
		String pFile = "analyze.local.properties";
		AnalyzeConf aconf = (AnalyzeConf) TaskUtil.getTaskConf(pFile);
		Date startDate = msdf.parse("2015-05-01 10:00");
		Date endDate = msdf.parse("2015-12-01 20:20");
		String sn = "overtradeone";
		AnalyzeBase.validateAllStrategyByStock(pFile, aconf, "nasdaq", startDate, endDate, sn, null, TradeHour.Normal);
	}
	
	@Test
	public void testBuyOvertrade() throws Exception{
		String pFile = "analyze.properties";
		AnalyzeConf aconf = (AnalyzeConf) TaskUtil.getTaskConf(pFile);
		Date startDate = sdf.parse("2011-04-06");
		Date endDate = sdf.parse("2012-04-07");
		String sn = "strategy.overtradeone.properties";
		List<SelectStrategy> ssl = SelectStrategy.genList(new PropertiesConfiguration(sn), sn, "nasdaq");
		List<Object[]> kvl = SelectStrategyByStockTask.getBuyOppList(aconf, ssl, "AAPL", startDate, endDate, TradeHour.Normal, null);
		for (Object[] kv:kvl){
			SelectCandidateResult scr = (SelectCandidateResult) kv[0];
			SelectStrategy bs = (SelectStrategy) kv[1];
			logger.info(String.format("scr:%s, bs:%s", scr, bs.paramsToString()));
		}
	}
	
	@Test
	public void testBuySimple() throws Exception{
		String pFile = "analyze.properties";
		AnalyzeConf aconf = (AnalyzeConf) TaskUtil.getTaskConf(pFile);
		Date startDate = msdf.parse("2014-08-14 09:40");
		Date endDate = msdf.parse("2014-08-14 16:00");
		String sn = "strategy.simple.properties";
		List<SelectStrategy> ssl = SelectStrategy.genList(new PropertiesConfiguration(sn), sn, "nasdaq");
		List<Object[]> kvl = SelectStrategyByStockTask.getBuyOppList(aconf, ssl, "GPRO", startDate, endDate, TradeHour.Normal, null);
		for (Object[] kv:kvl){
			SelectCandidateResult scr = (SelectCandidateResult) kv[0];
			SelectStrategy bs = (SelectStrategy) kv[1];
			logger.info(String.format("scr:%s, bs:%s", scr, bs.paramsToString()));
		}
	}
	
	@Test
	public void testBuyWShape() throws Exception{
		String pFile = "analyze.properties";
		AnalyzeConf aconf = (AnalyzeConf) TaskUtil.getTaskConf(pFile);
		Date startDate = sdf.parse("2011-04-06");
		Date endDate = sdf.parse("2011-04-07");
		String sn = "strategy.wshapeone.properties";
		List<SelectStrategy> ssl = SelectStrategy.genList(new PropertiesConfiguration(sn), sn, "nasdaq");
		List<Object[]> kvl = SelectStrategyByStockTask.getBuyOppList(aconf, ssl, "AAPL", startDate, endDate, TradeHour.Normal, null);
		for (Object[] kv:kvl){
			SelectCandidateResult scr = (SelectCandidateResult) kv[0];
			SelectStrategy bs = (SelectStrategy) kv[1];
			logger.info(String.format("scr:%s, bs:%s", scr, bs.paramsToString()));
		}
	}
	
	@Test
	public void testSell() throws Exception{
		String pFile = "analyze.properties";
		AnalyzeConf aconf = (AnalyzeConf) TaskUtil.getTaskConf(pFile);
		StockConfig sc = StockUtil.getStockConfig(StockUtil.NASDAQ_STOCK_BASE);
		String[] stockids=new String[]{"CETC"};
		SellStrategy ss = new SellStrategy(1, StrategyConst.V_UNIT_DAY, 3, 9f, 1f, true);
		for (String stockid:stockids){
			SelectCandidateResult scr = new SelectCandidateResult(stockid, 
					sc.getNormalTradeStartTime(sdf.parse("2011-04-26")), 0, 9.95f*1.005f);
			BuySellResult bsr = TradeSimulator.trade(scr, ss, sc, aconf, TradeHour.Normal);
			logger.info(bsr);
		}
	}
}
