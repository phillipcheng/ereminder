package org.cld.stock.strategy;

public enum IntervalUnit {
	tick,
	minute,
	minute5,
	day,
	unspecified;
	
	private static IntervalUnit[] vals = values();
    public IntervalUnit next() {
        return vals[(this.ordinal()+1) % vals.length];
    }
}
