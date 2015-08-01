package org.cld.datacrawl.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.entity.TaskStat;

@Entity
@DiscriminatorValue("browse_cat")
public class BrsCatStat extends TaskStat implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(BrsCatStat.class);
	
	private transient Set<BrowseCategoryTaskConf> brokenCat = new HashSet<BrowseCategoryTaskConf>();


	private int catBrowsed; //TODO looks only counts the 1st level sub category, needs investigation
	
	public BrsCatStat(){
	}
	
	
	/*
	 * add a broken category
	 */
	public void addBrokenCat(BrowseCategoryTaskConf cat){
		brokenCat.add(cat);		
	}
	public void addSuccessCat(BrowseCategoryTaskConf cat){
		brokenCat.remove(cat);
		catBrowsed++;
	}
	
	
	public void clear(){
		this.catBrowsed=0;
		this.brokenCat.clear();
	}
	
	public String toString(){
		return  "\ncat_browsed:" + catBrowsed + "\n"
				+ "broken_cat:" + brokenCat.size() + "\n"
				+ brokenCat.toString() + "\n"
				;
				
	}
	
	public boolean hasBroken(){
		return (brokenCat.size()>0);
	}
	
	public int getCatBrowsed() {
		return catBrowsed;
	}
	

	///////////////////////////////////
	public int getBrokenCatNum() {
		return brokenCat.size();
	}
	
	public void setCatBrowsed(int catBrowsed) {
		this.catBrowsed = catBrowsed;
	}
	public Set<BrowseCategoryTaskConf> getBrokenCat() {
		return brokenCat;
	}
	public void setBrokenCat(Set<BrowseCategoryTaskConf> brokenCat) {
		this.brokenCat = brokenCat;
	}
}
