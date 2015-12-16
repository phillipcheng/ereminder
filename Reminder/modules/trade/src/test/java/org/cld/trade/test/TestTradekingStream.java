package org.cld.trade.test;

import java.io.IOException;
import java.nio.Buffer;

import org.cld.trade.AutoTrader;
import org.junit.Test;

import org.mortbay.jetty.client.ContentExchange;
import org.mortbay.jetty.client.HttpClient;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.jetty.JettyOAuthConsumer;

public class TestTradekingStream {
	
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
