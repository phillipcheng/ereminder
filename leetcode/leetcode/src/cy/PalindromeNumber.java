package cy;

public class PalindromeNumber {
	public int getNumDigits(int a){
		int d = 1;
		while (a/10!=0){
			d++;
			a=a/10;
		}
		return d;
	}
	
	public int getDigit(int x, int idx){
		int a=x;
		int nextA;
		int r=0;
		for (int i=0; i<idx; i++){
			nextA = a/10;
			r = a%10;
			a=nextA;
		}
		return r;
	}
	
	public boolean isPalindrome(int x) {
		if (x<0){
			return false;
		}
        int nd = getNumDigits(x);
        int i=1;
        int j=nd;
        while (i<j){
        	int a = getDigit(x,i);
        	int b = getDigit(x,j);
        	if (a!=b){
        		return false;
        	}
        	i++;
        	j--;
        }
        return true;
    }
	
	
}
