package org.cld.stock.strategy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.CombPermUtil;
import org.cld.util.StringUtil;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class SelectStrategy {
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
	
	public SelectStrategy(){
	}
	
	public void init(PropertiesConfiguration props){
		orderDirection = props.getString(KEY_ORDERDIR);
	}
	
	public String paramsToString(){
		StringBuffer sb = new StringBuffer();
		for (Object param:params){
			sb.append(param).append(",");
		}
		return sb.toString();
	}
	
	public abstract void evalExp();
	
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
}
