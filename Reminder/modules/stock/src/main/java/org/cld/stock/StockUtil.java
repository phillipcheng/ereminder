package org.cld.stock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.hadooputil.DefaultCopyTextReducer;
import org.cld.stock.hk.HKStockConfig;
import org.cld.stock.nasdaq.NasdaqStockConfig;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SelectStrategyByStockTask;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.SellStrategyByStockMapper;
import org.cld.stock.strategy.SellStrategyByStockReducer;
import org.cld.stock.strategy.SortMapper;
import org.cld.stock.strategy.StockIdDateGroupingComparator;
import org.cld.stock.strategy.StockIdDatePair;
import org.cld.stock.strategy.StockIdDatePartitioner;
import org.cld.stock.strategy.StrategyResultMapper;
import org.cld.stock.strategy.StrategyResultReducer;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.DateTimeUtil;
import org.cld.util.FileDataMapper;
import org.cld.util.JsonUtil;
import org.cld.util.StringUtil;


public class StockUtil {
	public static final String SINA_STOCK_BASE="sina";
	public static final String NASDAQ_STOCK_BASE="nasdaq";
	public static final String HK_STOCK_BASE="hk";

	public static final String KEY_BASE_MARKET_ID="baseMarketId";
	
	protected static Logger logger =  LogManager.getLogger(StockUtil.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static StockConfig getStockConfig(String stockBase){
		if (SINA_STOCK_BASE.equals(stockBase)){
			return new SinaStockConfig();
		}else if (NASDAQ_STOCK_BASE.equals(stockBase)){
			return new NasdaqStockConfig();
		}else if (HK_STOCK_BASE.equals(stockBase)){
			return new HKStockConfig();
		}else{
			logger.error(String.format("stockBase %s not supported.", stockBase));
			return null;
		}
	}
	
	//[fromDate, toDate)
	public static LinkedList<Date> getOpenDayList(Date fromDate, Date toDate, Set<Date> holidays){
		LinkedList<Date> dll = new LinkedList<Date>();
		Date d = fromDate;
		while (d.before(toDate)){
		//while (!toDate.after(d)){
			if (isOpenDay(d, holidays)){
				dll.add(d);
			}
			d = getNextOpenDay(d, holidays);
		}
		return dll;
	}
	//get up to n open days
	public static Date getNextOpenDay(Date d, Set<Date> holidays, int n){
		Date nd = d;
		for(int i=0;i<n;i++){
			nd = getNextOpenDay(nd, holidays);
		}
		return nd;
	}
	//get 1 next open days
	public static Date getNextOpenDay(Date d, Set<Date> holidays){
		Date day = DateTimeUtil.tomorrow(d);
		Calendar c = Calendar.getInstance();
		c.setTime(day);
		while (!isOpenDay(day, holidays)){
			int dow = c.get(Calendar.DAY_OF_WEEK);
			if (dow==Calendar.SUNDAY){
				c.add(Calendar.DATE, +1);
			}else if (dow == Calendar.SATURDAY){
				c.add(Calendar.DATE, +2);
			}
			if (holidays.contains(c.getTime())){
				c.add(Calendar.DATE, +1);
			}
			day = c.getTime();
		}
		return day;
	}
	
	//including today
	public static Date getLastOpenDay(Date d, Set<Date> holidays){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		Date day = d;
		while (!isOpenDay(day, holidays)){
			int dow = c.get(Calendar.DAY_OF_WEEK);
			//check weekend
			if (dow==Calendar.SUNDAY){
				c.add(Calendar.DATE, -2);
			}else if (dow == Calendar.SATURDAY){
				c.add(Calendar.DATE, -1);
			}
			if (holidays.contains(c.getTime())){
				c.add(Calendar.DATE, -1);
			}
			day = c.getTime();
		}
		return day;
	}
	
	public static boolean isOpenDay(Date d, Set<Date> holidays){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int dow = c.get(Calendar.DAY_OF_WEEK);
		//check weekend
		if (dow==Calendar.SUNDAY ||
				dow == Calendar.SATURDAY ||
					holidays.contains(c.getTime())){
			return false;
		}else{
			return true;
		}
	}

	public static String getDate(int year, int quarter){
		String dt = null;
		if (quarter == 1){
			dt = "03-31";
		}else if (quarter ==2){
			dt = "06-30";
		}else if (quarter == 3){
			dt = "09-30";
		}else if (quarter == 4){
			dt = "12-31";
		}else{
			logger.error(String.format("wrong quarter %d", quarter));
		}
		return year + "-" + dt;
	}
	
	public static List<CqIndicators> getData(CrawlConf cconf, StockDataConfig sdcfg, SelectStrategy bs){
		FileDataMapper fdMapper = null;
		StockConfig sc = StockUtil.getStockConfig(sdcfg.getBaseMarketId());
		if (sdcfg.getUnit()==IntervalUnit.day){
			fdMapper = sc.getBTFQDailyQuoteMapper();
		}else if (sdcfg.getUnit()==IntervalUnit.minute){
			fdMapper = sc.getBTFQMinuteQuoteMapper();
		}else{
			logger.error(String.format("unit not supported: %s", sdcfg.getUnit()));
		}
		List<CandleQuote> cql = (List<CandleQuote>) StockPersistMgr.getBTDByStockDate(cconf, fdMapper, sdcfg.getStockId(), 
				sdcfg.getStartDt(), sdcfg.getEndDt());
		return CqIndicators.addIndicators(null, null, cql, bs);
	}
	
	public static final String STRATEGY_PREFIX="strategy.";
	public static final String STRATEGY_SUFFIX=".properties";
	public static final String STRATEGY_NAMES_SEP="_";//can't be , :
	public static final String GEN_DETAIL_FILE="gendetailfile";
	public static int NUM_REDUCER= 200;
	public static final int mapMbMem = 1024;
	public static final int reduceMbMemSellStrategy = 1024;
	public static final int reduceMbMemStrategyResult = 2048;
	public static void validateAllStrategyByStock(String propFile, CrawlConf cconf, String baseMarketId, String marketId, Date startDate, Date endDate, 
			String snParam, String stepParam){
		String folderName = null;
		if (snParam!=null){
			String strParam = snParam.replace(STRATEGY_NAMES_SEP, "_");
			folderName = String.format("%s_%s_%s_%s", marketId, sdf.format(startDate), sdf.format(endDate), strParam);
		}else{
			folderName = String.format("%s_%s_%s", marketId, sdf.format(startDate), sdf.format(endDate));
		}
		
		String outputDir1 = String.format("/reminder/sresult/%s/all1", folderName);
		String outputDir2 = String.format("/reminder/sresult/%s/all2", folderName);
		String outputDir3 = String.format("/reminder/sresult/%s/all3", folderName);
		String outputDir4 = String.format("/reminder/sresult/%s/all4", folderName);
		
		try{
			StockConfig sc = StockUtil.getStockConfig(baseMarketId);
			Map<String, Object> sssMap = new HashMap<String, Object>();
			String[] strategyNames;
			if (snParam==null){
				strategyNames = sc.getAllStrategy();
			}else{
				strategyNames = snParam.split(STRATEGY_NAMES_SEP);
			}
			int totalSS = 0;
			List<SelectStrategy> allSelectStrategy = new ArrayList<SelectStrategy>();
			int maxSelectNumber=0;
			for (String sn:strategyNames){
				String strategyName = STRATEGY_PREFIX+sn+STRATEGY_SUFFIX;
				PropertiesConfiguration props = new PropertiesConfiguration(strategyName);
				List<SelectStrategy> scsl = SelectStrategy.gen(props, sn, baseMarketId);
				allSelectStrategy.addAll(scsl);
				SellStrategy[] slsl = SellStrategy.gen(props);
				for (SellStrategy ss: slsl){
					if (ss.getSelectNumber()>maxSelectNumber){
						maxSelectNumber = ss.getSelectNumber();
					}
				}
				sssMap.put(sn, slsl);
				totalSS+=slsl.length;
			}
			
			String sssString = JsonUtil.ObjToJson(sssMap);
			SelectStrategy[] allSelectStrategyArray = new SelectStrategy[allSelectStrategy.size()];
			int totalBS = allSelectStrategy.size();
			int totalFiles = totalBS * totalSS;
			if (totalBS==1){
				NUM_REDUCER=5;
			}
			boolean genDetailFiles=true;
			if (totalFiles>7000){
				genDetailFiles = false;
			}
			logger.info(String.format("total files: %d, select strategy:%d, sell strategy:%d", totalFiles, totalBS, totalSS));
			Map<String, String> hadoopParams = new HashMap<String, String>();
			if (stepParam==null || "1".equals(stepParam)){
				allSelectStrategyArray = allSelectStrategy.toArray(allSelectStrategyArray);
				SelectStrategyByStockTask.launch(propFile, cconf, baseMarketId, marketId, outputDir1, 
						allSelectStrategyArray, startDate, endDate, maxSelectNumber);
			}
			
			if (stepParam==null || "2".equals(stepParam)){
				hadoopParams.put(SellStrategy.KEY_SELL_STRATEGYS, sssString);
				hadoopParams.put(CrawlUtil.CRAWL_PROPERTIES, propFile);
				hadoopParams.put(KEY_BASE_MARKET_ID, baseMarketId);
				String mapOptValue = "-Xmx" + mapMbMem + "M";
				String reduceOptValue = "-Xmx" + reduceMbMemSellStrategy + "M";
				hadoopParams.put("mapreduce.map.speculative", "false");
				hadoopParams.put("mapreduce.map.memory.mb", mapMbMem + "");
				hadoopParams.put("mapreduce.map.java.opts", mapOptValue);
				hadoopParams.put("mapreduce.reduce.memory.mb", reduceMbMemSellStrategy + "");
				hadoopParams.put("mapreduce.reduce.java.opts", reduceOptValue);
				hadoopParams.put("mapreduce.job.reduces", NUM_REDUCER+"");
				HadoopTaskLauncher.executeTasks(cconf.getNodeConf(), hadoopParams, new String[]{outputDir1}, false, outputDir2, true, 
						SellStrategyByStockMapper.class, SellStrategyByStockReducer.class, 
						StockIdDatePartitioner.class, StockIdDateGroupingComparator.class, StockIdDatePair.class, false);
			}
			if (stepParam==null || "3".equals(stepParam)){
				//multiple output
				hadoopParams.clear();
				String mapOptValue = "-Xmx" + mapMbMem + "M";
				String reduceOptValue = "-Xmx" + reduceMbMemStrategyResult + "M";
				hadoopParams.put("mapreduce.map.speculative", "false");
				hadoopParams.put("mapreduce.map.memory.mb", mapMbMem + "");
				hadoopParams.put("mapreduce.map.java.opts", mapOptValue);
				hadoopParams.put("mapreduce.reduce.memory.mb", reduceMbMemStrategyResult + "");
				hadoopParams.put("mapreduce.reduce.java.opts", reduceOptValue);
				hadoopParams.put("mapreduce.job.reduces", NUM_REDUCER+"");
				hadoopParams.put(GEN_DETAIL_FILE, Boolean.toString(genDetailFiles));
				HadoopTaskLauncher.executeTasks(cconf.getNodeConf(), hadoopParams, new String[]{outputDir2}, true, outputDir3, true, 
						StrategyResultMapper.class, StrategyResultReducer.class, false);
			}
			if (stepParam==null || "4".equals(stepParam)){
				hadoopParams.clear();
				hadoopParams.put("mapreduce.job.reduces", 1+"");
				hadoopParams.put("mapred.output.key.comparator.class", "org.apache.hadoop.mapred.lib.KeyFieldBasedComparator");
				hadoopParams.put("mapred.text.key.comparator.options", "-n");
				hadoopParams.put("file.pattern", ".*part.*");
				HadoopTaskLauncher.executeTasks(cconf.getNodeConf(), hadoopParams, new String[]{outputDir3}, false, outputDir4, true, 
						SortMapper.class, DefaultCopyTextReducer.class, false);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}

}
