package org.cld.trade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

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
	public static final int default_size=1000;
	
	private StockConfig sc;
	private TradeDataMgr tdmgr;
	private Map<IntervalUnit, Integer> sizeThresholdMap = new HashMap<IntervalUnit, Integer>();
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
					String key = iu.name() + "." + KEY_SIZE_THRESHOLD;
					sizeThresholdMap.put(iu, pc.getInteger(key, default_size));
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}

	private void dumpFile(String symbol, IntervalUnit iu, Queue<? extends Object> oq, int size){
		if (oq.size()>size){
			TimedItem ti = (TimedItem) oq.peek();
			File f = new File(String.format("%s%s%s_%s_%s", dumpDir, File.separator, symbol, iu, sdf.format(ti.getDatetime())));
			BufferedWriter bw = null;
			try{
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
				for (int i=0; i<size; i++){
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
				Map<String, Queue<TradeTick>> ttMap = tdmgr.getHistoryTickMap();
				int tickSizeThreshold = sizeThresholdMap.get(IntervalUnit.tick);
				for (String symbol:ttMap.keySet()){
					Queue<TradeTick> ttq = ttMap.get(symbol);
					dumpFile(symbol, IntervalUnit.tick, ttq, tickSizeThreshold);
				}
				Map<IntervalUnit, Map<String, Queue<CandleQuote>>> icqMap = tdmgr.getHistoryCqMap();
				for (IntervalUnit iu: icqMap.keySet()){
					int sizeThreshold = sizeThresholdMap.get(iu);
					Map<String, Queue<CandleQuote>> cqMap = icqMap.get(iu);
					for (String symbol:cqMap.keySet()){
						Queue<CandleQuote> cqq = cqMap.get(symbol);
						dumpFile(symbol, iu, cqq, sizeThreshold);
					}
				}
				
				Thread.sleep(checkInterval*1000);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}

}
