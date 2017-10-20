package leet.algo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CanIWin {
	class Node{
		int maxCh;
		int dTotal;
		Set<Integer> left;
		List<Node> children = null;//when has no children using maxCh>=dTotal to check win or loose

		public Node(int maxCh, int dTotal){
			this.maxCh = maxCh;
			this.dTotal = dTotal;
			left = new HashSet<Integer>();
			for (int i=0; i<maxCh; i++){
				left.add(i+1);
			}
			children = new ArrayList<Node>();
		}
		
		public Node(int maxCh, int dTotal, Set<Integer> left){
			this.maxCh = maxCh;
			this.dTotal = dTotal;
			this.left = left;
		}
		
		public boolean isWin(){
			return this.maxCh>=this.dTotal;
		}
		
		@Override
		public boolean equals(Object obj){
			if (obj instanceof Node){
				Node n = (Node)obj;
				if (this.maxCh==n.maxCh && this.dTotal==n.dTotal && 
						this.left.containsAll(n.left) && n.left.containsAll(this.left)){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
		
		public String toString(){
			String str = String.format("%d,%d,left:%s,\n(children:%s)", this.maxCh, this.dTotal, this.left, this.children);
			return str;
		}
	}
	
	public int getMax(Set<Integer> s){
		int max=0;
		for (int i:s){
			if (i>max){
				max=i;
			}
		}
		return max;
	}
	
	public Set<Node> construct(Node n){
		Set<Node> ns = new HashSet<Node>();
		if (n.maxCh>=n.dTotal){
			return null;
		}
		if (n.left.size()==1){
			return null;
		}else{
			for (int e: n.left){
				Set<Integer> s2 = new HashSet<Integer>();
				s2.addAll(n.left);
				s2.remove(e);
				int max = getMax(s2);
				int dt = n.dTotal-e;
				Node nn = new Node(max,dt,s2);
				ns.add(nn);
			}
			n.children = new ArrayList<Node>();
			n.children.addAll(ns);
			return ns;
		}
	}
	
	public boolean getMinMax(Node n){
		if (n.children!=null){
			for (Node nn:n.children){
				if (getMinMax(nn)==false){
					return true;
				}
			}
			return false;
		}else{
			return n.isWin();
		}
	}
	
	public boolean canIWin(int maxChoosableInteger, int desiredTotal) {
		//construct the tree
		Node root = new Node(maxChoosableInteger, desiredTotal);
		Set<Node> nl = construct(root);
		if (nl==null){
			return root.isWin();
		}
		while (nl.size()>0){
			Set<Node> nnl = new HashSet<Node>();
			for (Node n: nl){
				Set<Node> ns = construct(n);
				if (ns!=null){
					nnl.addAll(ns);
				}
			}
			nl = nnl;
		}
		//System.out.println(root);
		//calculate the minmax
		return getMinMax(root);
    }
	
	public static void main(String[] args){
		CanIWin ciw = new CanIWin();
		boolean b = ciw.canIWin(10,40);//8,11 true, 2,3 false,
		System.out.println(b);
	}
}
