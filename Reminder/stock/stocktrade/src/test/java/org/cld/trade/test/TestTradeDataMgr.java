package org.cld.trade.test;

import java.nio.ByteBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.trade.AutoTrader;
import org.cld.trade.TradeKingConnector;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.jetty.Jetty9OAuthConsumer;

class MyHandler implements Response.ContentListener, Response.CompleteListener{

	private static Logger logger =  LogManager.getLogger(MyHandler.class);
	
	@Override
	public void onContent(Response response, ByteBuffer content) {
		logger.info("content:" + new String(content.array()));
	}

	@Override
	public void onComplete(Result result) {
		 int status = result.getResponse().getStatus();
         if (status == 200)
             System.out.println("Successfully connected");
         else
             System.out.println("Error Code Received: " + status);
		
	}
	
}
public class TestTradeDataMgr {
	
	public static void main(String args[]) throws Exception {
		AutoTrader at = new AutoTrader();
        // create a consumer object and configure it with the access
        // token and token secret obtained from the service provider
		TradeKingConnector tradeApi = (TradeKingConnector)at.getTm();
        OAuthConsumer consumer = new Jetty9OAuthConsumer(tradeApi.getConsumerKey(), tradeApi.getConsumerSecret());
        consumer.setTokenWithSecret(tradeApi.getOauthToken(), tradeApi.getOauthTokenSecret());
        SslContextFactory sslContextFactory = new SslContextFactory();
        HttpClient client = new HttpClient(sslContextFactory);
        client.start();
        Request request = client.newRequest("https://stream.tradeking.com/v1/market/quotes.xml?symbols=COMP");
        request.method(HttpMethod.GET);

        MyHandler myhandler = new MyHandler();
        // sign the request
        consumer.sign(request);
        request.onResponseContent(myhandler);
        request.send(myhandler);
        Thread.sleep(20000);
	}
}
