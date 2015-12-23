package org.cld.util.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
//@Table(name = "Store", schema = "reminder@kundera_cassandra_cld")
@Table(name = "Store")
public class Store{

	public Store() {
	}

	public Store(String name, String storeId) {
		this.name = name;
		this.storeId = storeId;
	}

	@Column(name = "name")
	private String name;

	@Id
	private String storeId;


	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
