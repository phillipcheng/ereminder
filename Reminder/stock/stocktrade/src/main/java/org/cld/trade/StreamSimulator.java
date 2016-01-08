package org.cld.trade;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;
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
	
	private boolean running=true;
	private boolean extendedHour=true;
	
	Map<String, Long> lastTickMap = new ConcurrentHashMap<String, Long>();
	
	public StreamSimulator(List<String> symbols, TradeKingConnector tm, TradeDataMgr tdm, int totalThreadNumber, boolean extendedHour){
		this.symbols = new String[symbols.size()];
		this.symbols = symbols.toArray(this.symbols);
		this.tm = tm;
		this.tdm = tdm;
		this.threadNum = totalThreadNumber;
		this.extendedHour = extendedHour;
	}
	
	@Override
	public void run() {
		while (running){
			try{
				List<Quote> ql = null;
				if (extendedHour){
					ql = tm.getQuotes(symbols, Quote.getExtendeHourQuoteFields(), true);
				}else{
					ql = tm.getQuotes(symbols, Quote.getRegularQuoteFields(), false);
				}
				if (ql!=null){
					for (Quote q:ql){
						Long acumVol = lastTickMap.get(q.getSymbol());
						if ( acumVol!=null && acumVol!=q.getCumuVol()
								|| acumVol==null){
							TradeTick tt = q.toTradeTick();
							if (extendedHour){//cal vol
								long vol = 0;
								if (acumVol==null){
									vol = q.getCumuVol();
								}else{
									vol = q.getCumuVol() - acumVol;
								}
								tt.setVl(vol);
							}
							logger.debug(String.format("accept %s, %s", q.getSymbol(), tt.toCsv(TimeZone.getTimeZone("EST"))));
							tdm.accept(q.getSymbol(), tt);
							lastTickMap.put(q.getSymbol(), q.getCumuVol());
						}
					}
				}
				Thread.sleep(1000*threadNum);//limitation maximum admitted 60 per Minute
			}catch(InterruptedException ie){
				running=false;
				logger.info("StreamSimulator stopped.");
			}catch(Throwable t){
				logger.error("not interrupted exception, goon", t);
			}
		}
	}
}