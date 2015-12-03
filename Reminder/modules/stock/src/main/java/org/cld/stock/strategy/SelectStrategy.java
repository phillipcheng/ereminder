package org.cld.stock.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.stock.CandleQuote;
import org.cld.util.CombPermUtil;
import org.cld.util.DataMapper;
import org.cld.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class SelectStrategy {
	public static Logger logger = LogManager.getLogger(SelectStrategy.class);
	
	public static final String KEY_SELECTS_MEMORY="scs.memory";
	public static final String KEY_SELECTS_TYPE="scs.type";
	public static final String KEY_SELECTS_LIMIT="scs.limit";
	public static final String KEY_ORDERDIR="scs.orderDirection";
	public static String ASC = "asc";
	public static String DESC = "desc";
	
	public static final String KEY_PARAM="scs.param";
	public static final int LOOKUP_DAYS=50;
	
	private String name;
	private int mbMemory=512;
	private String orderDirection;
	protected Map<String, Object> params = new HashMap<String, Object>();
	private String baseMarketId;
	
	public SelectStrategy(){
	}
	
	public void init(PropertiesConfiguration props){
		orderDirection = props.getString(KEY_ORDERDIR);
	}
	
	public String paramsToString(){
		return StringUtils.join(params.values(), ":");
	}

	//to be overriden
	public void init(){}
	public void evalExp(){}
	public DataMapper[] getDataMappers(){return null;}
	public List<SelectCandidateResult> selectByHistory(Map<DataMapper, List<Object>> tableResults){return null;}
	public List<SelectCandidateResult> selectByCurrent(CrawlConf cconf, String baseMarketId, String marketId, Date submitDay, int n, Map<String, Float> newQuotes){return null;}
	public String[] prepareData(String baseMarketId, String marketId, CrawlConf cconf, String propfile, Date start, Date end){return null;}
	
	
	//
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
					SelectStrategy css = (SelectStrategy) selectClass.newInstance();
					css.setBaseMarketId(baseMarketId);
					css.init(props);
					css.setParams(pm);
					css.evalExp();
					css.setName(simpleStrategyName);
					lss.add(css);
				}
			}else{
				SelectStrategy css = (SelectStrategy) selectClass.newInstance();
				css.setName(simpleStrategyName);
				css.setBaseMarketId(baseMarketId);
				css.init(props);
				lss.add(css);
			}
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
		return lss;
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
}
