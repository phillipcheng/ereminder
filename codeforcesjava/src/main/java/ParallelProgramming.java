import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;


public class ParallelProgramming {
	//a^b
	public static int power(int a, int b){
		int ret = 1;
		for (int i=0; i<b; i++){
			ret = ret * a;
		}
		return ret;
	}
	public static String toStr(int[][] steps){
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<steps.length; i++){
			for (int j=0; j<steps[i].length; j++){
				sb.append(steps[i][j]).append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static int[][] getSteps(int n, int k){
		
		int[][] steps = new int[k][n];
		int a = 0;
		if (n==1){
			a=0;
		}else{
			a = (int)(Math.floor(Math.log(n-1)/Math.log(2)));
		}
		//a fast
		for (int i=0; i<a;i++){
			for (int j=0; j<n-1-power(2, i) ; j++)
				steps[i][j] = power(2, i)+1+j;
			for (int j=n-1-power(2, i); j<n; j++){
				steps[i][j] = n;
			}
		}
		if (n>1){
			//1
			for (int j=0; j<n-1-power(2, a);j++){
				steps[a][j] = power(2, a)+1+j;
			}
			for (int j=n-1-power(2, a);j<n; j++){
				steps[a][j] = n;
			}
		}
		//k-a-1
		if (a>0){
			for (int i=a+1; i<k; i++){
				for (int j=0; j<n; j++){
					steps[i][j]=n;
				}
			}
		}else{
			for (int i=0; i<k; i++){
				for (int j=0; j<n; j++){
					steps[i][j]=n;
				}
			}
		}
		return steps;
	}
	
	public static void main(String[] args){
		try{
			BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
			String line=buffer.readLine();
			String[] in = line.split(" ");
			int[][] steps = getSteps(Integer.parseInt(in[0]), Integer.parseInt(in[1]));
			System.out.printf("%s", toStr(steps));
		}catch(Exception e){
			System.out.print(e);
		}
	}

}
