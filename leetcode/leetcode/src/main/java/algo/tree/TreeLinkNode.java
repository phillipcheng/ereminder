package algo.tree;

public class TreeLinkNode {
	public int val;
	public TreeLinkNode left, right, next;
	public TreeLinkNode(int x) { val = x; }
	public String toString(){
		return String.format("%d", val);
	}
}
