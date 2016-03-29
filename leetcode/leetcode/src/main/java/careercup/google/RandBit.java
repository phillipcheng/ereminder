package careercup.google;

public class RandBit {
/*
 * You are given a function bool rand_bit_p() that returns true with some unknown probability p 
 * and false with probability 1 - p. 
 * Write function rand_bit() using rand_bit_p that will return true and false with equal probability 
 * (that is, implement a fair coin, given unfair coin)
 */
	
	public boolean rand_unfair_bit(){
		return true; //return true in probability p
	}
	
	//return true or false in probability 1/2
	public boolean rand_fair_bit(){
		while (true){
			boolean p1 = rand_unfair_bit();
			boolean p2 = rand_unfair_bit();
			if (p1 && !p2) return true;
			if (!p1 && p2) return false;
		}
	}
}
