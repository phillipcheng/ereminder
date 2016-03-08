package leet.algo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.BoardUtil;

public class WildcardMatching {
	private static Logger logger =  LogManager.getLogger(WildcardMatching.class);
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
	
	public boolean isMatch(String s, String p) {
		p = mergeP(p);
		int n=p.length();
		int m = s.length();
		boolean[][] A = new boolean[n][m+1];
		for (int i=0; i<n; i++){
			for (int j=0; j<=m; j++){
				char cp = p.charAt(i);
				
				if (j==0){//match empty string
					if (i==0){
						if (cp=='*'){
							A[i][j]=true;
						}else{
							A[i][j]=false;
						}
					}else{
						A[i][j]=false;
					}
				}else if (i==0){
					char c = s.charAt(j-1);
					if (cp=='*'){
						A[i][j]=true;
					}else if (cp=='?'){
						if (j==0){
							A[i][j]=false;
						}else if (j==1){
							A[i][j]=true;
						}else{
							A[i][j]=false;
						}
					}else{
						if (j==0){
							A[i][j]=false;
						}else if (j==1){
							if (c==cp){
								A[i][j]=true;
							}else{
								A[i][j]=false;
							}
						}else{
							A[i][j]=false;
						}
					}
				}else{
					char c = s.charAt(j-1);
					if (cp=='*'){
						boolean v = false;
						for (int k=0; k<=j; k++){
							if (A[i-1][k]==true){
								v= true;
								break;
							}
						}
						A[i][j]=v;
					}else if (cp=='?'){
						A[i][j]=A[i-1][j-1];
					}else if (cp==c){
						A[i][j]=A[i-1][j-1];
					}else{
						A[i][j]=false;
					}
				}
				//logger.info(BoardUtil.getBoardString(A));
			}
		}
		if (n==0 && m==0) return true;
		if (n==0 && m!=0) return false;
        return A[n-1][m];
    }

}
