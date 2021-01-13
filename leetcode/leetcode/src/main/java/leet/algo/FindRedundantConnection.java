package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class FindRedundantConnection {
    private static Logger logger =  LogManager.getLogger(FindRedundantConnection.class);

    class Graph{
        Set<Integer> nodes = new HashSet<>();
        Map<Integer, Set<Integer>> edges = new HashMap<>();
        Map<String, Integer> edgeToIdx = new HashMap<>();// small,big -> idx

        private void addNode(Integer n){
            nodes.add(n);
        }

        private String toStr(int from, int to){
            String str;
            if (from<to){
                str = String.format("%d,%d", from, to);
            }else{
                str = String.format("%d,%d", to, from);
            }
            return str;
        }

        private String toStr(List<Integer> edge){
            int from = edge.get(0);
            int to = edge.get(1);
            return toStr(from, to);
        }

        public void addEdge(int from, int to, int idx){
            addNode(from);
            addNode(to);
            if (edges.containsKey(from)){
                edges.get(from).add(to);
            }else{
                Set<Integer> toSet = new HashSet<>();
                toSet.add(to);
                edges.put(from, toSet);
            }
            String str;
            if (from<to){
                str = String.format("%d,%d", from, to);
            }else{
                str = String.format("%d,%d", to, from);
            }
            edgeToIdx.put(str, idx);
        }

        public int getIdx(List<Integer> edge){
            return edgeToIdx.get(toStr(edge));
        }

        //return the circle path
        public List<List<Integer>> getCircle(int start){
            Stack<Integer> stack = new Stack<>();//stack of nodes to visit
            stack.push(start);
            Map<Integer, List<Integer>> pushPath = new HashMap<>();
            boolean finish=false;
            int toN=-1;
            int fromNode = -1;
            int[] parent = new int[nodes.size()+1];
            for (int i=1; i<parent.length; i++){
                parent[i] = -1;
            }
            while(!stack.isEmpty() || !finish){
                fromNode = stack.pop();//visit now
                Set<Integer> toNodes = edges.get(fromNode);
                if (toNodes!=null) {
                    for (int toNode : toNodes) {
                        int father = parent[fromNode];
                        if (father != toNode) {
                            logger.info("stack:" + stack);
                            logger.info("pushPath:" + pushPath);
                            if (pushPath.containsKey(toNode)) {
                                toN = toNode;
                                finish = true;
                                break;
                            } else {//
                                stack.push(toNode);
                                pushPath.put(toNode, Arrays.asList(new Integer[]{fromNode, toNode}));
                                parent[toNode] = fromNode;
                                logger.info(String.format("add path: %s -> %s", fromNode, toNode));
                            }
                        }
                    }
                }
            }
            //create circle from prev->toNode is the last edge
            List<List<Integer>> path = new ArrayList<>();
            path.add(Arrays.asList(new Integer[]{fromNode, toN}));
            int node = fromNode;
            while (node != -1){
                int father = parent[node];
                path.add(Arrays.asList(new Integer[]{father, node}));
                node = father;
            }
            return path;
        }
    }

    public int[] findRedundantConnection(int[][] edges) {
        Graph graph = new Graph();
        for (int i=0; i<edges.length; i++){
            int[] edge = edges[i];
            graph.addEdge(edge[0], edge[1], i);
            graph.addEdge(edge[1], edge[0], i);
        }
        int start = edges[0][0];
        List<List<Integer>> path = graph.getCircle(start);
        TreeMap<Integer, int[]> idxToEdge = new TreeMap<>();
        for (List<Integer> edge: path){
            int idx = graph.getIdx(edge);
            idxToEdge.put(idx, new int[]{edge.get(0), edge.get(1)});
        }
        return idxToEdge.lastEntry().getValue();
    }

    public static void main(String[] args){
        FindRedundantConnection findRedundantConnection = new FindRedundantConnection();
        int[] ret;
        String strRet;
//
//        ret = findRedundantConnection.findRedundantConnection(new int[][]{{1,2},{1,3},{2,3}});
//        strRet = Arrays.toString(ret);
//        logger.info(strRet);
//        assertTrue(strRet.equals("[2, 3]"));

        ret = findRedundantConnection.findRedundantConnection(new int[][]{{1,2}, {2,3}, {3,4}, {1,4}, {1,5}});
        strRet = Arrays.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("[1, 4]"));
    }
}
