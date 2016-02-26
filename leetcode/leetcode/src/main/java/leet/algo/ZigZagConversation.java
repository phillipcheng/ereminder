package leet.algo;
/**
 * I     G
 * A   E Y
 * M H   I
 * C
 * 
 * 
 * IAMCHENGYI
 * @author cheyi
 *
 */
public class ZigZagConversation {
	
	public static boolean isDebug = false;
	
	public String convert(String s, int nRows) {
		if (nRows==1){
			return s;
		}
		
		char[] chars = s.toCharArray();
		char[] output = new char[chars.length];
		int len = chars.length;
		int radix = 2*nRows-2;
		int k = len / radix;
		int l = len % radix;
		
		
		for (int i=0; i<k; i++){
			if (isDebug){
				System.out.println("output:" + i + " is assigned with input:" + (radix*i));
			}
			output[i]=chars[radix*i];
		}
		int addition=0;
		if (l>=1){
			if (isDebug){
				System.out.println("output:" + k + " is assigned with input:" + (radix*k+1-1));
			}
			output[k]=chars[radix*k+1-1];
			addition++;
		}
		int start = k + addition;//start of the line
		for (int j=1; j<nRows-1; j++){
			int lineAddition=0;
			for (int i=0;i<k;i++){
				if (isDebug){
					System.out.println("output:" + (start + 2*i) + " is assigned with input:" + (radix*i+j));
					System.out.println("output:" + (start + 2*i+1) + " is assigned with input:" + (radix*i+nRows+nRows-1-j-1));
				}
				output[start + 2*i]=chars[radix*i+j];
				output[start + 2*i+1]=chars[radix*i+nRows+nRows-1-j-1];
			}
			if (l>1 && j<=l-1){
				if (isDebug){
					System.out.println("output:" + (start + 2*k) + " is assigned with input:" + (radix*k+j));
				}
				output[start + 2*k]=chars[radix*k+j];
				lineAddition++;
			}
			if (l>nRows && j>=(2*nRows-l-1)){
				if (isDebug){
					System.out.println("output:" + (start + 2*k + 1) + " is assigned with input:" + (radix*k+nRows+nRows-1-j-1));
				}
				output[start + 2*k + 1] = chars[radix*k+nRows+nRows-1-j-1];
				lineAddition++;
			}
			start+= (2*k + lineAddition);
		}
		
		for (int i=0;i<k;i++){
			if (isDebug){
				System.out.println("output:" + (start +i) + " is assigned with input:" + (i*radix+nRows-1));
			}
			output[start +i]=chars[i*radix+nRows-1];
		}
		if (l>=nRows){
			if (isDebug){
				System.out.println("output:" + (start+k) + " is assigned with input:" + (k*radix+nRows-1));
			}
			output[start+k] = chars[k*radix+nRows-1];
		}
		
		
		
        return new String(output);
    }
}
