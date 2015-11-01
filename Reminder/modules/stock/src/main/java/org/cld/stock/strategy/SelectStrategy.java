package org.cld.stock.strategy;

import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockConfig;
import org.cld.util.jdbc.DBConnConf;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class SelectStrategy {
	public static Logger logger = LogManager.getLogger(SelectStrategy.class);
	
	public static String KEY_SELECTS_MEMORY="scs.memory";
	public static String KEY_SELECTS_TYPE="scs.type";
	public static String KEY_SELECTS_LIMIT="scs.limit";
	public static String KEY_SELECTS_byDayType="scs.byDayType";
	public static String VAL_ByCalendarDay="byCalendarDay";
	public static String VAL_ByTradingDay="byTradingDay";
	
	public enum byDayType{
		byCalendarDay,
		byTradingDay
	}
	
	private String name;
	private byDayType dayType = byDayType.byTradingDay;
	private int mbMemory=512;
	protected Object[] params = new Object[]{};
	
	public String paramsToString(){
		StringBuffer sb = new StringBuffer();
		for (Object param:params){
			sb.append(param).append(",");
		}
		return sb.toString();
	}
	
	public SelectStrategy(){
	}
	
	public abstract List<SelectStrategy> gen(PropertiesConfiguration props, String simpleStrategyName);
	public void init(PropertiesConfiguration props){
		if (props.containsKey(SelectStrategy.KEY_SELECTS_MEMORY)){
			this.mbMemory = props.getInt(SelectStrategy.KEY_SELECTS_MEMORY);
		}
        if (props.containsKey(KEY_SELECTS_byDayType)){
        	if (VAL_ByCalendarDay.equals(props.getProperty(KEY_SELECTS_byDayType))){
        		this.setDayType(byDayType.byCalendarDay);
        	}else if (VAL_ByTradingDay.equals(props.getProperty(KEY_SELECTS_byDayType))){
        		this.setDayType(byDayType.byTradingDay);
        	}else{
        		logger.error("unsupported byDayType:" + props.getProperty(KEY_SELECTS_byDayType));
        	}
        }
	}
	//select the top N stock from the market using the select strategy given the dynamic param (date, etc) and other static parameter (percentage)
	public abstract List<String> select(DBConnConf cconf, Date dt, StockConfig sc);
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public byDayType getDayType() {
		return dayType;
	}
	public void setDayType(byDayType dayType) {
		this.dayType = dayType;
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
}
