package org.cld.taskmgr.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RunType{
	/**
	 * Sample tasks
	 * Ta
	 *  |
	 * Tb1 		 	Tb2
	 *	|			|
	 * Tc1 Tc2 	    Td1 Td2
	 */
	onePrd(0),  //taskName Ta, traverse Ta
	oneLevel(1), //taskName Ta, traverse Ta, Tb1, Tb2
	onePath(2), //taskName Ta, traverse Ta, Tb1, Tc1
	all(3);//taskName Ta, traverse all
	
	private int id;
	private RunType(final int id){
		this.id = id;
	}
	
	@JsonValue
	public int getId(){
		return id;
	}
}
