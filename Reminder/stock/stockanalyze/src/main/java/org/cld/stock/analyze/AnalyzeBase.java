package org.cld.stock.analyze;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.hadooputil.DefaultCopyTextReducer;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.common.TradeHour;
import org.cld.stock.analyze.SellStrategyByStockMapper;
import org.cld.stock.analyze.SellStrategyByStockReducer;
import org.cld.stock.analyze.SortMapper;
import org.cld.stock.analyze.StockIdDateGroupingComparator;
import org.cld.stock.analyze.StockIdDatePair;
import org.cld.stock.analyze.StockIdDatePartitioner;
import org.cld.stock.analyze.StrategyResultMapper;
import org.cld.stock.analyze.StrategyResultReducer;
import org.cld.stock.strategy.BuySellRecord;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.StrategyResult;
import org.cld.stock.strategy.TradeStrategy;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.JsonUtil;
import org.cld.util.SafeSimpleDateFormat;
import org.cld.util.StringUtil;

public class AnalyzeBase implements Runnable{

	protected static Logger logger =  LogManager.getLogger(AnalyzeBase.class);
	
	public static SafeSimpleDateFormat sdf = new SafeSimpleDateFormat("yyyy-MM-dd");
	private static SafeSimpleDateFormat msdf = new SafeSimpleDateFormat("yyyy-MM-dd HH:mm");
	private static SafeSimpleDateFormat ssdf = new SafeSimpleDateFormat("yyyyMMddHHmmss");

	public static final String STRATEGY_PREFIX="strategy.";
	public static final String STRATEGY_SUFFIX=".properties";
	
	private AnalyzeResult ar;
	private String symbol;
	private String analyzePropFile;
	private String strategyName;
	private String startDt;
	private String endDt;
	private String baseMarketId;
	private TradeHour th;
	
