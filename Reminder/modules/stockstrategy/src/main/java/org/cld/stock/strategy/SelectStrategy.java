package org.cld.stock.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.CandleQuote;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.indicator.Expression;
import org.cld.stock.indicator.Indicator;
import org.cld.util.CombPermUtil;
import org.cld.util.DataMapper;
import org.cld.util.JsonUtil;
import org.cld.util.StringUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class SelectStrategy {
	public static Logger logger = LogManager.getLogger(SelectStrategy.class);
	
	public static final String KEY_SELECTS_MEMORY="scs.memory";
	public static final String KEY_SELECTS_TYPE="scs.type";
	public static final String KEY_SELECTS_LIMIT="scs.limit";
	public static final String KEY_ORDERDIR="scs.orderDirection";
	public static final String PROP_SYMBOLS="scs.symbols";
	
	public static final String KEY_PARAM="scs.param";
	public static final String KEY_LU_UNIT="scs.param.luunit";
	public static final String KEY_INIDCATOR="scs.indicator";
	public static final String KEY_INIDCATOR_TYPE="type";
	public static final String KEY_INIDCATOR_PARAM="scs.param.indicator";
	
	public static final String KEY_CQ="cq";
	public static final String KEY_DIVSPLIT="divsplit";
	public static final String KEY_DIVIDEND="dividend";
	
	protected StockConfig sc;
	private String name;
	private int mbMemory=512;
	private String orderDirection;
	protected Map<String, Object> params = new TreeMap<String, Object>();
	protected Map<String, Indicator> indMap = new LinkedHashMap<String, Indicator>();//name map, keep insertion order
	private String baseMarketId;
	private IntervalUnit lookupUnit = IntervalUnit.day;
	private List<CqIndicators> cqilist = null;//passed in by init
	protected Map<String, DataMapper> dataMap=new HashMap<String, DataMapper>();

	public SelectStrategy(){
	}
	
	//to be overriden by subclass
	public void tradeCompleted(OrderFilled or){}
	public abstract SelectCandidateResult selectByStream(CqIndicators cqi);
	
	@JsonIgnore
	public Map<String, DataMapper> getDataMappers() {
		dataMap.put(SelectStrategy.KEY_CQ, quoteMapper());
		return dataMap;
	}
	//back testing
	public void initHistoryData(Map<String, List<? extends Object>> resultMap){
		cqilist = (List<CqIndicators>) resultMap.get(SelectStrategy.KEY_CQ);
	}
	//back testing, name of data set to data set map
	public List<SelectCandidateResult> selectByHistory(Map<String, List<? extends Object>> tableResults) {
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		if (!quoteMapper().oneFetch()){
			cqilist = (List<CqIndicators>) tableResults.get(SelectStrategy.KEY_CQ);
		}
		for (CqIndicators cqi:cqilist){
			SelectCandidateResult scr = selectByStream(cqi);
			if (scr!=null){
				scrl.add(scr);
			}
		}
		return scrl;
	}
	
	@JsonIgnore
	public int getMaxPeriod(){
		int maxPeriods=0;
		for (Indicator ind: indMap.values()){
			if (maxPeriods<ind.getPeriods()){
				maxPeriods = ind.getPeriods();
			}
		}
		return maxPeriods;
	}
	
	//init from properties file to paramMap, and paramMap can be serialized
	public void initProp(PropertiesConfiguration props){
		orderDirection = props.getString(KEY_ORDERDIR);//only this one not in param map
		String unit = (String) params.get(KEY_LU_UNIT);
		if (StrategyConst.V_UNIT_DAY.equals(unit)){
			setLookupUnit(IntervalUnit.day);
		}else if (StrategyConst.V_UNIT_MINUTE5.equals(unit)){
			setLookupUnit(IntervalUnit.minute5);
		}else if (StrategyConst.V_UNIT_MINUTE.equals(unit)){
			setLookupUnit(IntervalUnit.minute);
		}else if (StrategyConst.V_UNIT_TICK.equals(unit)){
			setLookupUnit(IntervalUnit.tick);
		}else {
			setLookupUnit(IntervalUnit.unspecified);
		}
		Iterator<String> ki = props.getKeys(KEY_INIDCATOR);
		while(ki.hasNext()){
			String pk = ki.next();
			if (pk.endsWith(KEY_INIDCATOR_TYPE)){
				String indName = pk.substring(KEY_INIDCATOR.length()+1, pk.length()-KEY_INIDCATOR_TYPE.length()-1);
				String indClass = props.getString(KEY_INIDCATOR + "." + indName + "." + KEY_INIDCATOR_TYPE);
				String paramPrefix = KEY_INIDCATOR_PARAM + "." + indName;
				try{
					Indicator indi = (Indicator) Class.forName(indClass).newInstance();
					Map<String, String> kv = new HashMap<String, String>();
					Iterator<String> paramKeys = props.getKeys(paramPrefix);
					String chartParam = KEY_INIDCATOR + "." + indName + "."+ Indicator.KEY_CHART;
					indi.setChartId(props.getString(chartParam));
					while (paramKeys.hasNext()){
						String paramKey = paramKeys.next();
						String paramValue = (String) params.get(paramKey);
						if (paramKey.equals(paramPrefix + "." + Indicator.KEY_PERIODS)){
							indi.setPeriods((int) Float.parseFloat(paramValue));
						}else{
							String stripParam = paramKey.substring(paramPrefix.length()+1);
							kv.put(stripParam, paramValue);
						}
					}
					indi.init(kv);
					indMap.put(indName, indi);
				}catch(Exception e){
					logger.error("", e);
				}
			}
		}
	}
	//called after initProp, before init
	protected Map<String, SelectStrategy> genBsMap(PropertiesConfiguration pc){
		//this is the seed SelectStrategy
		if (pc.containsKey(PROP_SYMBOLS)){
			String symbolFile = pc.getString(PROP_SYMBOLS);
			List<String> symbolList = StockUtil.getSymbols(symbolFile);
			Map<String, SelectStrategy> bsMap = new HashMap<String, SelectStrategy>();
			for (String symbol:symbolList){
				SelectStrategy bs = (SelectStrategy) JsonUtil.deepClone(this);
				bsMap.put(symbol, bs);
			}
			return bsMap;
		}else{
			//need to be populated by sub class
			return null;
		}
	}
	//init from paramMap to attributes, attributes may not be serializable
	public void init(){
		sc = StockUtil.getStockConfig(this.getBaseMarketId());
	}

	public String paramsToString(){
		StringBuffer sb = new StringBuffer();
		for (String key:params.keySet()){
			if (key.endsWith(Expression.p_expression)||
					key.endsWith(Indicator.KEY_CHART)){
				//
			}else{
				String v = (String) params.get(key);
				sb.append(v+":");
			}
		}
		return sb.toString();
	}

	public boolean allOneFetch(){
		for (DataMapper dm: getDataMappers().values()){
			if (!dm.oneFetch()){
				return false;
			}
		}
		return true;
	}
	
	protected DataMapper quoteMapper(){
		Object paramUnit = params.get(KEY_LU_UNIT);
		IntervalUnit unit = IntervalUnit.unspecified;
		if (paramUnit instanceof IntervalUnit){
			unit = (IntervalUnit) paramUnit;
		}else{
			String strParamUnit = (String) paramUnit;
			if (StrategyConst.V_UNIT_DAY.equals(strParamUnit)){
				unit = IntervalUnit.day;
			}else if (StrategyConst.V_UNIT_MINUTE.equals(strParamUnit)){
				unit = IntervalUnit.minute;
			}
		}
		if (IntervalUnit.day == unit){
			return sc.getBTFQDailyQuoteMapper();
		}else if (IntervalUnit.minute.equals(unit)){
			return sc.getBTFQMinuteQuoteMapper();
		}else{
			logger.error(String.format("unsupported lookup unit:%s", unit));
			return null;
		}
	}
	
	public static final double MIN_PRICE=3;//avoid penny stock
	public static final double MIN_AMOUNT=500000;//avoid dead stock, 1 million $ transaction at least
	public static boolean checkValid(CandleQuote cq){
		if (cq.getVolume()*(cq.getClose()/cq.getFqIdx())<MIN_AMOUNT ||
				(cq.getClose()/cq.getFqIdx())<MIN_PRICE
				){
			return false;
		}else{
			return true;
		}
	}
	
	private static SelectStrategy initBs(Class selectClass, Map<String, Object> pm, String baseMarketId, PropertiesConfiguration props, String strategyName, 
			Map<String, List<SelectStrategy>> bsMap ){
		try{
			SelectStrategy bs = (SelectStrategy) selectClass.newInstance();
			bs.setBaseMarketId(baseMarketId);
			bs.setParams(pm);
			bs.initProp(props);
			bs.setName(strategyName);
			bs.init();
			if (bsMap!=null){
				Map<String, SelectStrategy> map = bs.genBsMap(props);
				for (String symbol:map.keySet()){
					List<SelectStrategy> bsl = bsMap.get(symbol);
					if (bsl==null){
						bsl = new ArrayList<SelectStrategy>();
						bsMap.put(symbol, bsl);
					}
					SelectStrategy newBs = map.get(symbol);
					newBs.init();
					bsl.add(newBs);
				}
			}
			return bs;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	public static Map<String, List<SelectStrategy>> genMap(PropertiesConfiguration props, String simpleStrategyName, String baseMarketId){
		Map<String, List<SelectStrategy>> bsMap =new HashMap<String, List<SelectStrategy>>();
		Map<String, Object[]> paramMap = new HashMap<String,Object[]>();
		Iterator<String> paramKeyIt = props.getKeys(KEY_PARAM);
		while (paramKeyIt.hasNext()){
			String pk = paramKeyIt.next();
			paramMap.put(pk, StringUtil.parseSteps(props.getString(pk)));
		}
		try{
			Class selectClass = Class.forName(props.getString(KEY_SELECTS_TYPE));
			List<Map<String,Object>> paramsMapList = CombPermUtil.eachOne(paramMap);
			if (paramsMapList.size()>0){
				for (Map<String,Object> pm:paramsMapList){
					initBs(selectClass, pm, baseMarketId, props, simpleStrategyName, bsMap);
				}
			}else{//no param at all
				initBs(selectClass, null, baseMarketId, props, simpleStrategyName, bsMap);
			}
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
		return bsMap;
	}
	
	public static List<SelectStrategy> genList(PropertiesConfiguration props, String simpleStrategyName, String baseMarketId){
		List<SelectStrategy> bsList=new ArrayList<SelectStrategy>();
		Map<String, Object[]> paramMap = new HashMap<String,Object[]>();
		Iterator<String> paramKeyIt = props.getKeys(KEY_PARAM);
		while (paramKeyIt.hasNext()){
			String pk = paramKeyIt.next();
			paramMap.put(pk, StringUtil.parseSteps(props.getString(pk)));
		}
		try{
			Class selectClass = Class.forName(props.getString(KEY_SELECTS_TYPE));
			List<Map<String,Object>> paramsMapList = CombPermUtil.eachOne(paramMap);
			if (paramsMapList.size()>0){
				for (Map<String,Object> pm:paramsMapList){
					bsList.add(initBs(selectClass, pm, baseMarketId, props, simpleStrategyName, null));
				}
			}else{//no param at all
				bsList.add(initBs(selectClass, null, baseMarketId, props, simpleStrategyName, null));
			}
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
		return bsList;
	}
	
	public List<SelectCandidateResult> filterResults(List<SelectCandidateResult> inscrl, int selectNum){
		TreeMap<Float, List<SelectCandidateResult>> map = new TreeMap<Float, List<SelectCandidateResult>>();
		for (SelectCandidateResult scr: inscrl){
			List<SelectCandidateResult> scrl = map.get(scr.getValue());
			if (scrl==null){
				scrl = new ArrayList<SelectCandidateResult>();
				map.put(scr.getValue(), scrl);
			}
			map.put(scr.getValue(), scrl);
		}
		Iterator<Float> kl = null;
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		if (this.orderDirection.equals(StrategyConst.V_ASC)){
			kl = map.keySet().iterator();
		}else{
			kl = map.descendingKeySet().iterator();
		}
		int cnt=0;
		while(cnt<selectNum){
			if (kl.hasNext()){
				Float f = kl.next();
				List<SelectCandidateResult> l = map.get(f);
				int i=0;
				for (i=0; i<l.size();i++){
					SelectCandidateResult scr = l.get(i);
					logger.info(String.format("number %d:%s", i, scr));
					scrl.add(scr);
				}
				cnt+=(i+1);
			}else{
				break;
			}
		}
		return scrl;
	}
	
	//
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMbMemory() {
		return mbMemory;
	}
	public void setMbMemory(int mbMemory) {
		this.mbMemory = mbMemory;
	}
	public String getOrderDirection() {
		return orderDirection;
	}
	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}
	public String getBaseMarketId() {
		return baseMarketId;
	}
	public void setBaseMarketId(String baseMarketId) {
		this.baseMarketId = baseMarketId;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public void setParam(String key, Object value){
		params.put(key, value);
	}
	public IntervalUnit getLookupUnit() {
		return lookupUnit;
	}
	public void setLookupUnit(IntervalUnit lookupUnit) {
		this.lookupUnit = lookupUnit;
	}
	public Map<String, Indicator> getIndMap() {
		return indMap;
	}
	public void setIndMap(Map<String, Indicator> map) {
		indMap = map;
	}
}
