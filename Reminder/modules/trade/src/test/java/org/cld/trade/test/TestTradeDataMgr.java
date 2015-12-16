package org.cld.trade.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;

import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.trade.AutoTrader;
import org.cld.trade.StreamQuoteRequest;
import org.cld.trade.TradeDataMgr;
import org.junit.Test;

import org.mortbay.jetty.client.ContentExchange;
import org.mortbay.jetty.client.HttpClient;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.jetty.JettyOAuthConsumer;

public class TestTradeDataMgr {
	
	@Test
	public void test1() throws Exception {
		AutoTrader at = new AutoTrader();
		StockConfig sc = StockUtil.getStockConfig(at.getBaseMarketId());
		TradeDataMgr tdm = new TradeDataMgr(at, sc);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("C:\\mydoc\\myprojects\\ereminder\\Reminder\\modules\\trade\\input\\AAPL_tick_20151214.txt")));
		String line = null;
		while ((line=br.readLine())!=null){
			StreamQuoteRequest.processCsvData("AAPL", line, tdm);
		}
		br.close();
		
		Thread.sleep(30000);//60 seconds
	}
	
	@Test
	public void testStream() throws Exception {
		AutoTrader at = new AutoTrader();
        // create a consumer object and configure it with the access
        // token and token secret obtained from the service provider
        OAuthConsumer consumer = new JettyOAuthConsumer(at.getTm().getConsumerKey(), at.getTm().getConsumerSecret());
        consumer.setTokenWithSecret(at.getTm().getOauthToken(), at.getTm().getOauthTokenSecret());

        // create an HTTP request to a protected resource
        ContentExchange request = new ContentExchange(true) {
          // tell me what kind of response code we got
            protected void onResponseComplete() throws IOException {
                int status = getResponseStatus();
                if (status == 200)
                    System.out.println("Successfully connected");
                else
                    System.out.println("Error Code Received: " + status);
            }

            // print out any response data we get along the stream
            protected void onResponseContent(Buffer data) {
              System.out.println(data);
            }
        };

        // setup the request
        request.setMethod("GET");
        request.setURL("https://stream.tradeking.com/v1/market/quotes.xml?symbols=F");

        // sign the request
        consumer.sign(request);

        // send the request
        HttpClient client = new HttpClient();
        client.start();
        client.send(request);
        request.waitForDone();
	}
}
