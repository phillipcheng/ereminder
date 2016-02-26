package leet.algo;


public class UniqueBinarySearchTrees {
	
	public void showMap(int[][] map){
		int n = map.length;
		for (int i=0; i<n; i++){
			StringBuffer sb = new StringBuffer();
			for (int j=0; j<=i; j++){
				sb.append(map[j][i]).append(",");
			}
			System.err.println(sb.toString());
		}
	}
	
	public int numTrees(int n) {
		if (n==0){
			return 0;
		}
		int[][] map = new int[n][n];
		for (int i=0;i<n;i++){
			map[i][i]= 1;
		}
		for (int i=0;i<n;i++){
			for (int j=i-1; j>=0; j--){
				for (int k=j;k<=i;k++){
					//System.err.println(String.format("i:%d,j:%d,k:%d", i,j,k));
					//showMap(map);
					int before=1;
					if (k>j){
						before = map[j][k-1];
					}
					int after =1;
					if (k<i){
						after = map[k+1][i];
					}
					map[j][i]+=before*after;
				}
			}
		}
		return map[0][n-1];
    }
}
