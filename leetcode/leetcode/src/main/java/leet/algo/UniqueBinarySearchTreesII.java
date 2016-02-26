package leet.algo;

import java.util.ArrayList;
import java.util.List;

import algo.tree.TreeNode;

public class UniqueBinarySearchTreesII {
	
	public void showMap(ArrayList[][] map){
		int n = map.length;
		for (int i=0; i<n; i++){
			StringBuffer sb = new StringBuffer();
			for (int j=0; j<=i; j++){
				ArrayList l = map[j][i];
				if (l!=null){
					sb.append(l.size()).append(",");
				}else{
					sb.append("null").append(",");
				}
			}
			System.err.println(sb.toString());
		}
	}
	
	public List<TreeNode> generateTrees(int n) {
		if (n==0){
			return new ArrayList<TreeNode>();
		}
		ArrayList[][] map = new ArrayList[n][n];
		for (int i=0;i<n;i++){
			ArrayList t = new ArrayList();
			t.add(new TreeNode(i+1));
			map[i][i]= t;
		}
		for (int i=0;i<n;i++){
			for (int j=i-1; j>=0; j--){
				//build all the trees for (j,i)
				ArrayList tl = new ArrayList();
				for (int k=j;k<=i;k++){
					//System.err.println(String.format("i:%d,j:%d,k:%d", i,j,k));
					//showMap(map);
					ArrayList before = new ArrayList();
					if (k>j){
						before = map[j][k-1];
					}
					ArrayList after = new ArrayList();
					if (k<i){
						after = map[k+1][i];
					}
					if (before.size()>0){
						for(int l=0;l<before.size();l++){
							TreeNode ln = (TreeNode) before.get(l);
							if (after.size()>0){
								for (int m=0;m<after.size();m++){
									TreeNode rn = (TreeNode)after.get(m);
									TreeNode t = new TreeNode(k+1);
									t.left = ln;
									t.right = rn;
									tl.add(t);
								}
							}else{
								TreeNode t = new TreeNode(k+1);
								t.left = ln;
								t.right = null;
								tl.add(t);
							}
						}
					}else{
						for (int m=0;m<after.size();m++){
							TreeNode rn = (TreeNode)after.get(m);
							TreeNode t = new TreeNode(k+1);
							t.left = null;
							t.right = rn;
							tl.add(t);
						}
					}
					map[j][i] = tl;//temp
				}
				map[j][i] = tl;
			}
		}
		return map[0][n-1];
    }
}
