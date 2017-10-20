import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Q822A {
	public static void main(String[] args){
		try{
			BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
			String line=buffer.readLine();
			String[] inputs = line.split("\\s");
			int a= Integer.parseInt(inputs[0]);
			int b= Integer.parseInt(inputs[1]);
			int ret = a;
			if (a<=b){
				ret = a;
			}else{
				ret = b;
			}
			int prd=1;
			for (int i=1; i<=ret; i++){
				prd=prd*i;
			}
			System.out.println(prd);
		}catch(Exception e){
			System.out.print(e);
		}
	}
}
