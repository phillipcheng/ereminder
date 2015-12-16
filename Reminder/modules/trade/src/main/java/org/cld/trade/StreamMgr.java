package org.cld.trade;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.ProxyConf;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.jetty.Jetty9OAuthConsumer;

public class StreamMgr implements Runnable{
	private static Logger logger =  LogManager.getLogger(StreamMgr.class);
	
	public static final String STREAM_URL="https://stream.tradeking.com/v1/market/quotes.json?symbols=%s";
	public static final int REQUEST_MAX_SYMBOLS=250;
	private List<String> symbols;
	private TradeKingConnector tm;
	private StreamHandler streamHandler;
	private ProxyConf proxyConf;
	
	public StreamMgr(List<String> symbols, TradeKingConnector tm, TradeDataMgr tdm, ProxyConf pconf){
		this.symbols = symbols;
		this.tm = tm;
		this.streamHandler = new StreamHandler(tdm);
		this.proxyConf = pconf;
	}
	
	@Override
	public void run() {
		try{
			OAuthConsumer consumer = new Jetty9OAuthConsumer(tm.getConsumerKey(), tm.getConsumerSecret());
			consumer.setTokenWithSecret(tm.getOauthToken(), tm.getOauthTokenSecret());
			HttpClient client = new HttpClient();
			if (proxyConf.isUseProxy()){
				//client.setProxy(new Address(cconf.getProxyIP(), cconf.getProxyPort()));
			}
			client.start();
			String symbolsParam = StringUtils.join(symbols, ',');
			String url = String.format(STREAM_URL, symbolsParam);
			Request req = client.newRequest(url);
			req.method(HttpMethod.GET);
			req.onResponseContent(streamHandler);
			consumer.sign(req);
			req.send(streamHandler);
			while (true){
				if (streamHandler.isFinished()){
					logger.error(String.format("request state %d finished for %s!", streamHandler.getStatus(), symbols));
					streamHandler.reset();
					consumer.sign(req);
					req.send(streamHandler);
				}
				Thread.sleep(3000);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
}