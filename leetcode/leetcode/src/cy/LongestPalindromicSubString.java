package cy;

public class LongestPalindromicSubString {
	int maxLen = 0;
	int startIdx = 0;
	char[] chars = null;
	
	public static boolean isDebug = false;
	
	public void setMax(int len, int idx){
		if (isDebug){
			System.out.println("setMax: len:" + len + ", idx:" + idx);
			System.out.println("setMax:" + new String(chars, idx, len));
		}
		
		if (len>maxLen){
			maxLen = len;
			startIdx = idx;
		}
	}
	
	public int min(int a, int b){
		if (a<b) 
			return a;
		else
			return b;
	}
	
	public String longestPalindrome(String s) {
		if (s.length()==1){
			return s;
		}
		
		chars = s.toCharArray();
		for (int i=0; i<chars.length-1; i++){
			//grow the palindromic using seed chars[i]
			int rightLen = i;
			int leftLen = chars.length-i-1;
			int min = min(rightLen, leftLen);
			int j=1;
			int palinLen=0;
			int palinStartIdx=0;
			
			for (j=1; j<=min; j++){
				if (chars[i-j]!=chars[i+j]){
					break;
				}
			}
			palinLen = 2*(j-1)+1;
			palinStartIdx = i-(j-1);
			setMax(palinLen, palinStartIdx);
			
			//grow the palindromic using seed chars[i], chars[i+1]
			if (chars[i]==chars[i+1]){
				rightLen = i;
				leftLen = chars.length -i -2;
				min = min (rightLen, leftLen);
				for (j=1; j<=min; j++){
					if (chars[i-j]!=chars[i+1+j]){
						break;
					}
				}
				palinLen = 2*(j-1)+2;
				palinStartIdx = i - (j-1);
				setMax(palinLen, palinStartIdx);
			}
		}
        return new String(chars, startIdx, maxLen);
    }
}
