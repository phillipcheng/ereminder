package org.cld.trade;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.TradeTick;
import org.cld.trade.response.Quote;

public class StreamSimulator implements Runnable{
	
	private static Logger logger =  LogManager.getLogger(StreamSimulator.class);
	public static final int REQUEST_MAX_SYMBOLS=250;
	private String[] symbols;
	private TradeKingConnector tm;
	private TradeDataMgr tdm;
	private int threadNum =0;
	
	Map<String, Long> lastTickMap = new ConcurrentHashMap<String, Long>();
	
	public StreamSimulator(List<String> symbols, TradeKingConnector tm, TradeDataMgr tdm, int totalThreadNumber){
		this.symbols = new String[symbols.size()];
		this.symbols = symbols.toArray(this.symbols);
		this.tm = tm;
		this.tdm = tdm;
		this.threadNum = totalThreadNumber;
	}
	
	@Override
	public void run() {
		try{
			while (true){
				List<Quote> ql = tm.getQuotes(symbols);
				if (ql!=null){
					for (Quote q:ql){
						Long acumVol = lastTickMap.get(q.getSymbol());
						if ( acumVol!=null && acumVol!=q.getCumuVol()
								|| acumVol==null){
							TradeTick tt = q.toTradeTick();
							tdm.accept(q.getSymbol(), tt);
							lastTickMap.put(q.getSymbol(), q.getCumuVol());
						}
					}
				}
				Thread.sleep(1000*threadNum);//limitation maximum admitted 60 per Minute
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
}