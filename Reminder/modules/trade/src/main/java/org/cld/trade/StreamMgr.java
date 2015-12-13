package org.cld.trade;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mortbay.jetty.client.ContentExchange;
import org.mortbay.jetty.client.HttpClient;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.jetty.JettyOAuthConsumer;

public class StreamMgr implements Runnable{
	
	private static Logger logger =  LogManager.getLogger(StreamMgr.class);
	private static final int REQUEST_MAX_SYMBOLS=256;
	private List<String> symbols;
	private TradeKingConnector tm;
	private TradeDataMgr tdm;
	
	public StreamMgr(List<String> symbols, TradeKingConnector tm, TradeDataMgr tdm){
		this.symbols = symbols;
		this.tm = tm;
		this.tdm = tdm;
	}
	
	@Override
	public void run() {
		try{
			OAuthConsumer consumer = new JettyOAuthConsumer(tm.getConsumerKey(), tm.getConsumerSecret());
			consumer.setTokenWithSecret(tm.getOauthToken(), tm.getOauthTokenSecret());
			int total=0;
			HttpClient client = new HttpClient();
			client.start();
			ContentExchange lastRequest = null;
			while (total<symbols.size()){
				int end = Math.min(total+REQUEST_MAX_SYMBOLS, symbols.size());
				List<String> sl = symbols.subList(total, end);
				ContentExchange request = new StreamQuoteRequest(sl, tdm);
				consumer.sign(request);
				client.send(request);
				lastRequest = request;
				total=end;
			}
			if (lastRequest!=null){
				lastRequest.waitForDone();
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public static void main(String[] args) throws Exception {
	}
}