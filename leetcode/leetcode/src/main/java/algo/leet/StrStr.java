package algo.leet;

//Returns a pointer to the first occurrence of needle in haystack,
//or null if needle is not part of haystack.
public class StrStr {
	
	public String strStrNM(String h, String n){
		char[] ch = h.toCharArray();
		char[] cn = n.toCharArray();
		
		for (int i=0; i<=ch.length-cn.length; i++){
			int j=0;
			for (j=0; j<cn.length; j++){
				//compare ch[i]..ch[i+n.length-1] against ch[0]...cn[0+n.length-1]
				if (ch[i+j]!=cn[j]){
					break;
				}
			}
			if (j==cn.length){
				return new String(ch, i, ch.length-i);
			}
		}
		return null;
	}
	
	public String strStr(String haystack, String needle) {
		if (needle.length()>haystack.length())
			return null;
		return strStrNM(haystack, needle);
    }
}
