package leet.algo;

import algo.tree.TreeNode;


public class SerDeserBinaryTree {
	
	class ResultInput{
		TreeNode result;
		String nextInput;
		
		public ResultInput(TreeNode result, String nextInput){
			this.result = result;
			this.nextInput = nextInput;
		}
	}
	
	//preOrder, inOrder, postOrder are all dfs
    public String preOrderToString(TreeNode tn){
    	if (tn==null) return "";
    	StringBuffer sb = new StringBuffer();
    	sb.append(tn.val);
    	sb.append(",");
    	if (tn.left==null){
    		sb.append("#,");
    	}else{
    		sb.append(preOrderToString(tn.left));
    	}
    	if (tn.right==null){
    		sb.append("#,");
    	}else{
    		sb.append(preOrderToString(tn.right));
    	}
    	return sb.toString();
    }
    private ResultInput preOrderFromStringRI(String s){
    	if (s.length()==0) return new ResultInput(null, null);
    	String str,next=null;
    	int idx = s.indexOf(',');
    	if (idx<0){
    		str = s;
    	}else{
    		str = s.substring(0, idx);
    		next = s.substring(idx+1, s.length());
    	}
    	if (str.equals("#")){
    		return new ResultInput(null, next);
    	}else{
    		int v = Integer.parseInt(str);
    		TreeNode tn = new TreeNode(v);
    		ResultInput left = preOrderFromStringRI(next);
    		ResultInput right = preOrderFromStringRI(left.nextInput);
    		tn.left = left.result;
    		tn.right = right.result;
    		return new ResultInput(tn, right.nextInput);
    	}
    }
    public TreeNode preOrderFromString(String s){
    	ResultInput ri = preOrderFromStringRI(s);
    	return ri.result;
    }
    
	// Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        return preOrderToString(root);
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        return preOrderFromString(data);
    }

}
