package algo.tree;


public class BSTTree {
	
	public class BSTNode{
		int value;
		int count;
		BSTNode left;
		BSTNode right;

		public BSTNode(int a){
			value =a;
			count =1;
			left=null;
			right=null;
		}
		
		public int getValue(){
			return value;
		}
		
		public String toString(){
			StringBuffer sb = new StringBuffer();
			if (left!=null){
				sb.append(left.toString());
				sb.append(",");
			}
			sb.append(String.format("%d:%d", value, count));
			if (right!=null){
				sb.append(",");
				sb.append(right.toString());
			}
			return sb.toString();
		}
	}
	
	private BSTNode root;
	private int count;
	
	public void add(int a){
		if (root == null){
			root = new BSTNode(a);
			count++;
		}else{
			BSTNode n = root;
			while (true){
				if (n.value == a){
					n.count++;
					count++;
					return;
				}else if (a < n.value){
					if (n.left!=null)
						n = n.left;
					else{
						n.left = new BSTNode(a);
						count++;
						return;
					}
				}else{
					if (n.right!=null)
						n = n.right;
					else{
						n.right = new BSTNode(a);
						count++;
						return;
					}
				}
			}
		}
	}
	
	public BSTNode findMin(){
		BSTNode n = root;
		while (n.left!=null){
			n= n.left;
		}
		return n;
	}
	
	public BSTNode findMax(){
		BSTNode n = root;
		while (n.right!=null){
			n = n.right;
		}
		return n;
		
	}
	
	private BSTNode fixupSuccessor(BSTNode node){
		BSTNode n = node.right;
		BSTNode p = node;
		while (n.left!=null){
			p = n;
			n= n.left;
		}
		if (p!=node){
			p.left = n.right;
		}
		return n;
	}
	
	//return the parent
	public void delete(int v){
		BSTNode n = root;
		BSTNode p = null; //parent
		boolean isLeft=true;
		while (n!=null){
			if (n.value == v){
				if (n.count>1){
					n.count--;
				}else{//delete n, with p's help
					if (n.left==null && n.right==null){//1.n is the leaf
						if (p==null){
							root = null;
						}else{
							if (isLeft){
								p.left = null;
							}else{
								p.right = null;
							}
						}
					}else if (n.left==null){//2. n has 1 child
						if (p==null){
							root = n.right;
						}else{
							if (isLeft){
								p.left = n.right;
							}else{
								p.right = n.right;
							}
						}
					}else if (n.right==null){//3. 
						if (p==null){
							root = n.left;
						}else{
							if (isLeft){
								p.left = n.left;
							}else{
								p.right = n.left;
							}
						}
					}else{//4.
						BSTNode nm = fixupSuccessor(n);
						nm.left = n.left;
						if (p==null){
							root = nm;
						}else{
							if (isLeft){
								p.left = nm;
							}else{
								p.right = nm;
							}
						}
					}
				}
				count--;
				return;
			}else if (v<n.value){
				p = n;
				n = n.left;
				isLeft = true;
			}else{
				p = n;
				n = n.right;
				isLeft = false;
			}
		}
	}
	
	public int size(){
		return count;
	}
	
	public String inOrderToString(){
		if (root!=null){
			return root.toString();
		}else{
			return "";
		}
	}
}
