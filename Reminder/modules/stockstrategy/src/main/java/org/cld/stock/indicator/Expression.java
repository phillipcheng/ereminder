package org.cld.stock.indicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.ScriptEngineUtil;
import org.xml.taskdef.VarType;

//simple moving average
public class Expression extends Indicator{

	private static Logger logger =  LogManager.getLogger(Indicator.class);
	public static final String p_expression="expression";
	private String exp;

	public Expression(){
	}
	
	@Override
	public void init(Map<String, String> params) {
		exp = params.get(p_expression);
		super.getRmap().put(toKey(), RenderType.line);
	}
	
	@Override
	public Object calculate(CqIndicators cqi, SelectStrategy bs){
		Map<String,Object> variables = new HashMap<String, Object>();
		for (String name:bs.getIndMap().keySet()){
			Indicator ind = bs.getIndMap().get(name);
			String key = ind.toKey();
			if (cqi.hasIndicator(key)){
				float v = (float) cqi.getIndicator(key);
				if (v==Indicator.V_NA){
					//variables.put(name, null);
				}else{
					variables.put(name, v);
				}
			}
		}
		
		Float v= (Float) ScriptEngineUtil.eval(exp, VarType.FLOAT, variables, false);
		if (v==null){
			return 0f;
		}else{
			return v.floatValue();
		}
	}

	@Override
	public String toKey() {
		return String.format("Exp");
	}

	public String getExp() {
		return exp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}
}
