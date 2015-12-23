package org.cld.datacrawl.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class VerifyPageByBoolOpXPath implements VerifyPage {
	public static Logger logger = LogManager.getLogger(VerifyPageByBoolOpXPath.class);
	
	private VerifyPageByBoolOp vpbop;
	private VerifyPageByXPath vpbxp;
	
	public VerifyPageByBoolOpXPath(VerifyPageByBoolOp vpbop, VerifyPageByXPath vpbxp){
		this.vpbop = vpbop;
		this.vpbxp = vpbxp;
	}
	
	
	@Override
	public boolean verifySuccess(HtmlPage page, Object param) {
		boolean ret1 = true;
		boolean ret2 = true;
		if (vpbop!=null){
			ret1 = vpbop.verifySuccess(page, param);
		}
		if (vpbxp!=null){
			ret2 = vpbxp.verifySuccess(page, param);
		}
		return ret1 && ret2;
	}
}
