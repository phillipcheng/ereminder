
package oauth.signpost.jetty;


import org.eclipse.jetty.client.api.Request;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;


public class Jetty9OAuthConsumer extends AbstractOAuthConsumer {

    private static final long serialVersionUID = 1L;

    public Jetty9OAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        return new Jetty9HttpRequestAdapter((Request) request);
    }

}
