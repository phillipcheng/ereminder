package leet.algo;

import java.util.ArrayList;
import java.util.List;

public class SpiralMatrix {
	
	public List<Integer> spiralOrder(int[][] matrix) {
		List<Integer> ret = new ArrayList<Integer>();
		if (matrix.length==0||matrix[0].length==0||matrix==null) return ret;
		int up=0; 
		int down=matrix.length-1;
		int l=0;
		int r = matrix[0].length-1;
		int n = matrix.length;
		int m = matrix[0].length;
		while (up<down && l<r){
			int i=l;
			int j=up;
			for (i=l; i<r; i++){//left --> right
				ret.add(matrix[j][i]);
			}
			for (j=up; j<down; j++){//up --> down
				ret.add(matrix[j][i]);
			}
			
			for (i=r; i>l; i--){//right --> left
				ret.add(matrix[j][i]);
			}
			for (j=down; j>up; j--){//down-->up
				ret.add(matrix[j][i]);
			}
			r--;
			down--;
			l++;
			up++;
		}
		if (ret.size()<n*m){
			if (up==down && l<r){
				for (int i=l; i<=r; i++){
					ret.add(matrix[up][i]);
				}
			}else if (l==r && up<down){
				for (int i=up; i<=down; i++){
					ret.add(matrix[i][l]);
				}
			}else{
				ret.add(matrix[up][l]);
			}
		}
		return ret;
    }
}
