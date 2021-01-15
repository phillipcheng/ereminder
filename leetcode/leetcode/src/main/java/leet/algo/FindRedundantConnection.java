package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class FindRedundantConnection {
    private static Logger logger =  LogManager.getLogger(FindRedundantConnection.class);

    class CircleResult{
        List<List<Integer>> path = new ArrayList<>();
        boolean finish = false;
        int anchor;

        public void addEdge(List<Integer> edge){
            if (path.size()==0){
                anchor = edge.get(1);
            }
            path.add(edge);
        }
    }

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
        CircleResult dfs(int node, Stack<Integer> ancestor){
            //logger.info("node:"+node + ", stack:"+ ancestor);
            Set<Integer> toNodes = edges.get(node);
            if (toNodes != null) {
                for (int toNode : toNodes) {
                    if (ancestor.size()==0 || ancestor.peek() != toNode) {
                        CircleResult ret;
                        List<Integer> edge = new ArrayList<>();
                        edge.add(node);
                        edge.add(toNode);
                        if (ancestor.contains(toNode)) {//circle detected
                            ret = new CircleResult();
                            ret.addEdge(edge);
                            return ret;
                        } else {
                            Stack<Integer> newAncestor = new Stack<>();
                            newAncestor.addAll(ancestor);
                            newAncestor.add(node);
                            ret = dfs(toNode, newAncestor);
                            //check node before
                            if (ret != null) {//has circle
                                if (ret.finish){
                                    return ret;
                                }else{
                                    ret.addEdge(edge);
                                    if (node == ret.anchor){
                                        ret.finish = true;
                                    }
                                }
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
        CircleResult cr = graph.dfs(start, new Stack<>());
        //logger.info("path:" + cr.path);
        TreeMap<Integer, int[]> idxToEdge = new TreeMap<>();
        for (List<Integer> edge: cr.path){
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

        ret = findRedundantConnection.findRedundantConnection(new int[][]{{1,2},{1,3},{2,3}});
        strRet = Arrays.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("[2, 3]"));

        ret = findRedundantConnection.findRedundantConnection(new int[][]{{1,2}, {2,3}, {3,4}, {1,4}, {1,5}});
        strRet = Arrays.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("[1, 4]"));

        ret = findRedundantConnection.findRedundantConnection(new int[][]{{1,3},{3,4},{1,5},{3,5},{2,3}});
        strRet = Arrays.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("[3, 5]"));

        ret = findRedundantConnection.findRedundantConnection(new int[][]{{30,44},{34,47},{22,32},{35,44},
                {26,36},{2,15},{38,41},{28,35},{24,37},{14,49},{44,45},{11,50},{20,39},{7,39},{19,22},{3,17},
                {15,25},{1,39},{26,40},{5,14},{6,23},{5,6},{31,48},{13,22},{41,44},{10,19},{12,41},{1,12},
                {3,14},{40,50},{19,37},{16,26},{7,25},{22,33},{21,27},{9,50},{24,42},{43,46},{21,47},{29,40},
                {31,34},{9,31},{14,31},{5,48},{3,18},{4,19},{8,17},{38,46},{35,37},{17,43}});
        strRet = Arrays.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("[5, 48]"));

        ret = findRedundantConnection.findRedundantConnection(new int[][]{{85,163},{37,148},{22,167},
                {60,114},{66,179},{53,118},{15,133},{52,70},{19,198},{147,184},{20,125},{76,153},{94,132},
                {98,127},{144,180},{2,109},{144,161},{89,197},{62,174},{81,149},{76,168},{36,197},
                {11,122},{140,145},{87,134},{131,154},{86,134},{3,80},{37,135},{36,163},{88,144},
                {24,109},{18,113},{57,115},{13,194},{9,104},{20,104},{36,78},{71,78},{59,174},{59,111},
                {107,192},{74,112},{85,190},{108,197},{21,157},{15,91},{74,130},{38,62},{127,145},
                {99,171},{115,168},{41,175},{75,168},{67,179},{21,68},{125,180},{63,124},{114,188},
                {17,69},{155,175},{61,196},{43,165},{10,189},{129,139},{152,174},{71,186},{86,146},
                {4,131},{7,193},{152,178},{77,160},{26,149},{92,179},{141,155},{121,164},{14,135},
                {28,70},{15,182},{85,177},{33,123},{10,30},{25,150},{19,105},{76,101},{45,58},
                {146,173},{79,96},{183,190},{40,124},{128,151},{142,153},{46,175},{50,156},{19,64},
                {64,174},{54,73},{67,114},{27,30},{12,110},{80,195},{48,184},{42,124},{99,102},
                {156,166},{150,199},{29,90},{106,166},{59,139},{44,67},{141,167},{81,123},{99,115},
                {19,100},{95,116},{21,97},{1,46},{55,73},{118,195},{148,151},{29,192},{47,98},{156,194},
                {32,133},{90,189},{18,101},{39,108},{35,58},{94,164},{24,129},{84,115},{113,147},
                {19,120},{15,187},{125,127},{23,118},{158,193},{6,93},{89,130},{51,190},{83,117},
                {46,181},{143,191},{11,165},{100,143},{9,122},{60,193},{49,83},{68,96},{5,37},{8,105},
                {124,126},{48,82},{46,116},{94,137},{147,159},{7,169},{56,74},{5,155},{103,185},
                {154,160},{56,185},{77,151},{79,132},{10,17},{53,65},{172,177},{31,136},{10,54},
                {7,165},{113,173},{47,119},{31,72},{93,144},{153,176},{31,123},{12,27},{94,199},
                {35,68},{102,200},{162,176},{94,170},{138,153},{15,102},{144,178},{106,151},{3,45},
                {70,149},{23,49},{81,196},{28,34},{21,61},{17,150},{40,154},{48,51},{78,192},{16,74},
                {116,158},{12,16},{13,34}});
        strRet = Arrays.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("[12, 16]"));

    }
}
