package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class CalcEquation {
    private static Logger logger =  LogManager.getLogger(CalcEquation.class);

    class Vertex{
        public String fromNode;
        public Set<String> toNodes;
        public Vertex(String fromNode, Set<String> toNodes){
            this.fromNode = fromNode;
            this.toNodes = toNodes;
        }
        public String toString(){
            StringBuffer sb = new StringBuffer();
            for (String toNode: toNodes){
                sb.append(toNode + ",");
            }
            return sb.toString();
        }
        List<String> getCloneToNodes(){
            List<String> ret = new ArrayList<>();
            ret.addAll(toNodes);
            return ret;
        }
    }

    class Graph{
        Map<String, Vertex> nodes = new HashMap<>();
        Map<String, Map<String, Double>> pathWeight = new HashMap<>();//DAG value is determined

        public String toString(){
            StringBuffer sb = new StringBuffer();
            sb.append("nodes:" + nodes);
            sb.append("\n");
            sb.append("pathWeight:" + pathWeight);
            return sb.toString();
        }

        void addPathWeight(String from, String to, double v){
            if (pathWeight.containsKey(from)){
                pathWeight.get(from).put(to, v);
            }else{
                Map<String, Double> m = new HashMap<>();
                m.put(to, v);
                pathWeight.put(from, m);
            }
        }

        double getWeightFromCache(String from, String to){
            if (pathWeight.containsKey(from)){
                Map<String, Double> map = pathWeight.get(from);
                if (map.containsKey(to)){
                    return map.get(to);
                }else{
                    return -1d;
                }
            }else{
                return -1d;
            }
        }

        //
        Map<String, Double> getToWeights(String from){
            return pathWeight.get(from);
        }

        void addEdge(String fromNode, String toNode, double weight){
            if (nodes.containsKey(fromNode)){
                Vertex v = nodes.get(fromNode);
                v.toNodes.add(toNode);
            }else{
                Set<String> toNodes = new HashSet<>();
                toNodes.add(toNode);
                Vertex v = new Vertex(fromNode, toNodes);
                nodes.put(fromNode, v);
            }
            addPathWeight(fromNode, toNode, weight);
        }

        void dfs(String fromNode){
            Vertex start = nodes.get(fromNode);
            if (start == null) return;
            Set<String> visited = new HashSet<>();
            Stack<Vertex> stack = new Stack();
            stack.push(start);
            while(!stack.isEmpty()){
                Vertex v = stack.pop();
                if (!visited.contains(v.fromNode)){
                    //visit fromNode, from --> to with toWeight == to --> from with 1/toWeight
                    Map<String, Double> toWeights = getToWeights(v.fromNode);
                    //logger.info(String.format("from node:%s, to weights:%s", v.fromNode, toWeights));
                    for (String tn: v.getCloneToNodes()){
                        //build weights from
                        double w2 = getWeightFromCache(v.fromNode, tn);
                        for (String t1: toWeights.keySet()){
                            double w1 = toWeights.get(t1);
                            //t1->from->t
                            addEdge(t1, tn, w2/w1);
                        }
                        stack.push(nodes.get(tn));
                    }
                    visited.add(v.fromNode);
                }
            }
        }

        double getWeight(String fromNode, String toNode) {
            double ret = getWeightFromCache(fromNode, toNode);
            if (ret != -1) return ret;
            dfs(fromNode);
            //logger.info(String.format("dfs:%s: %s", fromNode, toString()));
            return getWeightFromCache(fromNode, toNode);
        }

    }

    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Graph graph = new Graph();
        for (int i=0; i<equations.size(); i++){
            List<String> equ = equations.get(i);
            double w = values[i];
            graph.addEdge(equ.get(0), equ.get(1), w);
            graph.addEdge(equ.get(1), equ.get(0), 1/w);
        }
        //logger.info(graph.toString());
        double[] ret = new double[queries.size()];
        for (int j=0; j<queries.size(); j++){
            List<String> query = queries.get(j);
            ret[j] = graph.getWeight(query.get(0), query.get(1));
        }
        return ret;
    }

    public static void main(String[] args){
        CalcEquation calcEquation = new CalcEquation();
        String equationInputs;
        List<List<String>> equations;
        double[] values;
        List<List<String>> queries;
        String queryInputs;
        double[] ret;
        double[] ans;

        equationInputs = "[[\"a\",\"b\"],[\"a\",\"c\"],[\"a\",\"d\"],[\"a\",\"e\"],[\"a\",\"f\"],[\"a\",\"g\"],[\"a\",\"h\"],[\"a\",\"i\"],[\"a\",\"j\"],[\"a\",\"k\"],[\"a\",\"l\"],[\"a\",\"aa\"],[\"a\",\"aaa\"],[\"a\",\"aaaa\"],[\"a\",\"aaaaa\"],[\"a\",\"bb\"],[\"a\",\"bbb\"],[\"a\",\"ff\"]]";
        equations = IOUtil.getStringListList(equationInputs);
        values = new double[]{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,1.0,1.0,1.0,1.0,1.0,3.0,5.0};
        queryInputs = "[[\"d\",\"f\"],[\"e\",\"g\"],[\"e\",\"k\"],[\"h\",\"a\"],[\"aaa\",\"k\"],[\"aaa\",\"i\"],[\"aa\",\"e\"],[\"aaa\",\"aa\"],[\"aaa\",\"ff\"],[\"bbb\",\"bb\"],[\"bb\",\"h\"],[\"bb\",\"i\"],[\"bb\",\"k\"],[\"aaa\",\"k\"],[\"k\",\"l\"],[\"x\",\"k\"],[\"l\",\"ll\"]]";
        queries = IOUtil.getStringListList(queryInputs);
        ret = calcEquation.calcEquation(equations, values, queries);
        //ans = new double[]{360.0,0.00833,20.0,1.0,-1.0,-1.0};
        logger.info(Arrays.toString(ret));
        //assertTrue(IOUtil.equals(ret, ans));


        equationInputs = "[[\"x1\",\"x2\"],[\"x2\",\"x3\"],[\"x3\",\"x4\"],[\"x4\",\"x5\"]]";
        equations = IOUtil.getStringListList(equationInputs);
        values = new double[]{3.0,4.0,5.0,6.0};
        queryInputs = "[[\"x1\",\"x5\"],[\"x5\",\"x2\"],[\"x2\",\"x4\"],[\"x2\",\"x2\"],[\"x2\",\"x9\"],[\"x9\",\"x9\"]]";
        queries = IOUtil.getStringListList(queryInputs);
        ret = calcEquation.calcEquation(equations, values, queries);
        ans = new double[]{360.0,0.00833,20.0,1.0,-1.0,-1.0};
        logger.info(Arrays.toString(ret));
        assertTrue(IOUtil.equals(ret, ans));

        equationInputs = "[[\"a\",\"b\"],[\"b\",\"c\"]]";
        equations = IOUtil.getStringListList(equationInputs);
        values = new double[]{2.0,3.0};
        queryInputs = "[[\"a\",\"c\"],[\"b\",\"a\"],[\"a\",\"e\"],[\"a\",\"a\"],[\"x\",\"x\"]]";
        queries = IOUtil.getStringListList(queryInputs);
        ret = calcEquation.calcEquation(equations, values, queries);
        ans = new double[]{6.0, 0.5, -1.0, 1.0, -1.0};
        logger.info(Arrays.toString(ret));
        assertTrue(IOUtil.equals(ret, ans));


        equationInputs = "[[\"a\",\"b\"],[\"b\",\"c\"],[\"bc\",\"cd\"]]";
        equations = IOUtil.getStringListList(equationInputs);
        values = new double[]{1.5,2.5,5.0};
        queryInputs = "[[\"a\",\"c\"],[\"c\",\"b\"],[\"bc\",\"cd\"],[\"cd\",\"bc\"]]";
        queries = IOUtil.getStringListList(queryInputs);
        ret = calcEquation.calcEquation(equations, values, queries);
        ans = new double[]{3.75000,0.40000,5.00000,0.20000};
        logger.info(Arrays.toString(ret));
        assertTrue(IOUtil.equals(ret, ans));


        equationInputs = "[[\"a\",\"b\"]]";
        equations = IOUtil.getStringListList(equationInputs);
        values = new double[]{0.5};
        queryInputs = "[[\"a\",\"b\"],[\"b\",\"a\"],[\"a\",\"c\"],[\"x\",\"y\"]]";
        queries = IOUtil.getStringListList(queryInputs);
        ret = calcEquation.calcEquation(equations, values, queries);
        ans = new double[]{0.50000,2.00000,-1.00000,-1.00000};
        logger.info(Arrays.toString(ret));
        assertTrue(IOUtil.equals(ret, ans));


    }
}
