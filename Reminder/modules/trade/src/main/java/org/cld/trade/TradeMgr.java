package org.cld.trade;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec.MAP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.prepare.GenCloseDropAvgForDayTask;
import org.cld.stock.trade.StockOrder;
import org.cld.stock.trade.StockOrder.ActionType;
import org.cld.stock.trade.StockOrder.OrderType;
import org.cld.stock.trade.StockOrder.TimeInForceType;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.Balance;
import org.cld.trade.response.Holding;
import org.cld.trade.response.OrderResponse;
import org.cld.trade.response.OrderStatus;
import org.cld.trade.response.Quote;
import org.cld.util.JsonUtil;
import org.cld.util.jdbc.DBConnConf;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.xml.fixml.ExecutionReportMessageT;
import org.xml.fixml.FIXML;
import org.xml.fixml.InstrumentBlockT;
import org.xml.fixml.NewOrderSingleMessageT;
import org.xml.fixml.OrderCancelRequestMessageT;
import org.xml.fixml.OrderQtyDataBlockT;
import org.xml.fixml.PegInstructionsBlockT;

public class TradeMgr {
	private static Logger logger =  LogManager.getLogger(TradeMgr.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	private static final String CRAWLCONF_KEY="crawl.conf";
	private static final String CONSUMER_KEY = "consumer.key";
	private static final String CONSUMER_SECRET = "consumer.secret";
	private static final String OAUTH_TOKEN = "oauth.token";
	private static final String OAUTH_TOKEN_SECRET = "oauth.token.secret";
	private static final String ACCOUNT_ID= "account.id";
	private static final String USE_AMOUNT="use.amount";
	private static final String BASE_MARKET="base.market";
	private static final String MARKET_ID="market.id";
	
	
	private static final String PROFILE_URL = "https://api.tradeking.com/v1/member/profile.json";
	private static final String PREVIEW_ORDER_URL="https://api.tradeking.com/v1/accounts/%s/orders/preview.json";
	private static final String MAKE_ORDER_URL="https://api.tradeking.com/v1/accounts/%s/orders.json";
	private static final String ACCOUNTS_URL = "https://api.tradeking.com/v1/accounts.json";
	private static final String QUOTES_URL="https://api.tradeking.com/v1/market/ext/quotes.json";
	private static final String BALANCE_URL="https://api.tradeking.com/v1/accounts/%s/balances.json";
	private static final String HOLDING_URL="https://api.tradeking.com/v1/accounts/%s/holdings.json";
	private static final String ORDERSTATUS_URL="https://api.tradeking.com/v1/accounts/%s/orders.json";
	
	//xml qname
	public static final String QNAME_ORDER="Order";
	public static final String QNAME_ORDER_CANCEL="OrdCxlReq";
	//json names
	public static final String RESPONSE="response";
	public static final String QUOTES="quotes";
	public static final String QUOTE="quote";
	public static final String ACCOUNTHOLDINGS="accountholdings";
	public static final String HOLDING="holding";
	public static final String ACCOUNTBALANCE = "accountbalance";
	public static final String ORDERSTATUS="orderstatus";
	public static final String ORDER="order";
	public static final String FIXMLMSG="fixmlmessage";
	
	private String cconffile;
	private String consumerKey;
	private String consumerSecret;
	private String oauthToken;
	private String oauthTokenSecret;
	private String accountId;
	private CrawlConf cconf;
	private int useAmount;
	private String baseMarketId;
	private String marketId;
	
	private OAuthService service;
	private Token accessToken;
	
	public TradeMgr(String properFile){//1 proper file 1 account
		try{
			PropertiesConfiguration pc = new PropertiesConfiguration(properFile);
			cconffile = pc.getString(CRAWLCONF_KEY);
			consumerKey = pc.getString(CONSUMER_KEY);
			consumerSecret = pc.getString(CONSUMER_SECRET);
			oauthToken = pc.getString(OAUTH_TOKEN);
			oauthTokenSecret = pc.getString(OAUTH_TOKEN_SECRET);
			accountId = pc.getString(ACCOUNT_ID);
			setUseAmount(pc.getInt(USE_AMOUNT));
			service = new ServiceBuilder()
	                .provider(TradeKingAuthApi.class)
	                .apiKey(consumerKey)
	                .apiSecret(consumerSecret)
	                .build();
			accessToken = new Token(oauthToken, oauthTokenSecret);
			cconf = CrawlTestUtil.getCConf(cconffile);
			setBaseMarketId(pc.getString(BASE_MARKET));
			setMarketId(pc.getString(MARKET_ID));
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	private NewOrderSingleMessageT convertSO(StockOrder so){
		NewOrderSingleMessageT nosm = new NewOrderSingleMessageT();
		nosm.setAcct(accountId);
		//set tif
		if (so.getTif()==TimeInForceType.DayOrder){
			nosm.setTmInForce("0");	
		}else if (so.getTif()==TimeInForceType.GTC){
			nosm.setTmInForce("1");
		}else if (so.getTif()==TimeInForceType.MarktOnClose){
			nosm.setTmInForce("7");
		}else{
			logger.error(String.format("tif not set in so.", ""));
		}
		//
		if (so.getOrderType()==OrderType.market){
			nosm.setTyp("1");
		}else if (so.getOrderType()==OrderType.limit){
			nosm.setTyp("2");
		}else if (so.getOrderType()==OrderType.stop){
			nosm.setTyp("3");
		}else if (so.getOrderType()==OrderType.stoplimit){
			nosm.setTyp("4");
		}else if (so.getOrderType()==OrderType.stoptrailingdollar || so.getOrderType()==OrderType.stoptrailingpercentage){
			nosm.setTyp("P");
			PegInstructionsBlockT pegIns = new PegInstructionsBlockT();
			if (so.getOrderType()==OrderType.stoptrailingdollar){
				pegIns.setOfstTyp(BigInteger.ZERO);
				pegIns.setOfstVal(new BigDecimal(so.getIncrementDollar()));
			}else{
				pegIns.setOfstTyp(BigInteger.ONE);
				pegIns.setOfstVal(new BigDecimal(so.getIncrementPercent()));
			}
			pegIns.setPegPxTyp(BigInteger.ONE);
			nosm.setPegInstr(pegIns);
		}else{
			logger.error(String.format("unsupported order type:", so.getOrderType()));
		}
		
		if (so.getAction()==ActionType.buy || so.getAction()==ActionType.buycover){
			nosm.setSide("1");
		}else if (so.getAction()==ActionType.sell){
			nosm.setSide("2");
		}else if (so.getAction()==ActionType.sellshort){
			nosm.setSide("5");
		}else{
			logger.error(String.format("unsupported action type:", so.getAction()));
		}
		InstrumentBlockT instr = new InstrumentBlockT();
		instr.setSym(so.getStockid());
		instr.setSecTyp("CS");
		nosm.setInstrmt(instr);
		
		nosm.setPx(new BigDecimal(String.format("%.2f",so.getLimitPrice())));
		OrderQtyDataBlockT oq = new OrderQtyDataBlockT();
		oq.setQty(new BigDecimal(so.getQuantity()));
		nosm.setOrdQty(oq);
		return nosm;
	}
	
	public OrderResponse previewOrder(StockOrder so){	
		OAuthRequest request = new OAuthRequest(Verb.POST, String.format(PREVIEW_ORDER_URL, accountId));

		FIXML fixml = new FIXML();
		NewOrderSingleMessageT nosm = convertSO(so);
		JAXBElement<NewOrderSingleMessageT> me = new JAXBElement<NewOrderSingleMessageT>(
				new QName(QNAME_ORDER), NewOrderSingleMessageT.class, nosm);
		fixml.setMessage(me);
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(FIXML.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(fixml, sw);
			String payload = sw.toString();
			payload = payload.replaceAll("ns2:", "");
			payload = payload.replaceAll(":ns2", "");
			logger.info(String.format("body:%s", payload));
			request.addPayload(payload);
			request.addHeader("content-type", "application/xml");
			request.addHeader("content-length", ""+payload.length());
			service.signRequest(accessToken, request);
			Response response = request.send();
			logger.info(response.getBody());
			Map<String, Object> map =  JsonUtil.fromJsonStringToMap(response.getBody());
			map = (Map<String, Object>) map.get(RESPONSE);
			return new OrderResponse(map);
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	public void getAccount(){
		OAuthRequest request = new OAuthRequest(Verb.GET, ACCOUNTS_URL);
		service.signRequest(accessToken, request);
		Response response = request.send();
		logger.info(response.getBody());
	}
	
	public OrderResponse makeOrder(StockOrder so){
		OAuthRequest request = new OAuthRequest(Verb.POST, String.format(MAKE_ORDER_URL, accountId));

		FIXML fixml = new FIXML();
		NewOrderSingleMessageT nosm = convertSO(so);
		JAXBElement<NewOrderSingleMessageT> me = new JAXBElement<NewOrderSingleMessageT>(
				new QName(QNAME_ORDER), NewOrderSingleMessageT.class, nosm);
		fixml.setMessage(me);
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(FIXML.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(fixml, sw);
			String payload = sw.toString();
			payload = payload.replaceAll("ns2:", "");
			payload = payload.replaceAll(":ns2", "");
			logger.info(String.format("body:%s", payload));
			request.addPayload(payload);
			request.addHeader("content-type", "application/xml");
			request.addHeader("content-length", ""+payload.length());
			request.addHeader("TKI_OVERRIDE", "true");
			service.signRequest(accessToken, request);
			Response response = request.send();
			logger.info(response.getBody());
			Map<String, Object> map =  JsonUtil.fromJsonStringToMap(response.getBody());
			map = (Map<String, Object>) map.get(RESPONSE);
			OrderResponse or = new OrderResponse(map);
			return or;
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
	
	public OrderResponse cancelOrder(String clientOrderId){
		OAuthRequest request = new OAuthRequest(Verb.POST, String.format(MAKE_ORDER_URL, accountId));

		FIXML fixml = new FIXML();
		OrderCancelRequestMessageT nosm = new OrderCancelRequestMessageT();
		nosm.setAcct(accountId);
		nosm.setOrigID(clientOrderId);
		nosm.setSide("1");
		InstrumentBlockT instr = new InstrumentBlockT();
		instr.setSecTyp("CS");
		nosm.setInstrmt(instr);
		OrderQtyDataBlockT oq = new OrderQtyDataBlockT();
		oq.setQty(new BigDecimal(1));
		nosm.setOrdQty(oq);
		JAXBElement<OrderCancelRequestMessageT> me = new JAXBElement<OrderCancelRequestMessageT>(
				new QName(QNAME_ORDER_CANCEL), OrderCancelRequestMessageT.class, nosm);
		fixml.setMessage(me);
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(FIXML.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(fixml, sw);
			String payload = sw.toString();
			payload = payload.replaceAll("ns2:", "");
			payload = payload.replaceAll(":ns2", "");
			logger.info(String.format("body:%s", payload));
			request.addPayload(payload);
			request.addHeader("content-type", "application/xml");
			request.addHeader("content-length", ""+payload.length());
			service.signRequest(accessToken, request);
			Response response = request.send();
			logger.info(response.getBody());
			Map<String, Object> map =  JsonUtil.fromJsonStringToMap(response.getBody());
			map = (Map<String, Object>) map.get(RESPONSE);
			return new OrderResponse(map);
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	public Balance getBalance(){
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(BALANCE_URL,accountId));
		service.signRequest(accessToken, request);
		Response response = request.send();
		logger.info(response.getBody());
		Map<String, Object> map =  JsonUtil.fromJsonStringToMap(response.getBody());
		map = (Map<String, Object>) map.get(RESPONSE);
		map = (Map<String, Object>) map.get(ACCOUNTBALANCE);
		return new Balance(map);
	}
	
	public List<Holding> getHolding(){
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(HOLDING_URL,accountId));
		service.signRequest(accessToken, request);
		Response response = request.send();
		logger.info(response.getBody());
		Map<String, Object> map =  JsonUtil.fromJsonStringToMap(response.getBody());
		map = (Map<String, Object>) map.get(RESPONSE);
		map = (Map<String, Object>) map.get(ACCOUNTHOLDINGS);
		List hl = (List) map.get(HOLDING);
		List<Holding> hlist = new ArrayList<Holding>();
		for (Object o:hl){
			Map<String, Object> hmap = (Map<String, Object>)o;
			Holding h = new Holding(hmap);
			hlist.add(h);
		}
		return hlist;
	}

	public Map<String, OrderStatus> getOrderStatus(){
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(ORDERSTATUS_URL,accountId));
		service.signRequest(accessToken, request);
		Response response = request.send();
		logger.info(response.getBody());
		Map<String, Object> map =  JsonUtil.fromJsonStringToMap(response.getBody());
		map = (Map<String, Object>) map.get(RESPONSE);
		map = (Map<String, Object>) map.get(ORDERSTATUS);
		List fml = (List) map.get(ORDER);
		Map<String, OrderStatus> om = new HashMap<String, OrderStatus>();
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance("org.xml.fixml");
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			for (Object fm:fml){
				Map<String, Object> fmm = (Map<String, Object>) fm;
				String fixmm = (String) fmm.get(FIXMLMSG);
				StringReader sr = new StringReader(fixmm);
				FIXML fixml = (FIXML) jaxbUnmarshaller.unmarshal(sr);
				JAXBElement<ExecutionReportMessageT> me = (JAXBElement<ExecutionReportMessageT>) fixml.getMessage();
				ExecutionReportMessageT erm = me.getValue();
				String orderId = erm.getOrdID();
				//String symbol = erm.getInstrmt().getSym();
				float avgPrice = 0;
				if (erm.getAvgPx()!=null){
					avgPrice = erm.getAvgPx().floatValue();
				}
				//int ordQty = erm.getOrdQty().getQty().intValue();
				int cumQty = 0;
				if (erm.getCumQty()!=null){
					cumQty = erm.getCumQty().intValue();
				}
				String stat = erm.getStat();
				om.put(orderId, new OrderStatus(orderId, cumQty, avgPrice, stat));
				String linkId = erm.getLnkID();
				if (linkId!=null && !"".equals(linkId)){
					om.put(linkId, new OrderStatus(linkId, cumQty, avgPrice, stat));
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return om;
	}
	
	public List<Quote> getQuotes(String[] stockids){
		try{
			String[] fids = Quote.getAllFields();
			String symbols = StringUtils.join(stockids, ",");
			String strFids = StringUtils.join(fids, ",");
			OAuthRequest request = new OAuthRequest(Verb.POST, QUOTES_URL);
			request.addBodyParameter("symbols", symbols);
			request.addBodyParameter("fids", strFids);
			service.signRequest(accessToken, request);
			Response response = request.send();
			logger.info(response.getBody());
			Map<String, Object> map =  JsonUtil.fromJsonStringToMap(response.getBody());
			map = (Map<String, Object>) map.get(RESPONSE);
			map = (Map<String, Object>) map.get(QUOTES);
			List<Quote> retql = new ArrayList<Quote>();
			if (map!=null){
				Object oq = map.get(QUOTE);
				if (oq instanceof List){
					List ql = (List) map.get(QUOTE);
					for (Object o : ql){
						map = (Map<String, Object>)o;
						Quote q = new Quote(map);
						retql.add(q);
					}
				}else{
					//single symbol quote is a map
					map = (Map<String, Object>)oq;
					Quote q = new Quote(map);
					retql.add(q);
				}
			}
			return retql;
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
	
	private static final int BATCH_LIMIT=2000;
	private List<Quote> getBatchQuote(List<String> allStockIds, int startIdx, int endIdx){
		String[] stockIdArray = new String[endIdx-startIdx];
		List<String> subList = allStockIds.subList(startIdx, endIdx);
		stockIdArray = subList.toArray(stockIdArray);
		return getQuotes(stockIdArray);
	}
	private List<Quote> getMarketAllQuotes(DBConnConf dbconf, String baseMarketId, String marketId){
		StockConfig sc = StockUtil.getStockConfig(baseMarketId);
		List<String> stockIds = StockPersistMgr.getStockIds(dbconf, sc);
		List<Quote> ql = new ArrayList<Quote>();
		int total = stockIds.size();
		int fullBatch = total/BATCH_LIMIT;
		for (int i=0; i<fullBatch; i++){
			int startIdx= i*BATCH_LIMIT;
			int endIdx = startIdx+BATCH_LIMIT;
			ql.addAll(getBatchQuote(stockIds, startIdx, endIdx));
		}
		int startIdx = fullBatch * BATCH_LIMIT;
		int endIdx = total;
		if (endIdx>startIdx){
			ql.addAll(getBatchQuote(stockIds, startIdx, endIdx));
		}
		return ql;
	}
	
	public List<Quote> getMarketAllQuotes(String baseMarketId, String marketId){
		return getMarketAllQuotes(cconf.getSmalldbconf(), baseMarketId, marketId);
	}
	
	public SelectCandidateResult applyCloseDropAvgNow(String baseMarketId, String marketId, boolean useLast, SelectStrategy bs){
		List<Quote> ql = getMarketAllQuotes(cconf.getSmalldbconf(), baseMarketId, marketId);
		return applyCloseDropAvg(ql, baseMarketId, marketId, useLast, bs);
	}
	/*return the buy order
	 *simulate true: use last price and preview the order
	*/
	public SelectCandidateResult applyCloseDropAvg(List<Quote> ql, String baseMarketId, String marketId, boolean useLast, SelectStrategy bs){
		Map<String, Float> newQuotes = new HashMap<String, Float>();
		for (Quote q:ql){
			if (useLast){
				if (q.getLast()>0f){
					newQuotes.put(q.getSymbol(), q.getLast());
				}
			}else{
				if (q.getOpen()>0f){
					newQuotes.put(q.getSymbol(), q.getOpen());
				}
			}
		}
		Date submitDay = null;
		try{
			submitDay = sdf.parse(sdf.format(new Date()));
		}catch(ParseException e){
			logger.error("", e);
		}
		List<SelectCandidateResult> scrl = GenCloseDropAvgForDayTask.select(cconf, baseMarketId, marketId, submitDay, 3, bs, newQuotes);
		if (scrl.size()>0){
			SelectCandidateResult scr = scrl.get(0);
			return scr;
		}else{
			logger.warn("no candidate found.");
			return null;
		}
	}

	public CrawlConf getCconf() {
		return cconf;
	}

	public void setCconf(CrawlConf cconf) {
		this.cconf = cconf;
	}

	public int getUseAmount() {
		return useAmount;
	}

	public void setUseAmount(int useAmount) {
		this.useAmount = useAmount;
	}

	public String getMarketId() {
		return marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

	public String getBaseMarketId() {
		return baseMarketId;
	}

	public void setBaseMarketId(String baseMarketId) {
		this.baseMarketId = baseMarketId;
	}
}
