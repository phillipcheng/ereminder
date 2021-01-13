package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class SmallestStringWithSwaps {
    private static Logger logger =  LogManager.getLogger(SmallestStringWithSwaps.class);

    class Vertex{
        public Integer fromNode;
        public Set<Integer> toNodes;
        public Vertex(Integer fromNode, Set<Integer> toNodes){
            this.fromNode = fromNode;
            this.toNodes = toNodes;
        }
        public Vertex(Integer fromNode){
            this.fromNode = fromNode;
        }
        public String toString(){
            StringBuffer sb = new StringBuffer();
            for (Integer toNode: toNodes){
                sb.append(toNode + ",");
            }
            return sb.toString();
        }
    }

    class Graph{
        Map<Integer, Vertex> nodes = new HashMap<>();

        public String toString(){
            StringBuffer sb = new StringBuffer();
            sb.append("nodes:" + nodes);
            return sb.toString();
        }

        void addNode(Integer node){
            if (!nodes.containsKey(node)){
                Vertex v = new Vertex(node);
                nodes.put(node, v);
            }
        }

        void addEdge(Integer fromNode, Integer toNode){
            if (nodes.containsKey(fromNode)){
                Vertex v = nodes.get(fromNode);
                v.toNodes.add(toNode);
            }else{
                Set<Integer> toNodes = new HashSet<>();
                toNodes.add(toNode);
                Vertex v = new Vertex(fromNode, toNodes);
                nodes.put(fromNode, v);
            }
        }

        Set<Integer> dfs(Integer fromNode){
            Vertex start = nodes.get(fromNode);
            if (start == null) return null;
            Set<Integer> visited = new HashSet<>();
            Stack<Vertex> stack = new Stack();
            stack.push(start);
            while(!stack.isEmpty()){
                Vertex v = stack.pop();
                if (!visited.contains(v.fromNode)){
                    if (v.toNodes!=null) {
                        for (Integer tn : v.toNodes) {
                            stack.push(nodes.get(tn));
                        }
                    }
                    visited.add(v.fromNode);
                }
            }
            return visited;
        }
    }

    public String smallestStringWithSwaps(String s, List<List<Integer>> pairs) {
        Graph g = new Graph();
        for (List<Integer> pair: pairs){
            g.addEdge(pair.get(0), pair.get(1));
            g.addEdge(pair.get(1), pair.get(0));
        }
        for (int j=0; j<s.length(); j++){
            g.addNode(j);
        }
        char[] output = new char[s.length()];
        Set<Integer> allVisited = new HashSet<>();
        for (int i=0; i<s.length(); i++){
            if (!allVisited.contains(i)) {
                Set<Integer> visited = g.dfs(i);
                //logger.info("visited:" + visited);
                allVisited.addAll(visited);
                int size = visited.size();
                int[] idxArray = new int[size];
                char[] charArray = new char[size];
                Iterator<Integer> visitedIt = visited.iterator();
                int k = 0;
                while (visitedIt.hasNext()){
                    int idx = visitedIt.next();
                    char ch = s.charAt(idx);
                    idxArray[k]=idx;
                    charArray[k]=ch;
                    k++;
                }
                Arrays.sort(idxArray);
                Arrays.sort(charArray);
                for (k=0; k<idxArray.length; k++){
                    output[idxArray[k]]=charArray[k];
                }
            }
        }
        return new String(output);
    }

    public static void main(String[] args){
        SmallestStringWithSwaps smallestStringWithSwaps = new SmallestStringWithSwaps();
        String s;
        String input;
        List<List<Integer>> pairs;
        String ret;

        input = "[[0,3],[1,2]]";
        pairs = IOUtil.getIntListList(input);
        s = "dcab";
        ret = smallestStringWithSwaps.smallestStringWithSwaps(s, pairs);
        logger.info(ret);
        assertTrue(ret.equals("bacd"));

        input = "[[0,3],[1,2],[0,2]]";
        pairs = IOUtil.getIntListList(input);
        s = "dcab";
        ret = smallestStringWithSwaps.smallestStringWithSwaps(s, pairs);
        logger.info(ret);
        assertTrue(ret.equals("abcd"));

        input = "[]";
        pairs = IOUtil.getIntListList(input);
        s = "dcab";
        ret = smallestStringWithSwaps.smallestStringWithSwaps(s, pairs);
        logger.info(ret);
        assertTrue(ret.equals("dcab"));

    }
}
