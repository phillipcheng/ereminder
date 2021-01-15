package leet.algo;

public class RemoveKDigit {

	public String removeKdigits(String num, int k) {
        String str = num;
        for (int i=0; i<k; i++){
        	System.out.println(str);
        	int j=0;
        	int n1=0, n2=0;
        	if (j<str.length()-1){
        		n1=str.charAt(j)-'0';
        		n2=str.charAt(j+1)-'0';
        	}else{
        		if (!"".equals(str)){
        			str = str.substring(0, str.length()-1);
        			continue;
        		}else{
        			return "0";
        		}
        	}
            while(n1<=n2){
                j++;
                if (j<str.length()-1){
                	n1=str.charAt(j)-'0';
                	n2=str.charAt(j+1)-'0';
                }else{//remove j
                	break;
                }
            }//n2>n1
            if (n2>n1){//
            	j--;
            	str = str.substring(0,j+1) + str.substring(j+2);
            }else {//n1>=n2
                //reached the end, remove n1
            	str = str.substring(0,j) + str.substring(j+1);
            }
            if (str.charAt(0)=='0'){
            	str = str.substring(1);
            }
        }
        if ("".equals(str)){
            return "0";
        }else{
            return str;
        }
    }
	
	public static void main(String[] args){
		RemoveKDigit rkd = new RemoveKDigit();
		String ret;
		ret = rkd.removeKdigits("110", 1);System.out.println(ret);
		ret = rkd.removeKdigits("112", 1);System.out.println(ret);
		ret = rkd.removeKdigits("1111111", 3);System.out.println(ret);
		ret = rkd.removeKdigits("1432219", 3);System.out.println(ret);
		ret = rkd.removeKdigits("10", 2);System.out.println(ret);
		ret = rkd.removeKdigits("178", 1);System.out.println(ret);
		ret = rkd.removeKdigits("10200", 1);System.out.println(ret);
	}
}
