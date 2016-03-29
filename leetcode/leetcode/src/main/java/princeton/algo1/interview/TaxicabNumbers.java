package princeton.algo1.interview;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class TaxicabNumbers {
	class TaxiNumber{
		int a;
		int b;
		int sum;
		public TaxiNumber(int a, int b){
			this.a = a;
			this.b = b;
			this.sum = a^3 + b^3;
		}
	}
	class TaxiNumberComparator implements Comparator<TaxiNumber>{
		@Override
		public int compare(TaxiNumber o1, TaxiNumber o2) {
			return o1.sum-o2.sum;
		}
	}
	
	public List<List<Integer>> getAllTaxicabNumbers2(int n){//T: n^2*log(n), S: n^2
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		TaxiNumberComparator tncmp = new TaxiNumberComparator();
		PriorityQueue<TaxiNumber> tnpq = new PriorityQueue<TaxiNumber>(10, tncmp);
		for (int i=0; i<n; i++){
			for (int j=0; j<=i; j++){
				tnpq.add(new TaxiNumber(i,j));
			}
		}
		TaxiNumber t1=tnpq.poll();
		TaxiNumber t2=null;
		while (!tnpq.isEmpty()){
			t2 = tnpq.poll();
			if (t1.sum==t2.sum){
				List<Integer> al = new ArrayList<Integer>();
				al.add(t1.a);
				al.add(t1.b);
				al.add(t2.a);
				al.add(t2.b);
				ret.add(al);
			}
			t1 = t2;
		}
		return ret;
	}
	
	public List<List<Integer>> getAllTaxicabNumbers1(int n){//T: n^2*log(n), S: n
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		TaxiNumberComparator tncmp = new TaxiNumberComparator();
		PriorityQueue<TaxiNumber> tnpq = new PriorityQueue<TaxiNumber>(10, tncmp);
		for (int i=0; i<n; i++){
			for (int j=i+1; j<n; j++){
				tnpq.add(new TaxiNumber(i,j));
			}
		}
		TaxiNumber t1=tnpq.poll();
		TaxiNumber t2=null;
		while (!tnpq.isEmpty()){
			t2 = tnpq.poll();
			if (t1.sum==t2.sum){
				List<Integer> al = new ArrayList<Integer>();
				al.add(t1.a);
				al.add(t1.b);
				al.add(t2.a);
				al.add(t2.b);
				ret.add(al);
			}
			t1 = t2;
		}
		return ret;
	}

}
