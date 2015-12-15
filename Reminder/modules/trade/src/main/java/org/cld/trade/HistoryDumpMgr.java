package org.cld.trade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.CandleQuote;
import org.cld.stock.StockConfig;
import org.cld.stock.TimedItem;
import org.cld.stock.TradeTick;
import org.cld.stock.strategy.IntervalUnit;

public class HistoryDumpMgr implements Runnable {
	private static Logger logger =  LogManager.getLogger(HistoryDumpMgr.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static final String KEY_DUMP_DIR="dump.dir";
	public static final String KEY_CHECK_INTERAL="check.interval";
	public static final String KEY_SIZE_THRESHOLD="size";
	public static final String KEY_INTERVAL_THRESHOLD="interval";
	public static final int default_size=1000;
	public static final String default_interval="PT1H";
	
	private StockConfig sc;
	private TradeDataMgr tdmgr;
	private Map<IntervalUnit, Integer> sizeThresholdMap = new HashMap<IntervalUnit, Integer>();
	private Map<IntervalUnit, Duration> intervalThresholdMap = new HashMap<IntervalUnit, Duration>();
	private String dumpDir;
	private int checkInterval;//unit second
	
	public HistoryDumpMgr(String confProp, TradeDataMgr tdmgr, StockConfig sc){
		this.tdmgr = tdmgr;
		this.sc = sc;
		try{
			PropertiesConfiguration pc = new PropertiesConfiguration(confProp);
			dumpDir = pc.getString(KEY_DUMP_DIR);
			checkInterval = pc.getInt(KEY_CHECK_INTERAL);
			for (IntervalUnit iu: IntervalUnit.values()){
				if (iu!=IntervalUnit.unspecified){
					String sizeKey = iu.name() + "." + KEY_SIZE_THRESHOLD;
					sizeThresholdMap.put(iu, pc.getInteger(sizeKey, default_size));
					String intervalKey = iu.name() + "." + KEY_INTERVAL_THRESHOLD;
					String strInterval = pc.getString(intervalKey, default_interval);
					Duration d = Duration.parse(strInterval);
					intervalThresholdMap.put(iu, d);
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}

	private void dumpFile(String symbol, IntervalUnit iu, Deque<? extends Object> oq, int size, Duration interval){
		TimedItem lastti = (TimedItem) oq.peekLast();
		TimedItem ti = (TimedItem) oq.peek();
		boolean overdue = false;
		if (lastti!=null && ti!=null){
			long elapse = lastti.getDatetime().getTime() - ti.getDatetime().getTime();
			if (elapse>interval.getSeconds()*1000){
				overdue = true;
			}
		}
		if (oq.size()>size || overdue){
			File f = new File(String.format("%s%s%s_%s_%s", dumpDir, File.separator, symbol, iu, sdf.format(ti.getDatetime())));
			BufferedWriter bw = null;
			try{
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
				int originalSize = oq.size();
				for (int i=0; i<originalSize; i++){
					ti = (TimedItem) oq.remove();
					bw.write(ti.toCsv(sc.getTimeZone()) + "\n");
				}
			}catch(Exception e){
				logger.error("", e);
			}finally{
				if (bw!=null){
					try{
						bw.close();
					}catch(Exception e1){
						logger.error("error in close bw", e1);
					}
				}
			}
		}
	}
	@Override
	public void run() {
		try{
			sdf.setTimeZone(sc.getTimeZone());
			while(true){
				Map<String, Deque<TradeTick>> ttMap = tdmgr.getHistoryTickMap();
				int tickSizeThreshold = sizeThresholdMap.get(IntervalUnit.tick);
				Duration tickIntervalThreshold = intervalThresholdMap.get(IntervalUnit.tick);
				for (String symbol:ttMap.keySet()){
					Deque<TradeTick> ttq = ttMap.get(symbol);
					dumpFile(symbol, IntervalUnit.tick, ttq, tickSizeThreshold, tickIntervalThreshold);
				}
				Map<IntervalUnit, Map<String, Deque<CandleQuote>>> icqMap = tdmgr.getHistoryCqMap();
				for (IntervalUnit iu: icqMap.keySet()){
					int sizeThreshold = sizeThresholdMap.get(iu);
					Duration intervalThreshold = intervalThresholdMap.get(iu);
					Map<String, Deque<CandleQuote>> cqMap = icqMap.get(iu);
					for (String symbol:cqMap.keySet()){
						Deque<CandleQuote> cqq = cqMap.get(symbol);
						dumpFile(symbol, iu, cqq, sizeThreshold, intervalThreshold);
					}
				}
				
				Thread.sleep(checkInterval*1000);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}

}
