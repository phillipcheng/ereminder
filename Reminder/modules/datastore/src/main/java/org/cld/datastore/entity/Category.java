package org.cld.datastore.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.StringUtil;

@Entity
@DiscriminatorValue("category")
@Table(name = "Category")
public class Category extends CrawledItem{

	private static Logger logger =  LogManager.getLogger(Category.class);
	
	public static final String CATID_SEP="|";
	public static final String CRAWLITEM_TYPE="org.cld.datastore.entity.Category";
	
	public Category(){
		super(CRAWLITEM_TYPE);
	}
	
	public Category(String prdType) {
		super(CRAWLITEM_TYPE, prdType);
	}
	
	public Category(CrawledItemId cid, String prdType) {
		super(CRAWLITEM_TYPE, prdType, cid);
	}
	
	@Column(name = "leaf")
	private boolean leaf;
	
	//the following 4 attributes are related
	//1)if this category/site does not limit the number of pages returned (ctconf.forePageLimit is not set)
	//user only needs to set the pageNum which is the real page number of this category and we will use this to split task to improve performance
	//2)when ctconf.forcePageLimit is set:
	//user needs to set itemNum (the total number of items), pageSize and pagelimit, 
	//since some of the items are not displayed because of the limitation, 
	//so we need to use split category technology in order to crawl more
	//3)user can also left all these not set, then this category will be browsed in one task
	@Column(name = "pagelimit")
	private int pagelimit=0; //# of pages limited per search
	@Column(name = "pageNum")
	private int pageNum=0;//# of pages of this category
	@Column(name = "pageSize")
	private int pageSize=0;//# items per page
	@Column(name = "itemNum")
	private int itemNum=0;//total item
	
	//same category has two id, 1 number id and 1 string id, used in different places
	@Column(name = "id2")
	private String id2;
	
	@Column(name="lastItem")
	private String lastItem; //specify the latest item for this category, if changed, re-analyze
	
	public String toString(){
		return id.toString() + ", id2:" + id2 + ", pageNum:" + pageNum + ", pageLimit:" + pagelimit + ", isLeaf:" + leaf + 
				", lastItem:" + lastItem;
	}
	
	public boolean contentEquals(Object o){
		if (o!=null){
			Category ca = (Category) o;
			return super.contentEquals(ca) 
					&& StringUtil.nullEquals(this.getId2(), ca.getId2())
					&& StringUtil.nullEquals(this.getLastItem(), ca.getLastItem())
					&& this.isLeaf()==ca.isLeaf()
					&& this.pageNum == ca.getPageNum()
					&& this.pageSize == ca.getPageSize()
					&& this.itemNum == ca.getItemNum();
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return this.id.hashCode();
	}
	
	//return the real cat id, trim the splitValue part
	public String getRealCatId(){
		String catId = id.getId();
		if (catId != null){
			if (catId.contains(CATID_SEP)){
				return catId.substring(0, catId.lastIndexOf(CATID_SEP, catId.length()));
			}else{
				return catId;
			}
		}else
			return null;
	}
	
	public void setCatId(String realCatId, String splitValue){
		id.setId(realCatId + CATID_SEP + splitValue);
	}
	
	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public int getPagelimit() {
		return pagelimit;
	}

	public void setPagelimit(int pagelimit) {
		this.pagelimit = pagelimit;
	}
	
	public String getId2() {
		return id2;
	}

	public void setId2(String id2) {
		this.id2 = id2;
	}

	public String getLastItem() {
		return lastItem;
	}

	public void setLastItem(String lastItem) {
		this.lastItem = lastItem;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getItemNum() {
		return itemNum;
	}

	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}
}
