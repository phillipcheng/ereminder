package org.cld.datacrawl;

public class CrawlConfChangedEvent {
	
	public static final int OP_ADD=1;
	public static final int OP_REMOVE=2;
	public static final int OP_UPDATE=3;

	public int opType;
	
	//used for cctcValue
	public static final int SUB_OP_CAT=1;
	public static final int SUB_OP_LIST=2;
	
	private int subOpType;

	public static final String PROP_NAME_CTCONF = "CrawlTaskConf"; //crawl-task template
	public static final String PROP_NAME_PRODUCT_DEF = "PrdDef"; //product definition
	public static final String PROP_NAME_TASK_DEF="TaskDef";//task definition
	public static final String PROP_NAME_TASK="Task";//task definition
	
	private String propName="";
	
	//the new value
	private int intValue=-1;
	private String strValue=null;
	private ProductConf prdConfValue = null;

	private boolean booleanValue=false;
	
	public String toString(){
		return "opType:" + opType + "\n" + 
				"propName:" + propName + "\n" +
				"subOpType" + subOpType + "\n" +
				"intValue:" + intValue + "\n" + 
				"strValue:" + strValue + "\n" + 
				"booleanValue:" + booleanValue + "\n" +
				"prdConfValue:" + prdConfValue + "\n";
	}
	
	public boolean equals(Object o){
		CrawlConfChangedEvent ccce = (CrawlConfChangedEvent)o;
		if ((opType == ccce.opType) && 
			(subOpType==ccce.subOpType) &&
			(propName.equals(ccce.propName))
			){
			if (PROP_NAME_PRODUCT_DEF.equals(propName)){
				if (prdConfValue.getName().equals(ccce.prdConfValue.getName())){
					return true;
				}else
					return false;
			}else{
				return true;
			}
		}else{
			return false;
		}
	}
	
	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
	}

	public int getSubOpType() {
		return subOpType;
	}

	public void setSubOpType(int subOpType) {
		this.subOpType = subOpType;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public boolean isBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public ProductConf getPrdConfValue() {
		return prdConfValue;
	}

	public void setPrdConfValue(ProductConf prdConfValue) {
		this.prdConfValue = prdConfValue;
	}

}
