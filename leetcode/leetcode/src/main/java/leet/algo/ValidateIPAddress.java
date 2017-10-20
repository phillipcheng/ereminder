package leet.algo;

public class ValidateIPAddress {
	public String validIPAddress(String IP) {
        if (IP.contains(".")){
            String[] ipv4 = IP.split("\\.", -1);
            if (ipv4.length==4){
                for (String s:ipv4){
                    if (!"0".equals(s) && s.startsWith("0")){
                        return "Neither";
                    }else{
                        if (s.length()>0){
                            char c = s.charAt(0);
                            if (c-'0'>=0 && c-'0'<=9){
                                try{
                                    int v = Integer.parseInt(s);
                                    if (v>255 || v<0){
                                        return "Neither";
                                    }
                                }catch(Exception e){
                                    return "Neither";
                                }
                            }else{
                                return "Neither";
                            }
                        }else{
                            return "Neither";
                        }
                    }
                }
                return "IPv4";
            }else{
                return "Neither";
            }
        }else if (IP.contains(":")){
            String[] ipv6 = IP.split("\\:", -1);
            if (ipv6.length==8){
                for (String s: ipv6){
                    if (s.length()<=4 && s.length()>0){
                        for (int i=0; i<s.length(); i++){
                            char c = s.charAt(i);
                            if (c-'0'>=0 && c-'0'<=9 || c-'A'>=0 && c-'A'<=5 || c-'a'>=0 && c-'a'<=5){
                                
                            }else{
                                return "Neither";
                            }
                        }
                    }else{
                        return "Neither";
                    }
                }
                return "IPv6";
            }else{
                return "Neither";
            }
        }else{
            return "Neither";
        }
    }
}
