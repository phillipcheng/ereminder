package zj;

import java.io.Serializable;

public class MyDate extends Object implements Serializable, Comparable<MyDate>, Cloneable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1;

	
	int year;
	int month;
	int day;
	public MyDate next;
	
	public MyDate(){		
	}
	
	public MyDate(int a, int b, int c){
		this.year=a;
		this.month = b;
		this.day= c;
		this.next = null;
	}
	
	public MyDate(int a, int b, int c, MyDate d){
		this.year=a;
		this.month = b;
		this.day= c;
		this.next = d;
	}
	
	public String toString() {
		String dateStr = new String();
		dateStr = year + "/" + month + "/" + day;
		if (next == null){			
			return dateStr;
		}else{
			return dateStr + "," + next.toString();
		}
	}
		
	
	@Override
	public boolean equals(Object o) {
		MyDate anaDate = null;
		if(this==o) return true;
		
		if (o instanceof MyDate) {
			anaDate = (MyDate) o;
		}else{
			return false;
		}
		
		if ( year == anaDate.year && month == anaDate.month && day==anaDate.day) {
			return true;
		} 
		
		return false;
	}

	@Override
	public int hashCode(){
		return year+month+day;
	}

	@Override
	public int compareTo(MyDate o) {
		if (this.year == o.year && this.month==o.month && this.day==o.day)	return 0;
		if (this.year > o.year) return 1;
			else if (this.year == o.year && this.month > o.month ) return 1;
			else if (this.year == o.year && this.month == o.month && this.day > o.day) return 1;
			else return -1;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		 return super.clone();
	}
	
	public static void main (String arg[]) {
		
	}


}
