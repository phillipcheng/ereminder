package org.cld.trade;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.TradeTick;
import org.cld.util.JsonUtil;
import org.cld.util.SafeSimpleDateFormat;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;

public class StreamHandler implements Response.ContentListener, Response.CompleteListener, Response.BeginListener{

	private static Logger logger =  LogManager.getLogger(StreamHandler.class);
	private static final SafeSimpleDateFormat sdf = new SafeSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	private static final SimpleDateFormat csvsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final int MAX_BUFFER_SIZE=1024*1024*1;
	
	public static final String TRADE="trade";
	public static final String CVOL="cvol";
	public static final String DATETIME="datetime";
	public static final String LAST="last";
	public static final String SYMBOL="symbol";
	public static final String VOL="vl";
	public static final String VWAP="vwap";
	
	private TradeDataMgr tdm;
	private boolean finished=false;
	private int status=200;
	private String buffer = "";
	
	public StreamHandler(TradeDataMgr tdm){
		this.tdm = tdm;
	}
	
	public void reset(){
		finished=false;
		status=200;
	}
	
	@Override
	public void onBegin(Response response) {
		logger.info(String.format("response begin %d", response.getStatus()));
	}
	
	@Override
	public void onComplete(Result result) {
		status = result.getResponse().getStatus();
		if (status == 200){
			logger.info("Successfully connected");
		}else{
			logger.error("Error Code Received: " + status);
		}
		finished = true;
	}

	private static final String OPEN_P="{";
	private static final String CLOSE_P="}";
	
	private boolean completeJson(String strContent){
		if (strContent.startsWith(OPEN_P) && strContent.endsWith(CLOSE_P) && 
				StringUtils.countMatches(strContent, OPEN_P) == StringUtils.countMatches(strContent, CLOSE_P)){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public void onContent(Response response, ByteBuffer content) {
		String strContent = new String(content.array());
		logger.debug(strContent);
		buffer+=strContent;
		if (completeJson(buffer)){
			processData(buffer, tdm);
			buffer = "";
		}
		//exception handling
		if (buffer.length()>MAX_BUFFER_SIZE){
			buffer="";
			logger.error(String.format("buffer exceeds %s.", buffer));
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
	
	public static TradeTick processCsvData(String symbol, String line, TradeDataMgr tdm){
		String[] vs = line.split(",");
		if (vs.length==3){
			try {
				Date datetime = csvsdf.parse(vs[0]);
				float last = Float.parseFloat(vs[1]);
				long vl = Long.parseLong(vs[2]);
				TradeTick tt = new TradeTick(datetime, last, vl);
				tdm.accept(symbol, tt);
				return tt;
			}catch(Exception e){
				logger.error(String.format("error parsing %s", line), e);
			}			
		}else{
			logger.error("format wrong:%s", line);
		}
		return null;
	}

	public boolean isFinished() {
		return finished;
	}

	public int getStatus() {
		return status;
	}

}
