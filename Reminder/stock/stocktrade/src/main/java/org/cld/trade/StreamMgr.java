package org.cld.trade;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.ProxyConf;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.client.ProxyConfiguration;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.jetty.Jetty9OAuthConsumer;

public class StreamMgr implements Runnable{
	private static Logger logger =  LogManager.getLogger(StreamMgr.class);
	
	public static final String STREAM_URL="https://stream.tradeking.com/v1/market/quotes.json?symbols=%s";
	public static final int REQUEST_MAX_SYMBOLS=250;
	private static final int TIMEOUT_HOURS=2;
	private List<String> symbols;
	private TradeKingConnector tm;
	private StreamHandler streamHandler;
	private ProxyConf proxyConf;
	
	private boolean running=true;
	
	public StreamMgr(List<String> symbols, TradeKingConnector tm, TradeDataMgr tdm, ProxyConf pconf){
		this.symbols = symbols;
		this.tm = tm;
		this.streamHandler = new StreamHandler(tdm);
		this.proxyConf = pconf;
	}
	
	private void sendRequest(HttpClient httpClient, String url, OAuthConsumer consumer){
		try{
			Request req = httpClient.newRequest(url);
			req.timeout(TIMEOUT_HOURS, TimeUnit.HOURS);
			req.method(HttpMethod.GET);
			req.onResponseContent(streamHandler);
			req.onResponseBegin(streamHandler);
			consumer.sign(req);
			req.send(streamHandler);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	@Override
	public void run() {
		OAuthConsumer consumer = new Jetty9OAuthConsumer(tm.getConsumerKey(), tm.getConsumerSecret());
		consumer.setTokenWithSecret(tm.getOauthToken(), tm.getOauthTokenSecret());
		//https support
		SslContextFactory sslContextFactory = new SslContextFactory();
        HttpClient httpClient = new HttpClient(sslContextFactory);
        //proxy support
        if (proxyConf.isUseProxy()){
			ProxyConfiguration proxyConfig = httpClient.getProxyConfiguration();
			HttpProxy proxy = new HttpProxy(proxyConf.getHost(), proxyConf.getPort());
			proxyConfig.getProxies().add(proxy);
		}
        try{
        	httpClient.start();
        }catch(Exception e){
        	logger.error("error to start httpclient.", e);
        }
		String symbolsParam = StringUtils.join(symbols, ',');
		String url = String.format(STREAM_URL, symbolsParam);
		sendRequest(httpClient, url, consumer);
		while (running){
			try{
				if (streamHandler.isFinished()){
					//logger.error(String.format("request state %d finished for %s!", streamHandler.getStatus(), symbols));
					streamHandler.reset();
					sendRequest(httpClient, url, consumer);
				}
				Thread.sleep(3000);
			}catch(InterruptedException ie){
				running = false;
				try{
					httpClient.stop(); 
				}catch(Exception e){
		        	logger.error("error to stop httpclient.", e);
		        }
				logger.info("StreamMgr stopped.");
			}catch(Throwable t){
				logger.error("not interrupted exception, goon", t);
			}
		}
	}
}