package org.cld.datastore.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.cld.util.DateTimeUtil;

@Entity
@Table(name = "Price")
public class Price implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String PROM_ID_SEP=",";
	
	public Price() {
	}

	public Price(String productId, Date createTime, String storeId, double price,
			String promotionIds) {
		this.id = new CrawledItemId(productId, storeId, createTime);
		this.price = price;
		this.promotionIds = promotionIds;
	}

	public String toString(){
		String idstring="";
		if (id!=null){
			idstring = "id:" + id.getId() + ";createDateTime:" + DateTimeUtil.sddf.format(id.getCreateTime());
		}
		return idstring + "; price:" + price + "; promotionId:" + promotionIds;
	}
	
	@EmbeddedId
	private CrawledItemId id;

	@Column(name = "price")
	private double price;

	@Column(name = "promotionIds")
	private String promotionIds;

	public CrawledItemId getId() {
		return id;
	}

	public void setId(CrawledItemId id) {
		this.id = id;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public static boolean looksChanged(Price summaryPrice, Price newPrice){
		if (newPrice!=null && summaryPrice!=null){
			return summaryPrice.getPrice() != newPrice.getPrice();
		}else{
			return false;
		}
	}

}
