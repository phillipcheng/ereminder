package org.cld.stock.strategy;

import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.stock.StockConfig;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class SelectStrategy {
	public static Logger logger = LogManager.getLogger(SelectStrategy.class);
	
	public static String KEY_SELECTS_TYPE="scs.type";
	public static String KEY_SELECTS_NAME="scs.name";
	public static String KEY_SELECTS_LIMIT="scs.limit";
	public static String KEY_SELECTS_byDayType="scs.byDayType";
	public static String VAL_ByCalendarDay="byCalendarDay";
	public static String VAL_ByTradingDay="byTradingDay";
	
	
	public enum byDayType{
		byCalendarDay,
		byTradingDay
	}
	
	private String name;
	private String outputDir;
	private byDayType dayType = byDayType.byTradingDay;
	private int limit=10;//topN

	public SelectStrategy(){
	}
		
	public void init(PropertiesConfiguration props){
		setName(props.getString(SelectStrategy.KEY_SELECTS_NAME));
        if (props.containsKey(SelectStrategy.KEY_SELECTS_LIMIT))
        	setLimit(props.getInt(SelectStrategy.KEY_SELECTS_LIMIT));
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
	public abstract List<String> select(CrawlConf cconf, Date dt, StockConfig sc);
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOutputDir() {
		return outputDir;
	}
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	
	public byDayType getDayType() {
		return dayType;
	}
	public void setDayType(byDayType dayType) {
		this.dayType = dayType;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
