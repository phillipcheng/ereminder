package algo.leet;

//Divide two integers without using multiplication, division and mod operator.
public class DevideTwoInteger {
	
	public static boolean isDebug=true;
	
	public int divide(int xdividend, int xdivisor){
		long dividend=xdividend, divisor=xdivisor;
		if (xdivisor<0 && xdividend<0){
			divisor = -1 * (long)xdivisor;
			dividend = -1 * (long)xdividend;
		}else if (xdivisor<0 && xdividend>0){
			divisor = -1 * (long)xdivisor;
		}else if (xdivisor>0 && xdividend<0){
			dividend = -1 * (long)xdividend;
		}
		
		char[] chDivident = (dividend+"").toCharArray();
		int divisorLen = (divisor+"").length();
		int idx=0;
		char[] result = new char[chDivident.length];
		int resultLen = 0;
		long left=0;
		
		while (chDivident.length>=idx+divisorLen){
			long iPartDivident=0;
			if (resultLen==0){
				String partDivident = new String(chDivident, idx, divisorLen);
				iPartDivident = Long.parseLong(partDivident);
			}else{
				iPartDivident = left*10 + chDivident[idx+divisorLen-1]-'0';
			}
			
			if (isDebug){
				System.out.println("iPartDivident:" + iPartDivident);
			}
			
			long sumDivisor=0;
			for (int i=1;i<=10;i++){
				sumDivisor+=divisor;
				if (sumDivisor>iPartDivident){
					result[resultLen]=(char) ('0' + i-1);
					resultLen++;
					left=iPartDivident+divisor-sumDivisor;
					if (isDebug){
						System.out.println("resultLen:" + resultLen);
						System.out.println("result:" + result[resultLen-1]);
						System.out.println("left:" + left);
					}
					break;
				}
			}
			idx++;
		}
		long ret=0;
		if (resultLen==0){
			ret=0;
		}else{
			ret = Long.parseLong(new String(result, 0, resultLen));
		}
		if (xdivisor<0 && xdividend<0 || xdivisor>0 && xdividend>0){
		}else{
			ret = -1*ret;
		}
		return (int)ret;
    }
}
