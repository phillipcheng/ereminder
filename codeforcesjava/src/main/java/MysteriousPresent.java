import java.util.Random;


/**
 * http://codeforces.com/problemset/problem/4/D
 * @author chengyi
 *
 */
public class MysteriousPresent {
	
	public static String genRandomInt(int n){
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		for (int i=0; i<n; i++){
			sb.append(r.nextInt(1000000000));
			sb.append(" ");
		}
		return sb.toString(); 
	}
	
	public static void main(String[] args){
		
	}

}
