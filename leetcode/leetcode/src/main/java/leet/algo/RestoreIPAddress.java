package leet.algo;

import java.util.ArrayList;
import java.util.List;

public class RestoreIPAddress {
	
	private boolean isValid(String s){
        if(s.length()>3 || s.length()==0 || (s.charAt(0)=='0' && s.length()>1) || Integer.parseInt(s)>255)
            return false;
        return true;
    }
   
	private List<String> restoreIpPart(String s, int n){//n<=4 && n>=1
		List<String> sl = new ArrayList<String>();
		if (n==1){
			if (isValid(s)){
				sl.add(s);
			}
		}else{
			for(int i=1; i<=3; i++){
				if (s.length()>i){
					String ip = s.substring(0,i);
					if (isValid(ip)){
						List<String> ipp = restoreIpPart(s.substring(i, s.length()), n-1);
						for (String p:ipp){
							sl.add(ip+"."+p);
						}
					}
				}
			}
		}
		return sl;
	}
	
	public List<String> restoreIpAddresses(String s) {
		return restoreIpPart(s, 4);
    }

}
