package org.cld.stock.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.stock.CandleQuote;
import org.cld.stock.CqIndicators;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.indicator.Expression;
import org.cld.stock.indicator.Indicator;
import org.cld.util.CombPermUtil;
import org.cld.util.DataMapper;
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
	protected List<Indicator> indList = new ArrayList<Indicator>();//order in the properties
	protected Map<String, Indicator> indMap = new HashMap<String, Indicator>();//name map
	private String baseMarketId;
	private IntervalUnit lookupUnit = IntervalUnit.day;
	@JsonIgnore
	private List<CqIndicators> cqilist = null;//passed in by init
	@JsonIgnore
	protected Map<String, DataMapper> dataMap=new HashMap<String, DataMapper>();

	public SelectStrategy(){
	}
	

	//streaming
	public abstract SelectCandidateResult selectByStream(CqIndicators cqi);
	
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
		for (Indicator ind: indList){
			if (maxPeriods<ind.getPeriods()){
				maxPeriods = ind.getPeriods();
			}
		}
		return maxPeriods;
	}
	
	public void initProp(PropertiesConfiguration props){
		orderDirection = props.getString(KEY_ORDERDIR);//only this one not in param map
		String unit = (String) params.get(KEY_LU_UNIT);
		if (StrategyConst.V_UNIT_DAY.equals(unit)){
			setLookupUnit(IntervalUnit.day);
		}else if (StrategyConst.V_UNIT_MINUTE.equals(unit)){
			setLookupUnit(IntervalUnit.minute);
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
					indList.add(indi);
					indMap.put(indName, indi);
				}catch(Exception e){
					logger.error("", e);
				}
			}
		}
	}
	
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
	
	public static List<SelectStrategy> gen(PropertiesConfiguration props, String simpleStrategyName, String baseMarketId){
		List<SelectStrategy> lss =new ArrayList<SelectStrategy>();
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
					SelectStrategy bs = (SelectStrategy) selectClass.newInstance();
					bs.setBaseMarketId(baseMarketId);
					bs.setParams(pm);
					bs.initProp(props);
					bs.setName(simpleStrategyName);
					lss.add(bs);
				}
			}else{//no param at all
				SelectStrategy bs = (SelectStrategy) selectClass.newInstance();
				bs.setBaseMarketId(baseMarketId);
				bs.setName(simpleStrategyName);
				bs.initProp(props);
				lss.add(bs);
			}
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
		return lss;
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
	public List<Indicator> getIndList() {
		return indList;
	}
	public void setIndList(List<Indicator> indList) {
		this.indList = indList;
	}
	public Map<String, Indicator> getIndMap() {
		return indMap;
	}
}
