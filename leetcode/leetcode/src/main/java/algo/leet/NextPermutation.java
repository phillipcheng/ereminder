package algo.leet;

import java.util.Arrays;

//Another algorithm to get Next-N-Permutation or Previous-N-Permutation
//1,3,2,4 (0,1,0) --> (0,1,1) 1,3,4,2 --> (0,2,0) 1,4,2,3 --> (0,2,1) 1,4,3,2 
// --> (1,0,0) 2,1,3,4 --> (1,0,1) (2,1,4,3)


//rearranges numbers into the lexicographically next greater permutation of numbers
public class NextPermutation {
	//insert a[i] right before a[j]
	public void insertBefore (int[] a, int i, int j){
		int tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
		Arrays.sort(a, j+1, a.length);
	}
	
	public void nextPermutation(int[] num) {
		int rightMostIdx=-1;
		int me=num.length-1;
		for (int i=num.length-1; i>=0; i--){
			//find the left most (1st smaller number right to you), go to that place, and make all the numbers left to you in ascending order
			for (int j=i-1; j>=0; j--){
				if (num[j]<num[i]){
					if (j>rightMostIdx){
						rightMostIdx = j;
						me = i;
					}					
					break;
				}
			}
		}
		if (rightMostIdx == -1){
			Arrays.sort(num);
		}else{
			insertBefore(num, me, rightMostIdx);
		}
    }
}
