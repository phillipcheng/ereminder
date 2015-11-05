package org.cld.stock.strategy;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.JDBCMapper;

public abstract class SelectStrategyMapperByStock extends SelectStrategy {
	
	public static Logger logger = LogManager.getLogger(SelectStrategyMapperByStock.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat detailSdf = new SimpleDateFormat("yyyyMMddhhmmss");

	private String baseMarketId;
	
	public void init(){
	}
	
	public void init(PropertiesConfiguration props){
		super.init(props);
	}
	
	public abstract JDBCMapper[] getTableMappers();
	public abstract List<SelectCandidateResult> getSelectCandidate(Map<JDBCMapper, List<Object>> tableResults);
	public void evalExp(){};
	
	//
	public String getBaseMarketId() {
		return baseMarketId;
	}

	public void setBaseMarketId(String baseMarketId) {
		this.baseMarketId = baseMarketId;
	}
}
