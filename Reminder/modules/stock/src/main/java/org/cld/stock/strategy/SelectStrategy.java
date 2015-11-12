package org.cld.stock.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.CandleQuote;
import org.cld.util.CombPermUtil;
import org.cld.util.StringUtil;
import org.cld.util.jdbc.JDBCMapper;

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
	
	private String name;
	private int mbMemory=512;
	private String orderDirection;
	protected Object[] params = new Object[]{};
	private String baseMarketId;
	
	public SelectStrategy(){
	}
	
	public void init(PropertiesConfiguration props){
		orderDirection = props.getString(KEY_ORDERDIR);
	}
	
	public String paramsToString(){
		return StringUtils.join(params, ":");
	}

	//to be overriden
	public void init(){}
	public void evalExp(){}
	public JDBCMapper[] getTableMappers(){return null;}
	public List<SelectCandidateResult> getSelectCandidate(Map<JDBCMapper, List<Object>> tableResults){return null;}

	public static final double MIN_PRICE=2;//avoid penny stock
	public static final double MIN_AMOUNT=1000000;//avoid dead stock, 1 million $ transaction at least
	public static boolean checkValid(CandleQuote cq){
		if (cq.getVolume()*(cq.getClose()/cq.getFqIdx())<MIN_AMOUNT ||
				(cq.getClose()/cq.getFqIdx())<MIN_PRICE
				){
			return false;
		}else{
			return true;
		}
	}
	
	public static List<SelectStrategy> gen(PropertiesConfiguration props, String simpleStrategyName){
		List<SelectStrategy> lss =new ArrayList<SelectStrategy>();
		List<Object[]> paramList = new ArrayList<Object[]>();
		for (int k=1;k<10;k++){
			String paramName = KEY_PARAM+"."+k;
			if (props.containsKey(paramName)){
				paramList.add(StringUtil.parseSteps(props.getString(paramName)));
			}else{
				break;
			}
		}
		try{
			Class selectClass = Class.forName(props.getString(KEY_SELECTS_TYPE));
			List<List<Object>> paramsList = CombPermUtil.eachOne(paramList);
			if (paramsList.size()>0){
				for (List<Object> pl:paramsList){
					SelectStrategy css = (SelectStrategy) selectClass.newInstance();
					css.init(props);
					Object[] params = new Object[pl.size()];
					params =  pl.toArray(params);
					css.setParams(params);
					css.evalExp();
					css.setName(simpleStrategyName);
					lss.add(css);
				}
			}else{
				SelectStrategy css = (SelectStrategy) selectClass.newInstance();
				css.setName(simpleStrategyName);
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
	
	public Object[] getParams() {
		return params;
	}
	
	public void setParams(Object[] params) {
		this.params = params;
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
}
