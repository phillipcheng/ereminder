package org.cld.taskmgr;

import java.util.ArrayList;
import java.util.List;

public class BatchTaskOperation {
	private String batchId; //for crawl app, this is category id
	private List<TaskOperation> toList = new ArrayList<TaskOperation>(); 
	
	public void addOperation(TaskOperation to){
		toList.add(to);
	}
	
	public List<TaskOperation> getTOList(){
		return toList;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	public String toString(){
		return "batchId:" + batchId + ", task operation list:" + toList;
	}

}
