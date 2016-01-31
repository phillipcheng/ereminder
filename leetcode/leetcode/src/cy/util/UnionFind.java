package cy.util;

import java.util.Arrays;

public class UnionFind {
	int[] id; //
	int[] count; //
	
	public String toString(){
		String s = "id:" + Arrays.toString(id);
		s+="\n" + "count:" + Arrays.toString(count);
		return s;
	}
	
	public UnionFind(int n){
		id = new int[n];
		count = new int[n];
		for (int i=0; i<n; i++){
			id[i] = i;
			count[i]=1;
		}
	}
	
	public int root(int idx){
		int inIdx=idx;
		while (id[idx]!=idx){
			idx=id[idx];
		}
		int rootIdx=idx;
		//for even better performance
//		idx=inIdx;
//		while (id[idx]!=rootIdx){
//			int preIdx = idx;
//			idx=id[idx];
//			id[preIdx]=rootIdx;
//		}
		
		return rootIdx;
	}
	
	public void union(int i, int j){
		//moving smaller tree to be sub-tree of the other
		int ri = root(i);
		int rj = root(j);
		if (ri!= rj){
			if (count[ri]>count[rj]){
				id[rj]=ri;
				count[ri]+=count[rj];
			}else{
				id[ri]=rj;
				count[rj]+=count[ri];
			}
		}
	}
	
	public boolean isConnected(int i, int j){
		return (root(i)==root(j));
	}
}
