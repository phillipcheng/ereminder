package leet.algo;

//Given a 2D binary matrix filled with 0's and 1's, find the largest rectangle containing all ones and return its area.
public class MaxRectangle {
	
	class RectRes{//at point a,b
		int maxArea;
		int[] heights;//heights[i]: have max (i+1)*heights[i] rectangle for [a-i,a]
		
		public RectRes(int maxArea, int[] heights){
			this.maxArea = maxArea;
			this.heights = heights;
		}
	}
	
	public int maximalRectangle(char[][] matrix) {
		int n = matrix.length;
		if (n==0) return 0;
		int m = matrix[0].length;
		if (m==0) return 0;
		RectRes[][] A = new RectRes[n][m];
		for (int i=0; i<n; i++){
			for (int j=0; j<m; j++){
				if (i==0 && j==0){
					if (matrix[0][0]=='1'){
						A[0][0] = new RectRes(1, new int[]{1});
					}else{
						A[0][0] = new RectRes(0, new int[]{});
					}
				}else if (i==0){
					RectRes upRes = A[0][j-1];
					int max = upRes.maxArea;
					int[] heights = new int[]{};
					if (upRes.heights.length==0){
						if (matrix[0][j]=='1'){
							heights = new int[]{1};
							max = Math.max(max, 1);
						}
					}else{
						if (matrix[0][j]=='1'){
							heights = new int[]{upRes.heights[0]+1};
							max = Math.max(max, upRes.heights[0]+1);
						}
					}
					A[0][j] = new RectRes(max, heights);
				}else if (j==0){
					RectRes leftRes = A[i-1][0];
					int max = leftRes.maxArea;
					int[] heights = new int[]{};
					if (matrix[i][0]=='1'){
						heights = new int[leftRes.heights.length+1];
						System.arraycopy(leftRes.heights, 0, heights, 1, leftRes.heights.length);
						heights[0] = 1;
						max = Math.max(max, 1+leftRes.heights.length);
					}
					A[i][0] = new RectRes(max, heights);
				}else{
					RectRes upRes = A[i][j-1];
					RectRes leftRes = A[i-1][j];
					int max = Math.max(upRes.maxArea, leftRes.maxArea);
					int[] heights = new int[]{};
					if (matrix[i][j]=='1'){
						heights = new int[leftRes.heights.length+1];
						for (int k=0; k<heights.length; k++){
							if (k<=upRes.heights.length-1){
								heights[k]=upRes.heights[k]+1;
							}else{
								heights[k]=1;
							}
							max = Math.max(max, (k+1)*heights[k]);
						}
					}
					A[i][j]= new RectRes(max, heights);
				}
			}
		}
		return A[n-1][m-1].maxArea;
    }
}
