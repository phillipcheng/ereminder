package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class SortItems {

    private static Logger logger =  LogManager.getLogger(SortItems.class);

    //1,2 -> 3, 4
    //edge between groups, edge between nodes within group
    class Graph<T>{
        Set<T> nodes = new HashSet<>();
        Map<T, Set<T>> edges = new HashMap<>();//from -> to
        Set<T> zeroIn = new HashSet<>();
        Map<T, Set<T>> inMap = new HashMap<>();//to -> from

        public String toString(){
            StringBuffer sb = new StringBuffer();
            sb.append("nodes:").append(nodes.toString());
            sb.append(",edges:").append(edges.toString());
            sb.append(",zeroIn:").append(zeroIn.toString());
            sb.append(", inMap:").append(inMap);
            return sb.toString();
        }

        public void addNode(T i){
            nodes.add(i);
            zeroIn.add(i);
            inMap.put(i, new HashSet<>());
        }

        public void addEdge(T from, T to){
            zeroIn.remove(to);
            Set<T> fromSet = inMap.get(to);
            fromSet.add(from);
            inMap.put(to, fromSet);
            if (edges.containsKey(from)){
                edges.get(from).add(to);
            }else{
                Set<T> set = new HashSet<>();
                set.add(to);
                edges.put(from, set);
            }
        }

        List<T> topologySort(){
            List<T> ret = new ArrayList<>();
            Queue<T> que = new ArrayDeque<>();
            que.addAll(zeroIn);
            Map<T, Integer> inCntMap = new HashMap<>();
            for (T key: inMap.keySet()){
                inCntMap.put(key, inMap.get(key).size());
            }
            while(!que.isEmpty()){
                T t = que.poll();
                ret.add(t);
                if (edges.containsKey(t)) {
                    for (T n : edges.get(t)) {
                        int inlet = inCntMap.get(n) - 1;
                        inCntMap.put(n, inlet);
                        if (inlet == 0) {
                            que.add(n);
                        }
                    }
                }
            }
            return ret;
        }
    }


    public int[] sortItems(int n, int m, int[] group, List<List<Integer>> beforeItems) {
        Graph<Graph<Integer>> graph = new Graph<>();
        Map<Integer, Graph<Integer>> idxGroupMap = new HashMap<>();//idx to group
        Map<Integer, Graph<Integer>> groupMap = new HashMap<>();//group id to group
        for (int i=0; i<group.length; i++){
            int grp = group[i];
            if (grp == -1){
                Graph<Integer> group1 = new Graph<>();
                group1.addNode(i);
                idxGroupMap.put(i, group1);
                graph.addNode(group1);
            }else {
                if (groupMap.containsKey(grp)) {
                    Graph<Integer> group1 = groupMap.get(grp);
                    group1.addNode(i);
                    idxGroupMap.put(i, group1);
                } else {
                    Graph<Integer> group1 = new Graph<>();
                    group1.addNode(i);
                    idxGroupMap.put(i, group1);
                    groupMap.put(grp, group1);
                    graph.addNode(group1);
                }
            }
        }

        for (int i=0; i<beforeItems.size(); i++){
            List<Integer> befores = beforeItems.get(i);
            //before "before" i
            for (int before: befores){
                //check if before and i belongs to the same group
                Graph<Integer> grp1 = idxGroupMap.get(before);
                Graph<Integer> grp2 = idxGroupMap.get(i);
                if (grp1 == grp2){
                    grp1.addEdge(before, i);
                }else{//if not
                    graph.addEdge(grp1, grp2);
                }
            }
        }

        //logger.info(graph);
        List<Integer> ret = new ArrayList<>();
        List<Graph<Integer>> gil = graph.topologySort();
        //logger.info(gil);
        if (gil.size()<graph.nodes.size()){
            return new int[0];
        }
        for (Graph<Integer> gi: gil){
            List<Integer> li = gi.topologySort();
            //logger.info(li);
            if (li.size()<gi.nodes.size())
                return new int[0];
            ret.addAll(li);
        }
        int[] retarr = new int[ret.size()];
        for (int i=0; i<ret.size(); i++){
            retarr[i] = ret.get(i);
        }
        return retarr;
    }

    public static void main(String[] args){
        SortItems sortItems = new SortItems();
        int[] group;
        String strBeforeItems;
        List<List<Integer>> beforeItems;
        int[] ret;

        group = new int[]{-1,-1,1,0,0,1,0,-1};
        strBeforeItems = "[[],[6],[5],[6],[3,6],[],[],[]]";
        beforeItems = IOUtil.getIntListList(strBeforeItems);
        ret = sortItems.sortItems(8, 2, group, beforeItems);
        logger.info(Arrays.toString(ret));

        group = new int[]{-1,-1,1,0,0,1,0,-1};
        strBeforeItems = "[[],[6],[5],[6],[3,6],[],[4],[]]";
        beforeItems = IOUtil.getIntListList(strBeforeItems);
        ret = sortItems.sortItems(8, 2, group, beforeItems);
        logger.info(Arrays.toString(ret));

        group = new int[]{-1,0,0,-1};
        strBeforeItems = "[[],[0],[1,3],[2]]";
        beforeItems = IOUtil.getIntListList(strBeforeItems);
        ret = sortItems.sortItems(8, 2, group, beforeItems);
        logger.info(Arrays.toString(ret));

        group = new int[]{0,0,2,1,0};
        strBeforeItems = "[[3],[],[],[],[1,3,2]]";
        beforeItems = IOUtil.getIntListList(strBeforeItems);
        ret = sortItems.sortItems(8, 2, group, beforeItems);
        logger.info(Arrays.toString(ret));
    }
}