	private static Date convertDate(String date){
		try {
			Date d;
			if (date.contains(":")){
				d = msdf.parse(date);
			}else{
				d = sdf.parse(date);
			}
			return d;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	public AnalyzeBase(AnalyzeResult ar, String baseMarketId, String symbol, String analyzePropFile, 
			String strategyName, String startDt, String endDt, TradeHour th){
		this.ar = ar;
		this.symbol = symbol;
		this.analyzePropFile = analyzePropFile;
		this.strategyName = strategyName;
		this.startDt = startDt;
		this.endDt = endDt;
		this.baseMarketId = baseMarketId;
		this.th = th;
	}
	
	@Override
	public void run() {
		execute(ar, symbol, analyzePropFile, baseMarketId, strategyName, startDt, endDt, th);
	}

	public static void execute(AnalyzeResult ar, String symbol, String analyzePropFile, String baseMarketId, 
			String strategyPropFileName, String startDt, String endDt, TradeHour th){
		try {
			List<SelectStrategy> bsl = SelectStrategy.genList(new PropertiesConfiguration(strategyPropFileName), strategyPropFileName, 
					baseMarketId, null);
			SellStrategy[] ssa = SellStrategy.gen(new PropertiesConfiguration(strategyPropFileName));
			Date startDate = convertDate(startDt);
			Date endDate = convertDate(endDt);
			Map<TradeStrategy, SummaryStatistics> tradeStatBySymbol = new HashMap<TradeStrategy, SummaryStatistics>();
			StockConfig sc = StockUtil.getStockConfig(baseMarketId);
			AnalyzeConf aconf = (AnalyzeConf) TaskUtil.getTaskConf(analyzePropFile);
			List<Object[]> kvl = SelectStrategyByStockTask.getBuyOppList(aconf, bsl, symbol, startDate, endDate, th, null);
			for (Object[] kv:kvl){
				SelectCandidateResult scr = (SelectCandidateResult) kv[0];
				SelectStrategy bs = (SelectStrategy) kv[1];
				for (SellStrategy ss:ssa){
					scr.setSymbol(symbol);
					BuySellRecord bsr = TradeSimulator.trade(scr, ss, sc, aconf, th);
					logger.info(String.format("scr:%s, bs:%s, ss:%s, bsr:%s", scr, bs.paramsToString(), ss.toString(), bsr.toString()));
					TradeStrategy ts = new TradeStrategy(bs, ss);
					SummaryStatistics sss = tradeStatBySymbol.get(ts);
					if (sss == null){
						sss = new SummaryStatistics();
						tradeStatBySymbol.put(ts, sss);
					}
					sss.addValue(bsr.getPercent());
				}
			}
			for (TradeStrategy ts: tradeStatBySymbol.keySet()){
				SummaryStatistics sss = tradeStatBySymbol.get(ts);
				ar.addResult(symbol, ts.getBs(), ts.getSs(), new StrategyResult(sss.getN(), sss.getMean()));
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public static final String POOL_SIZE_KEY="ps";
	public static final int DEFAULT_POOL_SIZE=10;
	public static AnalyzeResult validateStrategiesLocal(String analyzePropertyFile, String baseMarketId, String startDt, String endDt, 
			String strategyName, TradeHour th, String resultBy, int poolSize){
		return validateStrategiesLocal(analyzePropertyFile, baseMarketId, startDt, endDt, strategyName, th, resultBy, poolSize, null);
	}
	
	public static AnalyzeResult validateStrategiesLocal(String analyzePropertyFile, String baseMarketId, String startDt, String endDt, 
			String strategyName, TradeHour th, String resultBy, int poolSize, String inSymbol){
		try{
			AnalyzeConf aconf = (AnalyzeConf) TaskUtil.getTaskConf(analyzePropertyFile);
			AnalyzeResult ar = new AnalyzeResult();
			ExecutorService es = Executors.newFixedThreadPool(poolSize);
			String strategyPropFileName = STRATEGY_PREFIX + strategyName + STRATEGY_SUFFIX;
			PropertiesConfiguration strategyProperties = new PropertiesConfiguration(strategyPropFileName);
			Map<String, List<SelectStrategy>> scsl = SelectStrategy.genMap(strategyProperties, strategyName, baseMarketId, aconf.getDbconf());
			if (inSymbol==null){
				for (String symbol:scsl.keySet()){
					AnalyzeBase ab = new AnalyzeBase(ar, baseMarketId, symbol, analyzePropertyFile, strategyPropFileName, startDt, endDt, th);
					es.submit(ab);
				}
			}else{
				AnalyzeBase ab = new AnalyzeBase(ar, baseMarketId, inSymbol, analyzePropertyFile, strategyPropFileName, startDt, endDt, th);
				es.submit(ab);
			}
			es.shutdown();
			es.awaitTermination(2, TimeUnit.HOURS);
			Date now = new Date();
			String fileName = String.format("%s%s%s_%s.txt", aconf.getBtOutputFolder(), File.separator, strategyName, ssdf.format(now));
			Map<String, StrategyResult> strategyResults = null;
			if (BY_STRATEGY.equals(resultBy)){
				strategyResults = ar.getOrderedResultByStrategy();
			}else if (BY_SYMBOL.equals(resultBy)){
				strategyResults = ar.getOrderedResultBySymbol();
			}else{
				logger.error(String.format("resultBy %s not supported.", resultBy));
			}
			try {
				if (strategyResults!=null){
					File file = new File(fileName);
					BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
					for (Map.Entry<String, StrategyResult> ssr:strategyResults.entrySet()){
						//this map can't use get, because the customized compare (RateValueComparator) never return 0
						bw.write(String.format("%s,%s", ssr.getKey(), ssr.getValue()));
						bw.newLine();
					}
					bw.close();
				}
			} catch (IOException e) {
				logger.error("", e);
			}
			return ar;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}

	public static final String KEY_BASE_MARKET_ID="baseMarketId";
	public static final String STRATEGY_NAMES_SEP="_";//can't be , :
	public static final String STRATEGY_NAME_KEY="sn";
	public static final String STEP_KEY="step";
	public static final String TRADE_HOUR_KEY="th";
		public static final String TH_NORMAL="normal";
		public static final String TH_ALL="all";
	public static final String RESULT_BY="resultBy";
		public static final String BY_STRATEGY="strategy";
		public static final String BY_SYMBOL="symbol";
	public static final String GEN_DETAIL_FILE="gendetailfile";
	public static int NUM_REDUCER= 200;
	public static final int mapMbMem = 1024;
	public static final int reduceMbMemSellStrategy = 1024;
	public static final int reduceMbMemStrategyResult = 2048;
	
	public static void validateStrategiesHadoop(String propFile, AnalyzeConf aconf, String baseMarketId, String startDt, String endDt, 
			String snParam, String stepParam, TradeHour th, String strResultBy){
		String folderName = null;
		if (snParam!=null){
			folderName = String.format("%s_%s_%s_%s", baseMarketId, startDt, endDt, snParam);
		}else{
			folderName = String.format("%s_%s_%s", baseMarketId, startDt, endDt);
		}
		
		String outputDir1 = String.format("%s/%s/all1", aconf.getBtOutputFolder(), folderName);
		String outputDir2 = String.format("%s/%s/all2", aconf.getBtOutputFolder(), folderName);
		String outputDir3 = String.format("%s/%s/all3", aconf.getBtOutputFolder(), folderName);
		String outputDir4 = String.format("%s/%s/all4", aconf.getBtOutputFolder(), folderName);
		
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
			Map<String, List<SelectStrategy>> allSelectStrategy = new HashMap<String, List<SelectStrategy>>();
			int maxSelectNumber=0;
			for (String sn:strategyNames){
				String strategyName = STRATEGY_PREFIX+sn+STRATEGY_SUFFIX;
				PropertiesConfiguration props = new PropertiesConfiguration(strategyName);
				Map<String, List<SelectStrategy>> scsl = SelectStrategy.genMap(props, sn, baseMarketId, aconf.getDbconf());
				for (String symbol:scsl.keySet()){
					List<SelectStrategy> bsl = allSelectStrategy.get(symbol);
					if (bsl==null){
						bsl = new ArrayList<SelectStrategy>();
						allSelectStrategy.put(symbol, bsl);
					}
					bsl.addAll(scsl.get(symbol));
				}
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
			Date startDate = convertDate(startDt);
			Date endDate = convertDate(endDt);
			if (stepParam==null || "1".equals(stepParam)){//find buy opportunities
				SelectStrategyByStockTask.launch(propFile, aconf, outputDir1, allSelectStrategy, startDate, endDate, maxSelectNumber, th);
			}
			
			if (stepParam==null || "2".equals(stepParam)){//simulate the transactions
				hadoopParams.put(SellStrategy.KEY_SELL_STRATEGYS, sssString);
				hadoopParams.put(TaskUtil.TASKCONF_PROPERTIES, propFile);
				hadoopParams.put(KEY_BASE_MARKET_ID, baseMarketId);
				String mapOptValue = "-Xmx" + mapMbMem + "M";
				String reduceOptValue = "-Xmx" + reduceMbMemSellStrategy + "M";
				hadoopParams.put("mapreduce.map.speculative", "false");
				hadoopParams.put("mapreduce.map.memory.mb", mapMbMem + "");
				hadoopParams.put("mapreduce.map.java.opts", mapOptValue);
				hadoopParams.put("mapreduce.reduce.memory.mb", reduceMbMemSellStrategy + "");
				hadoopParams.put("mapreduce.reduce.java.opts", reduceOptValue);
				hadoopParams.put("mapreduce.job.reduces", NUM_REDUCER+"");
				HadoopTaskLauncher.hadoopExecuteTasks(aconf, hadoopParams, new String[]{outputDir1}, false, outputDir2, true, 
						SellStrategyByStockMapper.class, SellStrategyByStockReducer.class, 
						StockIdDatePartitioner.class, StockIdDateGroupingComparator.class, StockIdDatePair.class, false);
			}
			if (stepParam==null || "3".equals(stepParam)){//merge the result by strategy
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
				hadoopParams.put(RESULT_BY, strResultBy);
				HadoopTaskLauncher.hadoopExecuteTasks(aconf, hadoopParams, new String[]{outputDir2}, true, outputDir3, true, 
						StrategyResultMapper.class, StrategyResultReducer.class, false);
			}
			if (stepParam==null || "4".equals(stepParam)){//sort the result by strategy
				hadoopParams.clear();
				hadoopParams.put("mapreduce.job.reduces", 1+"");
				hadoopParams.put("mapred.output.key.comparator.class", "org.apache.hadoop.mapred.lib.KeyFieldBasedComparator");
				hadoopParams.put("mapred.text.key.comparator.options", "-n");
				hadoopParams.put("file.pattern", ".*part.*");
				HadoopTaskLauncher.hadoopExecuteTasks(aconf, hadoopParams, new String[]{outputDir3}, false, outputDir4, true, 
						SortMapper.class, DefaultCopyTextReducer.class, false);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public static void validateStrategies(String propFile, String baseMarketId, String startDt, String endDt, String params){
		AnalyzeConf aconf = (AnalyzeConf) TaskUtil.getTaskConf(propFile);
		Map<String, String> keyvalues = StringUtil.parseMapParams(params);
		String snParam = keyvalues.get(STRATEGY_NAME_KEY);
		String stepParam = keyvalues.get(STEP_KEY);
		String strTradeHour = keyvalues.get(TRADE_HOUR_KEY);
		String strResultBy = keyvalues.get(RESULT_BY);
		TradeHour th = TradeHour.Normal;
		int poolSize = DEFAULT_POOL_SIZE;
		if (keyvalues.containsKey(POOL_SIZE_KEY)){
			poolSize = Integer.parseInt(keyvalues.get(POOL_SIZE_KEY));
		}
		if (strTradeHour!=null){
			if (TH_ALL.equals(strTradeHour)){
				th = TradeHour.All;
			}
		}
		if (AnalyzeConf.BT_HADOOP.equals(aconf.getBtMode())){
			validateStrategiesHadoop(propFile, aconf, baseMarketId, startDt, endDt, snParam, stepParam, th, strResultBy);
		}else if (AnalyzeConf.BT_LOCAL.equals(aconf.getBtMode())){
			validateStrategiesLocal(propFile, baseMarketId, startDt, endDt, snParam, th, strResultBy, poolSize);
		}
	}
}
