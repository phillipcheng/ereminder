import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class TavasAndMalekas {
	// return (b^a) % m
	public static long power(int a, int b, int m){
		long v = 1;
		for (int i=0; i<a; i++){
			v = v*b%m;
		}
		return v;
	}
	
	public static Map<String, Integer> smap = new HashMap<String, Integer>();
	public static boolean[] parray = null;
	public static void buildSuffixMapPrefixArray(String p){
		for (int i=0; i<p.length(); i++){
			String suffix = p.substring(i);
			smap.put(suffix, i);
		}
		parray = new boolean[p.length()];
		for (int i=0; i<p.length(); i++){
			String prefix = p.substring(0, i+1);
			parray[i] = smap.containsKey(prefix);
		}
	}
	
	public static long getNumber(int n, int m, String p, String pIdxS){
		String[] pIdx = pIdxS.split(" ");
		int[] pid = new int[m];
		for (int i=0; i<pIdx.length-1; i++){
			pid[i] = Integer.parseInt(pIdx[i]);
		}
		int open = 0;
		
		return power(open, 26, 1000000007);
	}
	
	public static void main(String[] args){
		try{
			BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
			String line=buffer.readLine();
			String nm[] = line.split(" ");
			int n = Integer.parseInt(nm[0]);
			int m = Integer.parseInt(nm[1]);
			String p = buffer.readLine();
			String pIdxS = buffer.readLine();
			System.out.println(getNumber(n, m, p, pIdxS));
		}catch(Exception e){
			System.out.print(e);
		}
	}
}
