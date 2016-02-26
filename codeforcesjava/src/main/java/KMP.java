
public class KMP{
	
	public static void makeNext(char a[], int[] next){
		int n = a.length;
		next[0]=-1;
		next[1]=0;
		int k = 0; //k prefix, j suffix
		int j = 2;
		while (j<n){
			if (a[k] == a[j-1]){
				k++;
				next[j]=k;
				j++;
			}else if (k>0){
				k = next[k];
			}else{
				next[j]=0;
				j++;
			}
		}
	}
	
	public static int find(String s, String p){
		int n = p.length();
		int[] next = new int[n];
		makeNext(p.toCharArray(), next);
		int m = 0; //index of the beginning of the current match on s
		int i = 0; //index of the current match on p
		while (m+i<s.length()){
			if (s.charAt(m+i)==p.charAt(i)){
				if (i==p.length()-1){
					return m;
				}else
					i++;
			}else{
				if (next[i]==-1){
					m = m + 1;
					i = 0;
				}else{
					m = m + i - next[i];
					i = next[i];
				}
			}
		}
		return -1;
	}
}