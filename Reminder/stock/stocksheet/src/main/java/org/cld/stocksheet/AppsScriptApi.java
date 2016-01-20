package org.cld.stocksheet;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.persist.RangeEntry;
import org.cld.stock.strategy.select.Range;
import org.cld.trade.AutoTrader;
import org.cld.trade.TradeKingConnector;
import org.cld.trade.evt.MarketStatusType;
import org.cld.trade.response.Quote;

import com.google.api.services.script.model.*;
import com.google.api.services.script.Script;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppsScriptApi {

	public static Logger logger = LogManager.getLogger(Range.class);
	
    /** Application name. */
    private static final String APPLICATION_NAME =
        "StockRange";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/StockRange");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES =
        Arrays.asList("https://www.googleapis.com/auth/drive", "https://www.googleapis.com/auth/spreadsheets");

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = AppsScriptApi.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Create a HttpRequestInitializer from the given one, except set
     * the HTTP read timeout to be longer than the default (to allow
     * called scripts time to execute).
     *
     * @param {HttpRequestInitializer} requestInitializer the initializer
     *     to copy and adjust; typically a Credential object.
     * @return an initializer with an extended read timeout.
     */
    private static HttpRequestInitializer setHttpTimeout(
            final HttpRequestInitializer requestInitializer) {
        return new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
                requestInitializer.initialize(httpRequest);
                // This allows the API to call (and avoid timing out on)
                // functions that take up to 6 minutes to complete (the maximum
                // allowed script run time), plus a little overhead.
                httpRequest.setReadTimeout(380000);
            }
        };
    }

    /**
     * Build and return an authorized Script client service.
     *
     * @param {Credential} credential an authorized Credential object
     * @return an authorized Script client service
     */
    public static Script getScriptService() throws IOException {
        Credential credential = authorize();
        return new Script.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, setHttpTimeout(credential))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Interpret an error response returned by the API and return a String
     * summary.
     *
     * @param {Operation} op the Operation returning an error response
     * @return summary of error response, or null if Operation returned no
     *     error
     */
    public static String getScriptError(Operation op) {
        if (op.getError() == null) {
            return null;
        }

        // Extract the first (and only) set of error details and cast as a Map.
        // The values of this map are the script's 'errorMessage' and
        // 'errorType', and an array of stack trace elements (which also need to
        // be cast as Maps).
        Map<String, Object> detail = op.getError().getDetails().get(0);
        List<Map<String, Object>> stacktrace =
                (List<Map<String, Object>>)detail.get("scriptStackTraceElements");
        java.lang.StringBuilder sb =
                new StringBuilder("\nScript error message: ");
        sb.append(detail.get("errorMessage"));
        sb.append("\nScript error type: ");
        sb.append(detail.get("errorType"));

        if (stacktrace != null) {
            // There may not be a stacktrace if the script didn't start
            // executing.
            sb.append("\nScript error stacktrace:");
            for (Map<String, Object> elem : stacktrace) {
                sb.append("\n  ");
                sb.append(elem.get("function"));
                sb.append(":");
                sb.append(elem.get("lineNumber"));
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    public static Object runScript(String scriptId, String functionName, List<Object> params) throws IOException {
        // ID of the script to call. Acquire this from the Apps Script editor,
        // under Publish > Deploy as API executable.
        Script service = getScriptService();

        // Create an execution request object.
        ExecutionRequest request = new ExecutionRequest()
                .setFunction(functionName);

        if (params!=null){
        	request.setParameters(params);
        }
        
        try {
            // Make the API request.
            Operation op =
                    service.scripts().run(scriptId, request).execute();

            // Print results of request.
            if (op.getError() != null) {
                // The API executed, but the script returned an error.
                System.out.println(getScriptError(op));
                return null;
            } else {
                // The result provided by the API needs to be cast into
                // the correct type, based upon what types the Apps
                // Script function returns. Here, the function returns
                // an Apps Script Object with String keys and values,
                // so must be cast into a Java Map (folderSet).
            	return op.getResponse().get("result");
                
            }
        } catch (GoogleJsonResponseException e) {
            // The API encountered a problem before the script was called.
            e.printStackTrace(System.out);
            return null;
        }
    }
    
    public static final String myScriptId = "MVjKy1TcI47sMse1t4Sr-bnTYSmEOAgcq";
    
    public static final String getFoldersFunctionName = "getFoldersUnderRoot";
    public static void getFoldersUnderRoot() throws IOException {
    	Map<String, String> folderSet = (Map<String, String>)runScript(myScriptId, getFoldersFunctionName, null);
        if (folderSet.size() == 0) {
            System.out.println("No folders returned!");
        } else {
            System.out.println("Folders under your root folder:");
            for (String id: folderSet.keySet()) {
                System.out.printf(
                        "\t%s (%s)\n", folderSet.get(id), id);
            }
        }
    }
    
    public static final String getRangeDataFunctionName="getRangeData";
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    static {
    	sdf.setTimeZone(TimeZone.getTimeZone("EST"));
    }
    
    public static List<RangeEntry> getRangeData(){
    	List<RangeEntry> rel = new ArrayList<RangeEntry>();
    	try{
    		//return symbol to [buyPrice,eval date] map
	    	Map<String, String> dataSet = (Map<String, String>)runScript(myScriptId, getRangeDataFunctionName, null);
	        for (String symbol: dataSet.keySet()) {
	        	String info = dataSet.get(symbol);
	        	String[] fields = info.split(",");
	        	if (fields.length>1){
	        		RangeEntry re = new RangeEntry(symbol, sdf.parse(fields[1]), Float.parseFloat(fields[0]));
	        		rel.add(re);
	        	}
	        }
    	}catch(Exception e){
    		logger.error("", e);
    	}
        return rel;
    }
    
    public static final String getSymbolsFunctionName="getSymbols";
    public static List<String> getSymbols(){
    	try{
    		//return symbol to [buyPrice,eval date] map
    		List<String> dataSet = (List<String>) runScript(myScriptId, getSymbolsFunctionName, null);
	    	return dataSet;
    	}catch(Exception e){
    		logger.error("", e);
    	}
        return null;
    }
    
    public static void dumpSymbols(String fileName){
    	List<String> symbols = getSymbols();
    	BufferedWriter bos = null;
    	try{
    		bos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
    		for (String symbol: symbols){
    			bos.write(symbol + "\n");
    		}
    	}catch(Exception e){
    		logger.error("", e);
    	}finally{
    		if (bos!=null)
			try {
				bos.close();
			} catch (IOException e) {
				logger.error("", e);
			}
    	}
    }
    
    public static final String updateDataFunctionName="updateRangeData";
    public static final String RECORD_SPLIT="|";
    public static void updateMarketData(boolean useLast){
    	AutoTrader at = new AutoTrader();
    	boolean extendedHour=false;
    	if (!useLast){
	    	MarketStatusType mst = AutoTrader.getMarketStatus(at);
	    	extendedHour=AutoTrader.isExtendedHour(mst);
    	}
    	TradeKingConnector tkc = new TradeKingConnector();
    	List<String> sl = getSymbols();
    	String[] symbols = new String[sl.size()];
    	sl.toArray(symbols);
    	List<Quote> ql = tkc.getQuotes(symbols, Quote.getQuoteAndBasicFields(), extendedHour);
    	List<Object> pl = new ArrayList<Object>();
    	StringBuffer sb = new StringBuffer();
    	for (Quote q: ql){
    		String b = String.format("%s,%.2f,%.2f,%.2f,%.2f,%d,%d", q.getSymbol(), q.getLast(), q.getEps(), 
    				q.getIad(), q.getYield(), q.getSho(), q.getAdv90());
    		sb.append(b);
    		sb.append(RECORD_SPLIT);
    	}
    	pl.add(sb.toString());
    	try{
    		//return symbol to [buyPrice,eval date] map
	    	runScript(myScriptId, updateDataFunctionName, pl);
    	}catch(Exception e){
    		logger.error("", e);
    	}
    }
}