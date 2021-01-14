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

        /**
         *
         * @param node, start
         * @param ancestor
         * @return null if no circle, return all edges belongs to the circle
         */
        List<List<Integer>> dfs(int node, Stack<Integer> ancestor){
            logger.info("node:"+node + ", stack:"+ ancestor);
            Set<Integer> toNodes = edges.get(node);
            if (toNodes != null) {
                for (Integer toNode : toNodes) {
                    if (ancestor.size()==0 || ancestor.peek() != toNode) {
                        List<List<Integer>> ret;
                        List<Integer> edge = new ArrayList<>();
                        edge.add(node);
                        edge.add(toNode);
                        if (ancestor.contains(toNode)) {//circle detected
                            ret = new ArrayList<>();
                            ret.add(edge);
                            return ret;
                        } else {
                            Stack<Integer> newAncestor = new Stack<>();
                            newAncestor.addAll(ancestor);
                            newAncestor.add(node);
                            ret = dfs(toNode, newAncestor);
                            if (ret != null) {//has circle
                                ret.add(edge);
                                return ret;
                            }
                        }
                    }
                }
            }
            return null;
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
        List<List<Integer>> path = graph.dfs(start, new Stack<>());
        logger.info("path:" + path);
        TreeMap<Integer, int[]> idxToEdge = new TreeMap<>();
        for (List<Integer> edge: path){
            int idx = graph.getIdx(edge);
            idxToEdge.put(idx, new int[]{edge.get(0), edge.get(1)});
        }
        int[] biggest = idxToEdge.lastEntry().getValue();
        int tmp;
        if (biggest[0]>biggest[1]){
            tmp = biggest[0];
            biggest[0] = biggest[1];
            biggest[1] = tmp;
        }
        return biggest;
    }

    public static void main(String[] args){
        FindRedundantConnection findRedundantConnection = new FindRedundantConnection();
        int[] ret;
        String strRet;

//        ret = findRedundantConnection.findRedundantConnection(new int[][]{{1,2},{1,3},{2,3}});
//        strRet = Arrays.toString(ret);
//        logger.info(strRet);
//        assertTrue(strRet.equals("[2, 3]"));
//
//        ret = findRedundantConnection.findRedundantConnection(new int[][]{{1,2}, {2,3}, {3,4}, {1,4}, {1,5}});
//        strRet = Arrays.toString(ret);
//        logger.info(strRet);
//        assertTrue(strRet.equals("[1, 4]"));
//
//        ret = findRedundantConnection.findRedundantConnection(new int[][]{{1,3},{3,4},{1,5},{3,5},{2,3}});
//        strRet = Arrays.toString(ret);
//        logger.info(strRet);
//        assertTrue(strRet.equals("[3, 5]"));

        ret = findRedundantConnection.findRedundantConnection(new int[][]{{30,44},{34,47},{22,32},{35,44},
                {26,36},{2,15},{38,41},{28,35},{24,37},{14,49},{44,45},{11,50},{20,39},{7,39},{19,22},{3,17},
                {15,25},{1,39},{26,40},{5,14},{6,23},{5,6},{31,48},{13,22},{41,44},{10,19},{12,41},{1,12},
                {3,14},{40,50},{19,37},{16,26},{7,25},{22,33},{21,27},{9,50},{24,42},{43,46},{21,47},{29,40},
                {31,34},{9,31},{14,31},{5,48},{3,18},{4,19},{8,17},{38,46},{35,37},{17,43}});
        strRet = Arrays.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("[5, 48]"));


    }
}
