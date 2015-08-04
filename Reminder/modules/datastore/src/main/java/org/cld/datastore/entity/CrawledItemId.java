package org.cld.datastore.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CrawledItemId implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "id")
	private String id;

	@Column(name = "storeId")
	private String storeId;

	@Column(name = "createTime")
	private Date createTime;//crawl time
	
	public CrawledItemId() {
	}
	
	public CrawledItemId(String id, String storeId, Date createTime) {
		this.id = id;
		this.storeId = storeId;
		this.createTime = createTime;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(id).append(", storeId:").append(storeId).append(", createTime:").append(createTime);
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof CrawledItemId){
			CrawledItemId ci=(CrawledItemId)obj;
			if (id.equals(ci.getId()) && storeId.equals(ci.getStoreId()) 
					&& createTime.equals(ci.getCreateTime())){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public boolean contentEquals(CrawledItemId cid){
		if (cid!=null){
			return this.id.equals(cid.getId()) 
					&& this.getStoreId().equals(cid.getStoreId());
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		int hc = id.hashCode();
		hc += storeId.hashCode();
		hc += createTime.hashCode();
		return hc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

}
