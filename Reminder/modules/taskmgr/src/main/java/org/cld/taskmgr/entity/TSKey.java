package org.cld.taskmgr.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class TSKey implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int runRound;
	private String tid;

	public TSKey(){
		
	}
	
	public TSKey(String tid, int runRound){
		this.tid = tid;
		this.runRound = runRound;
	}
	
	public String toString(){
		return "tid:" + tid + "\n" +
				"runRound:" + runRound;
	}
	
	public int getRunRound() {
		return runRound;
	}
	public void setRunRound(int runRound) {
		this.runRound = runRound;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	
}
