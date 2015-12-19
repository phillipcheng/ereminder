package org.cld.trade;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import org.cld.util.JsonUtil;
import org.cld.stock.strategy.OrderFilled;
import org.cld.stock.strategy.StockOrder;
import org.cld.stock.strategy.StockOrder.ActionType;
import org.cld.stock.strategy.StockOrder.OrderType;
import org.cld.stock.strategy.StockOrder.TimeInForceType;
import org.cld.trade.response.Balance;
import org.cld.trade.response.Holding;
import org.cld.trade.response.OrderResponse;
import org.cld.trade.response.OrderStatus;
import org.cld.trade.response.Quote;

public class TradeKingConnector implements TradeApi{
	private static Logger logger =  LogManager.getLogger(TradeKingConnector.class);
	private static final String CONSUMER_KEY = "consumer.key";
	private static final String CONSUMER_SECRET = "consumer.secret";
	private static final String OAUTH_TOKEN = "oauth.token";
	private static final String OAUTH_TOKEN_SECRET = "oauth.token.secret";
	private static final String ACCOUNT_ID= "account.id";
	
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
	
	
	private String consumerKey;
	private String consumerSecret;
	private String oauthToken;
	private String oauthTokenSecret;
	private String accountId;

	private OAuthService service;
	private Token accessToken;

	
	
