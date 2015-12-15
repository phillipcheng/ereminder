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
	public static final int REQUEST_MAX_SYMBOLS=250;
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
			HttpClient client = new HttpClient();
			client.start();
			ContentExchange request = new StreamQuoteRequest(symbols, tdm);
			consumer.sign(request);
			client.send(request);
			while (true){
				int exchageState = request.waitForDone();
				logger.error(String.format("request state %d finished for %s!", exchageState, symbols));
				consumer.sign(request);
				client.send(request);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
}