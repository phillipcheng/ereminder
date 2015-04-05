package org.cld.datacrawl.mgr.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.util.StringUtil;
import org.xml.taskdef.BinaryBoolOp;
import org.xml.taskdef.OpType;
import org.xml.taskdef.ValueType;

import com.gargoylesoftware.htmlunit.html.DomNode;


public class BinaryBoolOpEval {
	public static final String NULL_VAL="null";
	
	private static Logger logger =  LogManager.getLogger(BinaryBoolOpEval.class);
	
	public static boolean eval(DomNode page, CrawlConf cconf, BinaryBoolOp bbo, Map<String, Object> attributes){
		if (attributes==null){
			attributes = new HashMap<String, Object>();
		}
		ValueType lhsVar = bbo.getLhs(); //variable
		OpType op = bbo.getOperator();
		ValueType rhsVar = bbo.getRhs();
		try {
			Object lhsV = CrawlTaskEval.eval(page, lhsVar, cconf, attributes);
			Object rhsV = CrawlTaskEval.eval(page, rhsVar, cconf, attributes);
			if (NULL_VAL.equals(lhsV)){
				lhsV = null;
			}
			if (NULL_VAL.equals(rhsV)){
				rhsV = null;
			}
			if (OpType.NOTEQUALS.equals(op.value())){//!=
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
