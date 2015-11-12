package org.cld.trade;

import java.util.List;

//process result
public class TradeMsgPR {

	boolean executed=false;//whether this message is executed, if yes, then it will be deleted, if not it will remain there to be executed in next round
	List<TradeMsg> newMsgs; //to add new messages
	List<String> rmMsgs;//to remove msg ids
	String msgId;
	
	public TradeMsgPR(){	
	}
	
	public TradeMsgPR(boolean executed, List<TradeMsg> newMsgs, List<String> rmMsgs){
		this.executed = executed;
		this.newMsgs = newMsgs;
		this.rmMsgs = rmMsgs;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public List<TradeMsg> getNewMsgs() {
		return newMsgs;
	}

	public void setNewMsgs(List<TradeMsg> newMsgs) {
		this.newMsgs = newMsgs;
	}

	public List<String> getRmMsgs() {
		return rmMsgs;
	}

	public void setRmMsgs(List<String> rmMsgs) {
		this.rmMsgs = rmMsgs;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
}
