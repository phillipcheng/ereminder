package org.cld.taskmgr;

//specific node conf property changed
public class NodeConfPropChangedEvent {
	
	public static final int OP_ADD=1;
	public static final int OP_REMOVE=2;
	public static final int OP_UPDATE=3;
	public int opType;
	
	private String propName="";
	
	//the new value
	private int intValue=-1;
	private String strValue=null;
	private Object objValue = null;
	private Object oldObjValue = null;
	private boolean booleanValue=false;
	
	public String toString(){
		return "opType:" + opType + "\n" + 
				"propName:" + propName + "\n" +
				"intValue:" + intValue + "\n" + 
				"strValue:" + strValue + "\n" +
				"booleanValue:" + booleanValue + "\n" +
				"objValue:" + objValue + "\n" + 
				"oldObjValue:" + oldObjValue + "\n";
	}

	public boolean equals(Object o){
		return false;
	}
	
	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
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
	
	public void setObjectValue(Object objValue){
		this.objValue = objValue;
	}
	
	public Object getObjectValue(){
		return this.objValue;
	}

	public boolean isBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public Object getOldObjValue() {
		return oldObjValue;
	}

	public void setOldObjValue(Object oldObjValue) {
		this.oldObjValue = oldObjValue;
	}
}
