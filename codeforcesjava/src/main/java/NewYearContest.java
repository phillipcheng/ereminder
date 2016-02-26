import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;


public class NewYearContest {
	
	public static String getMin(int n, String input){
		int in[] = new int[n];
		StringTokenizer st = new StringTokenizer(input);
		int i=0;
		while (st.hasMoreTokens()){
			in[i] = Integer.parseInt(st.nextToken());
			i++;
		}
		Arrays.sort(in);
		int sum = 0;
		int penalty=0;
		i = 0;
		while (sum<=710 && i<n){
			sum += in[i];
			if (sum>350 && sum <=710){
				int del = sum - 350;
				penalty += del;
			}
			i++;
		}
		int t=0;
		if (sum<=710){
			t = n;
		}else{
			t = i-1;
		}
		return t + " " + penalty;
	}
	
	public static void main(String args[]){
		try{
			BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
			String line=buffer.readLine();
			int n = Integer.parseInt(line);
			String str = buffer.readLine();
			System.out.printf("%s", getMin(n, str));
		}catch(Exception e){
			System.out.print(e);
		}
	}

}
