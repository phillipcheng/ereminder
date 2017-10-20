package leet.algo;

public class CompareVNumber {
	public int compareVersion(String version1, String version2) {
        String[] av1 = version1.split("\\.");
        String[] av2 = version2.split("\\.");
        String[] sv1 = av1;
        String[] sv2 = av2;
        int swap = 1;
        if (av1.length<av2.length){
            sv1 = av2;
            sv2 = av1;
            swap = -1;
        }
        int part = sv1.length;
        for (int i=0; i<part; i++){
            String p1 = sv1[i];
            String p2= "0";
            if (i<sv2.length){
                p2 = sv2[i];
            }
            int v1 = Integer.parseInt(p1);
            int v2 = Integer.parseInt(p2);
            if (v1>v2){
                return 1*swap;
            }else if (v1<v2){
                return -1*swap;
            }
        }
        return 0;
    }
	
	public static void main(String[] args){
		CompareVNumber cvn = new CompareVNumber();
		cvn.compareVersion("1", "0");
	}
}
