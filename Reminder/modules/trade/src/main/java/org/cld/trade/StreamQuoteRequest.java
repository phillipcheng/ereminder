package org.cld.trade;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.TradeTick;
import org.cld.util.JsonUtil;
import org.cld.util.SafeSimpleDateFormat;
import org.mortbay.io.Buffer;
import org.mortbay.jetty.client.ContentExchange;

public class StreamQuoteRequest extends ContentExchange {

	public static final String STREAM_URL="https://stream.tradeking.com/v1/market/quotes.json?symbols=%s";
	private static Logger logger =  LogManager.getLogger(StreamQuoteRequest.class);
	private static final SafeSimpleDateFormat sdf = new SafeSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	private static final SimpleDateFormat csvsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final String TRADE="trade";
	public static final String CVOL="cvol";
	public static final String DATETIME="datetime";
	public static final String LAST="last";
	public static final String SYMBOL="symbol";
	public static final String VOL="vl";
	public static final String VWAP="vwap";
	
	private TradeDataMgr tdm;
	
	public StreamQuoteRequest(List<String> symbols, TradeDataMgr tdm){
		super(true);
		this.setMethod("GET");
		String symbolsParam = StringUtils.join(symbols, ',');
		String url = String.format(STREAM_URL, symbolsParam);
		this.setURL(url);
		this.tdm = tdm;
	}
	
	protected void onResponseComplete() throws IOException {
		int status = getResponseStatus();
		if (status == 200){
			logger.info("Successfully connected");
		}else{
			logger.error("Error Code Received: " + status);
		}
	}

	public static void processData(String line, TradeDataMgr tdm){
		String input = line;
		Map<String, Object> map = null;
		map =  JsonUtil.fromJsonStringToMap(input);
		if (map!=null){
			Map<String, Object> trade = (Map<String, Object>) map.get(TRADE);
			if (trade!=null){
				try {
					Date datetime = sdf.parse((String) trade.get(DATETIME));
					float last = Float.parseFloat((String) trade.get(LAST));
					String symbol = (String) trade.get(SYMBOL);
					long vl = Long.parseLong((String) trade.get(VOL));
					TradeTick tt = new TradeTick(datetime, last, vl);
					tdm.accept(symbol, tt);
				}catch(Exception e){
					logger.error(String.format("error parsing %s", line), e);
				}
			}
		}
	}
	
	public static void processCsvData(String symbol, String line, TradeDataMgr tdm){
		String[] vs = line.split(",");
		if (vs.length==3){
			try {
				Date datetime = csvsdf.parse(vs[0]);
				float last = Float.parseFloat(vs[1]);
				long vl = Long.parseLong(vs[2]);
				TradeTick tt = new TradeTick(datetime, last, vl);
				tdm.accept(symbol, tt);
			}catch(Exception e){
				logger.error(String.format("error parsing %s", line), e);
			}			
		}
	}
	
	protected void onResponseContent(Buffer data) {
		logger.debug(data.toString());
		processData(data.toString(), tdm);
	}
}