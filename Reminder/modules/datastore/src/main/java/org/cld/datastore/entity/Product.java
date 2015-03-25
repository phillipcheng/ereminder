package org.cld.datastore.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@DiscriminatorValue("product")
@Table(name = "Product")
public class Product extends CrawledItem{
	public static final Logger logger = LogManager.getLogger(Product.class);
	public static final String CRAWLITEM_TYPE="org.cld.datastore.entity.Product";
	public static String CAT_SEP=":";
	
	public Product(){
		super(CRAWLITEM_TYPE);
	}
	
	public Product(String itemType) {
		super(CRAWLITEM_TYPE, itemType);
	}

	public Product(CrawledItemId cid, String itemType, String name, double originalPrice, double currentPrice) {
		super(CRAWLITEM_TYPE, itemType, cid);
		super.name = name;
		this.originalPrice = originalPrice;
		this.currentPrice = currentPrice;
		type="product";
	}

	@Column(name = "oPrice")
	private double originalPrice;

	@Column(name = "cPrice")
	private double currentPrice;

	@Column(name = "catlist", length=800)
	private String catlist; //: Separated category list
	
	@Column(name = "externalId")
	private String externalId; //external id, remains the same across sites, like ISBN for book, ISRC for CD, etc.

	@Column(name = "completed")
	private boolean completed;
	
	@Column(name = "lastUrl")
	private String lastUrl;

	@Column(name = "totalPage")
	private int totalPage;
	
	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + 
				"Product [id=" + id + ", type=" + type + ", originalPrice=" + originalPrice
				+ ", currentPrice=" + currentPrice + ", catlist=" + catlist + ", externalId="
				+ externalId + "]";
	}

	public String getLastUrl() {
		return lastUrl;
	}

	//using setLastUrl to tell engine, last time crawling stops here, 
	//if not set, every time needed to crawl, crawl from beginning.
	public void setLastUrl(String lastUrl) {
		this.lastUrl = lastUrl;
	}
	
	public String getCatlist(){
		return catlist;
	}
	
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public double getOriginalPrice() {
		return originalPrice;
	}
	public void setOriginalPrice(double originalPrice) {
		this.originalPrice = originalPrice;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}
	
	//return true when added
	public boolean addCat(String cat){
		if (catlist == null){
			catlist = cat;
			return true;
		}else{
			if (catlist.contains(cat)){
				return false;
			}else{
				this.catlist += CAT_SEP + cat;
				return true;
			}
		}
	}
	
	public boolean belongs(String cat){
		if (catlist!=null && catlist.contains(cat)){
			return true;
		}else{
			return false;
		}
	}
	
	public String getFirstCat(){
		if (catlist != null){
			if (catlist.contains(CAT_SEP)){
				return catlist.substring(catlist.indexOf(CAT_SEP));
			}else{
				return catlist;
			}
		}else{
			return null;
		}
	}

}
