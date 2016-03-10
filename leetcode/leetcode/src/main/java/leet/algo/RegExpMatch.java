package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Implement regular expression matching with support for '.' and '*'.
public class RegExpMatch {
	private static Logger logger =  LogManager.getLogger(RegExpMatch.class);
	//merge consecutive * into 1 *
	public String mergeP(String p){
		if (p.length()==0) return p;
		StringBuffer sb = new StringBuffer();
		char ch = p.charAt(0);
		sb.append(ch);
		for (int i=1; i<p.length(); i++){
			char nch = p.charAt(i);
			if (ch==nch && ch=='*'){
			}else{
				sb.append(nch);
				ch = nch;
			}
		}
		return sb.toString();
	}
	public boolean isMatch(char s, char p){
		if (p=='*'){
			return false;
		}else if (p=='.'){
			return true;
		}else if (s==p){
			return true;
		}else{
			return false;
		}
	}
	public boolean isMatch(String s, String p) {
		p = mergeP(p);
		int n=p.length();
		int m = s.length();
		boolean[][] A = new boolean[n][m];
		for (int i=0; i<n; i++){
			for (int j=0; j<m; j++){
				char cp = p.charAt(i);
				char c = s.charAt(j);
				if (i==0 && j==0){
					A[i][j] = isMatch(c, cp);
				}else if (i==0){
					A[i][j]=false;
				}else if (j==0){
					if (p.charAt(i-1)=='*'){
						if (i==1){
							A[i][j]=false;
						}else if (i==2){
							A[i][j]=isMatch(c, cp);
						}else{
							
						}
					}
					if (A[i-1][j] && cp=='*'){
						A[i][j]=true;
					}
				}else{
					if (A[i][j-1] && cp=='*'){
						if (p.charAt(i-1)=='.'){
							A[i][j]=true;
						}else if (s.charAt(j-1)==c){
							A[i][j]=true;
						}
					}else if (A[i-1][j] && cp=='*'){
						A[i][j]=true;
					}else if (A[i-1][j-1] && 
							(cp=='.' || cp==c || (cp=='*' && (p.charAt(i-1)=='.'||c==p.charAt(j-1))))){
						A[i][j]=true;
					}else{
						A[i][j]=false;
					}
				}
			}
		}
		if (n==0 && m==0) return true;
		if (n==0 && m!=0) return false;
		if (n!=0 && m==0) return false;
        return A[n-1][m-1];
    }

}
