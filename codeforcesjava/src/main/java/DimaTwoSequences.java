import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;


public class DimaTwoSequences {
	static class IndexedItem{
		int idx;
		int value;
		IndexedItem(int idx, int value){
			this.idx = idx;
			this.value = value;
		}
	}

	static class IndexedItemValueComparator implements Comparator<IndexedItem>{
		public int compare(IndexedItem o1, IndexedItem o2) {
			return o1.value - o2.value;
		}
		
	}
	
	private static IndexedItemValueComparator iivc = new IndexedItemValueComparator();
	
	public static Long power(int n){
		if (n==0) return 1l;
		return 2*power(n-1);
	}
	
	//return = n!/(2^tp) mod m
	public static long factorial(int n, int tp, int m){
		long ret = 1;
		int j = 1;
		int t = tp;
		while (j<=n){
			int f = j;
			while ((f&1)==0 && t>0){
				f>>=1;
				t--;
			}
			ret = (ret*f)%m;
			j++;
		}
		return ret;
	}
	
	public static int getConsecutiveN(IndexedItem[] a, int startIdx){
		int c = 1;
		while ((startIdx+c)<a.length && a[startIdx].value==a[startIdx+c].value){
			c++;
		}
		return c;
	}
	
	public static int getPairs(IndexedItem[] a, int starta, int na, IndexedItem[] b, int startb, int nb){
		if (na==0 || nb==0) return 0;
		if (a[starta].idx==b[startb].idx){
			return 1 + getPairs(a, starta+1, na-1, b, startb+1, nb-1);
		}else if (a[starta].idx>b[startb].idx){
			return getPairs(a, starta, na, b, startb+1, nb-1);
		}else{
			return getPairs(a, starta+1, na-1, b, startb, nb);
		}
	}
	
	public static long getNumbers(int n, String aline, String bline, int m){
		IndexedItem[] a = new IndexedItem[n];
		String[] as = aline.split(" ");
		for (int i=0; i<as.length; i++){
			a[i] = new IndexedItem(i, Integer.parseInt(as[i]));
		}
		IndexedItem[] b = new IndexedItem[n];
		String[] bs = bline.split(" ");
		for (int i=0; i<bs.length; i++){
			b[i] = new IndexedItem(i, Integer.parseInt(bs[i]));
		}
		
		Arrays.sort(a, iivc);
		Arrays.sort(b, iivc);
		int mina = a[0].value;
		int idxa = 0;
		int minb = b[0].value;
		int idxb = 0;
		long ret = 1;
		while (idxa<n || idxb<n){
			if (idxa<n){
				mina = a[idxa].value;
			}else{//a is exhausted
				mina = Integer.MAX_VALUE;
			}
			if (idxb<n){
				minb = b[idxb].value;
			}else{//b is exhausted
				minb = Integer.MAX_VALUE;
			}
			int na = 0;
			int nb = 0;
			long f = 1;
			if (mina<minb){
				na = getConsecutiveN(a, idxa);
				f = factorial(na, 0, m);
				idxa += na;
			}else if (mina==minb){
				na = getConsecutiveN(a, idxa); //from idxa to idxa+na-1
				nb = getConsecutiveN(b, idxb); //from idxb to idxb+nb-1
				int no =0;
				no = getPairs(a, idxa, na, b, idxb, nb);
				f = factorial(na+nb, no, m);
				
				/*
				System.out.println("na+nb:" + (na+nb));
				System.out.println("no:" + no);
				System.out.println("f:" + f);
				*/
				idxa += na;
				idxb += nb;
				
			}else{//minb < mina or a is exhausted
				nb = getConsecutiveN(b, idxb);
				idxb += nb;
				f = factorial(nb, 0, m);
			}
			ret = (ret * f)%m;
		}
		return ret;
	}
	
	public static void main(String[] args){
		try{
			BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
			String line=buffer.readLine();
			int n = Integer.parseInt(line);
			String aline = buffer.readLine();
			String bline = buffer.readLine();
			line=buffer.readLine();
			int m = Integer.parseInt(line);
			System.out.println(getNumbers(n, aline, bline, m));
		}catch(Exception e){
			System.out.print(e);
		}
	}

}
