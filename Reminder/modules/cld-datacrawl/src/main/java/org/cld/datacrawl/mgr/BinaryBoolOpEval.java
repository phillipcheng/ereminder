package org.cld.datacrawl.mgr;

import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.util.ScriptEngineUtil;
import org.xml.taskdef.BinaryBoolOp;
import org.xml.taskdef.OpType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class BinaryBoolOpEval {
	public static final String NULL_VAL="null";
	
	private static Logger logger =  LogManager.getLogger(BinaryBoolOpEval.class);
	
	public static boolean eval(BinaryBoolOp bbo, Map<String, Object> attributes, HtmlPage page, CrawlConf cconf){
		if (bbo.getExpression()!=null){
			Boolean ret = (Boolean)ScriptEngineUtil.eval(bbo.getExpression(), VarType.BOOLEAN, attributes);
			return ret.booleanValue();
		}else{
			ValueType lhsVar = bbo.getLhs(); //variable
			OpType op = bbo.getOperator();
			ValueType rhsVar = bbo.getRhs();
			try {
				Object lhsV = CrawlTaskEval.eval(page, lhsVar, cconf, attributes);
				Object rhsV = CrawlTaskEval.eval(page, rhsVar, cconf, attributes);
				logger.debug("binary bool: l:" + lhsV + ", r:" + rhsV );
				if (NULL_VAL.equals(lhsV)){
					lhsV = null;
				}
				if (NULL_VAL.equals(rhsV)){
					rhsV = null;
				}
				if (OpType.NOTEQUALS == op){//!=
					return !Objects.equals(lhsV, rhsV);
				}else{//=
					return Objects.equals(lhsV, rhsV);
				}
			}catch(Exception e){
				logger.error("", e);
				return false;
			}
		}
	}
}