	public TradeKingConnector(String propFile){//1 proper file 1 account
		try{
			PropertiesConfiguration pc = new PropertiesConfiguration(propFile);
			consumerKey = pc.getString(CONSUMER_KEY);
			consumerSecret = pc.getString(CONSUMER_SECRET);
			oauthToken = pc.getString(OAUTH_TOKEN);
			oauthTokenSecret = pc.getString(OAUTH_TOKEN_SECRET);
			accountId = pc.getString(ACCOUNT_ID);
			service = new ServiceBuilder()
	                .provider(TradeKingAuthApi.class)
	                .apiKey(consumerKey)
	                .apiSecret(consumerSecret)
	                .build();
			accessToken = new Token(oauthToken, oauthTokenSecret);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public static StockOrder.ActionType toActionType(String side){
		if (FixmlConst.Side_Buy.equals(side)){
			return ActionType.buy;
		}else if (FixmlConst.Side_Sell.equals(side)){
			return ActionType.sell;
		}else if (FixmlConst.Side_SellShort.equals(side)){
			return ActionType.sellshort;
		}else{
			logger.error("unsupported side value:" + side);
			return ActionType.unknown;
		}
	}
	
	public static String fromActionType(StockOrder.ActionType actType){
		if (actType==ActionType.buy || actType==ActionType.buycover){
			return FixmlConst.Side_Buy;
		}else if (actType==ActionType.sell){
			return FixmlConst.Side_Sell;
		}else if (actType == ActionType.sellshort){
			return FixmlConst.Side_SellShort;
		}else{
			logger.error(String.format("unsupported action type:", actType));
			return null;
		}
	}
	
	public static StockOrder.OrderType toOrderType(String orderType){
		if (FixmlConst.Typ_Market.equals(orderType)){
			return OrderType.market;
		}else if (FixmlConst.Typ_Limit.equals(orderType)){
			return OrderType.limit;
		}else if (FixmlConst.Typ_Stop.equals(orderType)){
			return OrderType.stop;
		}else if (FixmlConst.Typ_Market.equals(orderType)){
			return OrderType.market;
		}else if (FixmlConst.Typ_StopTrailing.equals(orderType)){
			return OrderType.stoptrailingpercentage;
		}else if (FixmlConst.Typ_StopLimit.equals(orderType)){
			return OrderType.stoplimit;
		}else{
			logger.error(String.format("unsupported order type: %s", orderType));
			throw new RuntimeException(String.format("unsupported order type: %s", orderType));
		}
	}
	
	public static String fromOrderType(StockOrder.OrderType orderType){
		if (orderType==OrderType.market){
			return FixmlConst.Typ_Market;
		}else if (orderType==OrderType.limit){
			return FixmlConst.Typ_Limit;
		}else if (orderType==OrderType.stop){
			return FixmlConst.Typ_Stop;
		}else if (orderType==OrderType.stoplimit){
			return FixmlConst.Typ_StopLimit;
		}else if (orderType==OrderType.stoptrailingdollar || orderType==OrderType.stoptrailingpercentage){
			return FixmlConst.Typ_StopTrailing;
		}else{
			logger.error(String.format("unsupported order type:", orderType));
			throw new RuntimeException(String.format("unsupported order type:", orderType));
		}
	}
	
	private NewOrderSingleMessageT convertSO(StockOrder so){
		NewOrderSingleMessageT nosm = new NewOrderSingleMessageT();
		nosm.setAcct(accountId);
		//set tif
		if (so.getTif()==TimeInForceType.DayOrder){
			nosm.setTmInForce(FixmlConst.TIF_Day);	
		}else if (so.getTif()==TimeInForceType.GTC){
			nosm.setTmInForce(FixmlConst.TIF_GTC);
		}else if (so.getTif()==TimeInForceType.MarktOnClose){
			nosm.setTmInForce(FixmlConst.TIF_MarketOnClose);
		}else{
			logger.error(String.format("tif not set in so.", ""));
		}
		//
		nosm.setTyp(fromOrderType(so.getOrderType()));
		if (so.getOrderType()==OrderType.stoplimit){
			nosm.setStopPx(new BigDecimal(String.format("%.2f", so.getStopPrice())));
		}else if (so.getOrderType()==OrderType.stoptrailingdollar || so.getOrderType()==OrderType.stoptrailingpercentage){
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
			throw new RuntimeException(String.format("unsupported order type:", so.getOrderType()));
		}
		nosm.setSide(fromActionType(so.getAction()));
		InstrumentBlockT instr = new InstrumentBlockT();
		instr.setSym(so.getSymbol());
		instr.setSecTyp("CS");
		nosm.setInstrmt(instr);
		
		nosm.setPx(new BigDecimal(String.format("%.2f", so.getLimitPrice())));
		OrderQtyDataBlockT oq = new OrderQtyDataBlockT();
		oq.setQty(new BigDecimal(so.getQuantity()));
		nosm.setOrdQty(oq);
		return nosm;
	}
	
	@Override
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
			if (map!=null){
				map = (Map<String, Object>) map.get(RESPONSE);
				if (map!=null){
					return new OrderResponse(map);
				}else{
					logger.error(String.format("%s not found in response.", RESPONSE));
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
	
	public void getAccount(){
		OAuthRequest request = new OAuthRequest(Verb.GET, ACCOUNTS_URL);
		service.signRequest(accessToken, request);
		Response response = request.send();
		logger.info(response.getBody());
	}
	
	@Override
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
			if (map!=null){
				map = (Map<String, Object>) map.get(RESPONSE);
				if (map!=null){
					return new OrderResponse(map);
				}else{
					logger.error(String.format("%s not found in response.", RESPONSE));
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
	
	@Override
	public OrderResponse cancelOrder(String clientOrderId, ActionType at, String symbol, int quantity){
		OAuthRequest request = new OAuthRequest(Verb.POST, String.format(MAKE_ORDER_URL, accountId));

		FIXML fixml = new FIXML();
		OrderCancelRequestMessageT nosm = new OrderCancelRequestMessageT();
		nosm.setAcct(accountId);
		nosm.setOrigID(clientOrderId);
		if (at==ActionType.buy || at==ActionType.buycover){
			nosm.setSide(FixmlConst.Side_Buy);
		}else if (at==ActionType.sell){
			nosm.setSide(FixmlConst.Side_Sell);
		}else if (at==ActionType.sellshort){
			nosm.setSide(FixmlConst.Side_SellShort);
		}
		InstrumentBlockT instr = new InstrumentBlockT();
		instr.setSecTyp("CS");
		instr.setSym(symbol);
		nosm.setInstrmt(instr);
		OrderQtyDataBlockT oq = new OrderQtyDataBlockT();
		oq.setQty(new BigDecimal(quantity));
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
			if (map!=null){
				map = (Map<String, Object>) map.get(RESPONSE);
				if (map!=null){
					return new OrderResponse(map);
				}else{
					logger.error(String.format("%s not found in response.", RESPONSE));
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
	
	@Override
	public Balance getBalance(){
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(BALANCE_URL,accountId));
		service.signRequest(accessToken, request);
		Response response = request.send();
		logger.info(response.getBody());
		Map<String, Object> map =  JsonUtil.fromJsonStringToMap(response.getBody());
		if (map!=null){
			map = (Map<String, Object>) map.get(RESPONSE);
			if (map!=null){
				map = (Map<String, Object>) map.get(ACCOUNTBALANCE);
				if (map!=null){
					return new Balance(map);
				}else{
					logger.error(String.format("%s not found.", ACCOUNTBALANCE));
				}
			}else{
				logger.error(String.format("%s not found.", RESPONSE));
			}
		}
		return null;
	}
	
	@Override
	public List<Holding> getHolding(){
		List<Holding> hlist = new ArrayList<Holding>();
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(HOLDING_URL,accountId));
		service.signRequest(accessToken, request);
		Response response = request.send();
		logger.info(response.getBody());
		Map<String, Object> map =  JsonUtil.fromJsonStringToMap(response.getBody());
		if (map!=null){
			map = (Map<String, Object>) map.get(RESPONSE);
			if (map!=null){
				map = (Map<String, Object>) map.get(ACCOUNTHOLDINGS);
				if (map!=null){
					List hl = (List) map.get(HOLDING);
					if (hl!=null){
						for (Object o:hl){
							Map<String, Object> hmap = (Map<String, Object>)o;
							Holding h = new Holding(hmap);
							hlist.add(h);
						}
					}
				}
			}
		}
		return hlist;
	}
	
	
	public OrderStatus getTheOrderStatus(String trackOrderId){
		Map<String, OrderStatus> map = getOrderStatus();
		return map.get(trackOrderId);
	}
	
	@Override
	public Map<String, OrderStatus> getOrderStatus(){
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(ORDERSTATUS_URL,accountId));
		service.signRequest(accessToken, request);
		Response response = request.send();
		List<Map<String, Object>> fml = null;
		logger.debug(response.getBody());
		Map<String, Object> map =  JsonUtil.fromJsonStringToMap(response.getBody());
		if (map!=null){
			map = (Map<String, Object>) map.get(RESPONSE);
			if (map!=null){
				map = (Map<String, Object>) map.get(ORDERSTATUS);
				if (map!=null){
					Object orders = map.get(ORDER);
					if (orders!=null){
						if (orders instanceof List){
							fml = (List) orders;
						}else{
							fml = new ArrayList<Map<String, Object>>();
							fml.add((Map<String, Object>)orders);
						}
					}else{
						logger.error(String.format("%s not found.", ORDER));
					}
				}else{
					logger.error(String.format("%s not found.", ORDERSTATUS));
				}
			}else{
				logger.error(String.format("%s not found.", RESPONSE));
			}
		}
		Map<String, OrderStatus> om = new HashMap<String, OrderStatus>();
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance("org.xml.fixml");
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			if (fml!=null){
				for (Object fm:fml){
					Map<String, Object> fmm = (Map<String, Object>) fm;
					String fixmm = (String) fmm.get(FIXMLMSG);
					StringReader sr = new StringReader(fixmm);
					FIXML fixml = (FIXML) jaxbUnmarshaller.unmarshal(sr);
					JAXBElement<ExecutionReportMessageT> me = (JAXBElement<ExecutionReportMessageT>) fixml.getMessage();
					ExecutionReportMessageT erm = me.getValue();
					String orderId = erm.getOrdID();
					String symbol = null;
					if (erm.getInstrmt()!=null){
						symbol = erm.getInstrmt().getSym();
					}
					float price =0f;
					if (erm.getPx()!=null){
						price = erm.getPx().floatValue();
					}
					int qty = erm.getLeavesQty().intValue();
					String side = erm.getSide();
					String typ = erm.getTyp();
					String stat = erm.getStat();
					om.put(orderId, new OrderStatus(orderId, symbol, qty, price, stat, side, typ));
					String linkId = erm.getLnkID();
					if (linkId!=null && !"".equals(linkId)){
						om.put(linkId, new OrderStatus(linkId, symbol, qty, price, stat, side, typ));
					}
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return om;
	}
	
	@Override
	public List<Quote> getQuotes(String[] stockids){
		return getQuotes(stockids, false);
	}

	public List<Quote> getQuotes(String[] stockids, boolean allInfo){
		try{
			String[] fids = Quote.getAllFields();
			String symbols = StringUtils.join(stockids, ",");
			String strFids = StringUtils.join(fids, ",");
			OAuthRequest request = new OAuthRequest(Verb.POST, QUOTES_URL);
			request.addBodyParameter("symbols", symbols);
			if (!allInfo){
				request.addBodyParameter("fids", strFids);
			}
			service.signRequest(accessToken, request);
			Response response = request.send();
			logger.debug(response.getBody());
			Map<String, Object> map =  JsonUtil.fromJsonStringToMap(response.getBody());
			List<Quote> retql = new ArrayList<Quote>();
			if (map!=null){
				map = (Map<String, Object>) map.get(RESPONSE);
				if (map!=null){
					map = (Map<String, Object>) map.get(QUOTES);
					if (map!=null){
						Object oq = map.get(QUOTE);
						if (oq!=null){
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
						}else{
							logger.error(String.format("%s not found.", QUOTE));
						}
					}else{
						logger.error(String.format("%s not found.", QUOTES));
					}
				}else{
					logger.error(String.format("%s not found.", RESPONSE));
				}
			}
			return retql;
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
	
	@Override
	public OrderResponse trySubmit(StockOrder sobuy, boolean preview){
		if (sobuy!=null){
			if (preview){
				logger.info(String.format("preview order: %s",sobuy));
				return previewOrder(sobuy);
			}else{
				logger.info(String.format("make order: %s",sobuy));
				return makeOrder(sobuy);
			}
		}else{
			logger.info(String.format("no order to submit."));
			return null;
		}
	}

	public static OrderFilled toOrderFilled(OrderStatus os){
		StockOrder.ActionType side;//buy,sell
		StockOrder.OrderType typ;
		side = toActionType(os.getSide());
		typ = toOrderType(os.getTyp());
		return new OrderFilled(os.getSymbol(), os.getCumQty(), os.getAvgPrice(), side, typ);
	}
	
	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getOauthToken() {
		return oauthToken;
	}

	public String getOauthTokenSecret() {
		return oauthTokenSecret;
	}
}
