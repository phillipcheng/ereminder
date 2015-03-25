package org.cld.util;

public class PatternIO{
	boolean findPattern = false;
	boolean verified = false;
	int count0=0;//matched starting page, index starting from 1
	PatternResult pr=null;
	
	public PatternIO(){
		
	}
	
	public PatternResult getPR(){
		return pr;
	}
	
	public PatternIO(boolean findPattern, boolean verified, int count0, PatternResult pr){
		this.findPattern = findPattern;
		this.verified= verified;
		this.count0 = count0;
		this.pr = pr;
	}
	
	public boolean isFinished(){
		if (verified && findPattern){
			return true;
		}else{
			return false;
		}
	}
}