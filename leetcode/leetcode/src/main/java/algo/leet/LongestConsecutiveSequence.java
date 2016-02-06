package algo.leet;


//Given an unsorted array of integers, find the length of the longest consecutive elements sequence.
public class LongestConsecutiveSequence {
	public static boolean isDebug=false;
	
	public int getMin(int[] num){
		int min = Integer.MAX_VALUE;
		for (int i=0; i<num.length; i++){
			if (num[i]<min){
				min = num[i];
			}
		}
		return min;
	}
	
	public int getMax(int[] num){
		int max = Integer.MIN_VALUE;
		for (int i=0; i<num.length; i++){
			if (num[i]>max){
				max = num[i];
			}
		}
		return max;
	}
	
	public int longestConsecutive(int[] num) {
		int hashSize = 1024; //bucket number
		int min = getMin(num);
		int max = getMax(num);
		
		int k = 0;
		int bucketSize = 0;
		
		long longMax = (long)max;
		long longMin = (long)min;
		
		if (longMax-longMin>hashSize){
			k = hashSize;
		}else{
			k = 1;
		}
		
		long lv = longMax-longMin + 1;
		bucketSize =  (int) (lv/k + 1);
		
		
		boolean[][] hashes = new boolean[k][];
		int[][] hashValue = new int[k][];
		if (isDebug){
			System.out.println("bucket size:" + bucketSize);
		}
		
		for (int i=0; i<num.length; i++){
			int hash = (int) (((long)num[i]-longMin)/(long)bucketSize);
			int left = (int) (((long)num[i]-longMin)%(long)bucketSize);
			if (isDebug){
				System.out.println("for num:" + num[i] + ", hash:" + hash + ", left:" + left);
			}
			if (hashes[hash]==null){
				hashes[hash] = new boolean[bucketSize];
				if (isDebug){
					hashValue[hash] = new int[bucketSize];
				}
			}
			hashes[hash][left]=true;
			if (isDebug){
				hashValue[hash][left]=num[i];
			}
		}
		
		if (isDebug){
			for (int i=0; i<k; i++){
				System.out.print("bucket " + i + ":");
				if (hashes[i]!=null){
					for (int j=0; j<hashes[i].length; j++){
						if (hashes[i][j]){
							System.out.print(j + ":" + hashValue[i][j] + ",");
						}
					}
					System.out.println();
				}
			}
		}
		int longRecord=0;
		int longest=0;
		boolean prev=false;
		for (int i=0; i<k; i++){
			if (hashes[i]!=null){
				for (int j=0; j<hashes[i].length; j++){
					if (longest==0 && hashes[i][j]){
						longest=1;
						prev=true;
					}else{
						if (prev && hashes[i][j]){
							longest++;
							prev=true;
						}else if (!hashes[i][j]){
							if (longRecord<longest){
								longRecord = longest;
							}
							longest=0;
							prev=false;
						}
					}
				}
			}
		}
        return longRecord;
    }

}
