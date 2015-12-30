package org.cld.stock.analyze;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.hadooputil.DefaultCopyTextReducer;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.common.TradeHour;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.analyze.SellStrategyByStockMapper;
import org.cld.stock.analyze.SellStrategyByStockReducer;
import org.cld.stock.analyze.SortMapper;
import org.cld.stock.analyze.StockIdDateGroupingComparator;
import org.cld.stock.analyze.StockIdDatePair;
import org.cld.stock.analyze.StockIdDatePartitioner;
import org.cld.stock.analyze.StrategyResultMapper;
import org.cld.stock.analyze.StrategyResultReducer;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.JsonUtil;
import org.cld.util.StringUtil;
import org.cld.util.jdbc.DBConnConf;

public class AnalyzeBase {

	protected static Logger logger =  LogManager.getLogger(AnalyzeBase.class);
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public static final String KEY_BASE_MARKET_ID="baseMarketId";
	public static final String STRATEGY_PREFIX="strategy.";
	public static final String STRATEGY_SUFFIX=".properties";
	public static final String STRATEGY_NAMES_SEP="_";//can't be , :
	public static final String STRATEGY_NAME_KEY="sn";
	public static final String STEP_KEY="step";
	public static final String TRADE_HOUR_KEY="th";
		public static final String TH_NORMAL="normal";
		public static final String TH_ALL="all";
	public static final String GEN_DETAIL_FILE="gendetailfile";
	public static int NUM_REDUCER= 200;
	public static final int mapMbMem = 1024;
	public static final int reduceMbMemSellStrategy = 1024;
	public static final int reduceMbMemStrategyResult = 2048;
	
	public static void validateAllStrategyByStock(String propFile, AnalyzeConf aconf, String baseMarketId, Date startDate, Date endDate, 
			String snParam, String stepParam, TradeHour th){
		String folderName = null;
		if (snParam!=null){
			String strParam = snParam.replace(STRATEGY_NAMES_SEP, "_");
			folderName = String.format("%s_%s_%s_%s", baseMarketId, sdf.format(startDate), sdf.format(endDate), strParam);
		}else{
			folderName = String.format("%s_%s_%s", baseMarketId, sdf.format(startDate), sdf.format(endDate));
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
			if (stepParam==null || "1".equals(stepParam)){
				SelectStrategyByStockTask.launch(propFile, aconf, outputDir1, allSelectStrategy, startDate, endDate, maxSelectNumber, th);
			}
			
			if (stepParam==null || "2".equals(stepParam)){
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
				HadoopTaskLauncher.hadoopExecuteTasks(aconf, hadoopParams, new String[]{outputDir2}, true, outputDir3, true, 
						StrategyResultMapper.class, StrategyResultReducer.class, false);
			}
			if (stepParam==null || "4".equals(stepParam)){
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
	
	public static void validateAllStrategyByStock(String propFile, AnalyzeConf aconf, String baseMarketId, Date startDate, Date endDate, 
			String params){
		Map<String, String> keyvalues = StringUtil.parseMapParams(params);
		String snParam = keyvalues.get(STRATEGY_NAME_KEY);
		String stepParam = keyvalues.get(STEP_KEY);
		String strTradeHour = keyvalues.get(TRADE_HOUR_KEY);
		TradeHour th = TradeHour.All;
		if (strTradeHour!=null){
			if (TH_NORMAL.equals(strTradeHour)){
				th = TradeHour.Normal;
			}
		}
		validateAllStrategyByStock(propFile, aconf, baseMarketId, startDate, endDate, snParam, stepParam, th);
	}
	
	public static String[] getSybmolsByMarketId(DBConnConf dbconf, String baseMarketId, String subsector, String country){
		String tableName=null;
		if ("nasdaq".equals(baseMarketId)){
			tableName = "NasdaqIds";
		}else if ("sina".equals(baseMarketId)){
			tableName = "SinaStockIds";
		}else{
			logger.error(String.format("base market %s not supported.", baseMarketId));
		}
		List<String> symbols = StockPersistMgr.getStockIds(dbconf, tableName, subsector, country);
		String[] symarray = new String[symbols.size()];
		symbols.toArray(symarray);
		return symarray;
	}
}
