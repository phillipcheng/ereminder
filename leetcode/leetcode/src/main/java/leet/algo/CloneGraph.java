package leet.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;



public class CloneGraph {
	//Clone an undirected graph
	public class UndirectedGraphNode {
		int label;
		List<UndirectedGraphNode> neighbors;
		UndirectedGraphNode(int x) { label = x; neighbors = new ArrayList<UndirectedGraphNode>(); }
	}
	
	public UndirectedGraphNode cloneGraph(UndirectedGraphNode node) {
		if (node == null) return null;
        Map<Integer, UndirectedGraphNode> newNodes = new HashMap<Integer, UndirectedGraphNode>();
        Stack<UndirectedGraphNode> stack = new Stack<UndirectedGraphNode>();
        stack.push(node);
        while (!stack.isEmpty()){
        	UndirectedGraphNode n = stack.pop();
        	if (!newNodes.containsKey(n.label)){
	        	UndirectedGraphNode nn = new UndirectedGraphNode(n.label);
	        	newNodes.put(n.label, nn);
	        	for (UndirectedGraphNode neigh: n.neighbors){
	            	stack.push(neigh);
	            }
        	}
        }
        int i = 0;
        UndirectedGraphNode ret = null;
        Set<Integer> visited = new HashSet<Integer>();
        stack.clear();
        stack.push(node);
        while (!stack.isEmpty()){
        	UndirectedGraphNode n = stack.pop();
        	if (!visited.contains(n.label)){
        		UndirectedGraphNode nn = newNodes.get(n.label);
        		if (i==0) ret = nn;
            	for (UndirectedGraphNode neigh: n.neighbors){
	        		nn.neighbors.add(newNodes.get(neigh.label));
	            	stack.push(neigh);
	            }
	        	visited.add(n.label);
        	}
        	i++;
        }
        return ret;
    }

}
