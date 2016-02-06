package algo.leet;

import algo.util.DirectedGraph;

//Given a string s, partition s such that every substring of the partition is a palindrome.

//Return the minimum cuts needed for a palindrome partitioning of s.
public class PalindromePartitionII {
	
	public static boolean isDebug=false;
	
	private byte[][] IsPalinMap;
	
	private static final byte unfilled = 0;
	private static final byte fill_false = 1;
	private static final byte fill_true = 2;
	
	
	public boolean isPalindrome(char[] chars, int from, int to, boolean useCache){
		String ft = new String(chars, from, to-from+1);
		
		if (IsPalinMap[from][to]!=unfilled){
			return IsPalinMap[from][to]==fill_true;
		}
		
		if (chars[from]!=chars[to]){
			IsPalinMap[from][to]= fill_false;
			return false;
		}else{
			if (IsPalinMap[from+1][to-1]!=unfilled){
				System.out.println("hit");
				if (IsPalinMap[from+1][to-1]==fill_false){
					IsPalinMap[from][to]=fill_false;
					return false;
				}else{
					IsPalinMap[from][to]=fill_true;
					return true;
				}
			}else{
				for (int i=0; i<(to-from+1)/2; i++){
					if (chars[from+i]!=chars[to-i]){
						if (isDebug){
							System.out.println("not palindrome:" + new String(chars, from, to-from+1));
						}
						IsPalinMap[from][to]=fill_false;
						return false;
					}
				}
				if (isDebug){
					System.out.println("is palindrome:" + new String(chars, from, to-from+1));
				}
				IsPalinMap[from][to]=fill_true;
				return true;
			}
		}	
	}
	
	
	public boolean isPalindrome(char[] chars, int from, int to){
		for (int i=0; i<(to-from+1)/2; i++){
			if (chars[from+i]!=chars[to-i]){
				return false;
			}
		}
		return true;
	}
	
	
	public void judgePalindrom(String s, boolean useCache){
		if (useCache)
			IsPalinMap = new byte[s.length()][s.length()];
		
		long start = System.nanoTime();
		char[] chars = s.toCharArray();
		int n = chars.length;
		for (int i=0; i<n; i++){
			for (int j=i+1; j<n; j++){
				//always connect i,i+1
				if (!useCache)
					isPalindrome(chars, i, j);
				else
					isPalindrome(chars, i, j, true);
			}
		}
		long end = System.nanoTime();
		System.out.println("time:" + (end-start));
	}
	
	
	public int minCut(String s) {
		char[] chars = s.toCharArray();
		DirectedGraph<Integer> g = new DirectedGraph<Integer>();
		int n = chars.length;

		IsPalinMap = new byte[n][n];
		
		for (int i=0; i<n; i++){
			g.addNode(i);
		}
		g.setSource(0);
		for (int i=0; i<n; i++){
			for (int j=i+1; j<n; j++){
				//always connect i,i+1
				if (isPalindrome(chars, i, j)){
					if (j!=n-1){
						g.addEdge(i, j+1);						
					}else{
						g.addEdge(i, j);
					}
				}
			}
		}
		
		g.bfs(0);
		
		if (g.distTo.containsKey(n-1)){
			return g.distTo.get(n-1);
		}else{
			return n-1;
		}
    }

}
