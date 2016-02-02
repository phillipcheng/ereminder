package org.cld.datacrawl.test;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BrowseType{
	oneLevel(0),
	onePath(1),
	recursive(2),
	category(3),
	details(4),
	detailsTurnPageOnly(5),
	product(6);
	
	private int id;
	private BrowseType(final int id){
		this.id = id;
	}
	
	@JsonValue
	public int getId(){
		return id;
	}
}
