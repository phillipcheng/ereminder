import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class RestoringTable {

	public static void print(int[][] res){
		for (int i=0; i<res.length; i++){
			for (int j=0; j<res[i].length; j++){
				System.out.print(res[i][j]);
			}
			System.out.print("\n");
		}
	}
	public static int power(int b){
		int ret = 1;
		for (int i=0; i<b; i++){
			ret = ret * 2;
		}
		return ret;
	}
	
	public static int toStr(int[] in){
		int l = in.length;
		int v = 0;
		for (int i=0; i<l; i++){
			v = v + in[l-1-i]*power(i);
		}
		return v;
	}
	
	public static String[] getOrigins(String[] input){
		int n = input.length;
		int[][][] ins = new int[n][n][30];
		for (int i=0; i<n; i++){
			String is[] = input[i].split(" ");
			for (int j=0; j<n ; j++){
				if (i!=j){
					int val = Integer.parseInt(is[j]);
					String bin = String.format("%30s", Integer.toBinaryString(val)).replace(' ', '0');
					for (int k=0; k<30; k++){
						ins[i][j][k] = bin.toCharArray()[k]=='0'? 0 : 1;
					}
				}
			}
		}
		int[][] res = new int[n][30];
		for (int i=0; i<n; i++){
			for (int j=0; j<30; j++){
				res[i][j] = -1;
			}
		}
		//System.out.println("after set init -11");
		//print(res);
		for (int i=0; i<n; i++){
			for (int j=0; j<n; j++){
				if (i!=j){
					for (int k=0; k<30; k++){
						if (ins[i][j][k]==1){
							res[i][k]=1;
							res[j][k]=1;
						}
					}
				}
			}
		}
		//System.out.println("after set 1");
		//print(res);
		for (int i=0; i<n; i++){
			for (int j=0; j<n; j++){
				if (i!=j){
					for (int k=0; k<30; k++){
						if (ins[i][j][k]==0){
							if (res[i][k]==1){
								res[j][k]=0;
							}else if (res[j][k]==1){
								res[i][k]=0;
							}
						}
					}
				}
			}
		}

		//System.out.println("after set 0");
		//print(res);
		for (int i=0; i<n; i++){
			for (int k=0; k<30; k++){
				if (res[i][k]==-1){
					//set the undefined to 0, since all their ands are 0
					res[i][k]=0;
				}
			}
		}

		//System.out.println("after set uncertain to 0");
		//print(res);
		String[] output = new String[n];
		for (int i=0; i<n; i++){
			output[i] = toStr(res[i]) + "";
		}
		return output;
	}
	
	public static void main(String[] args){
		try{
			BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
			String line=buffer.readLine();
			int n = Integer.parseInt(line);
			String[] input = new String[n];
			for (int i=0; i<n; i++){
				input[i]=buffer.readLine();
			}
			System.out.printf("%s", String.join(" ", getOrigins(input)));
		}catch(Exception e){
			System.out.print(e);
		}
	}
}
