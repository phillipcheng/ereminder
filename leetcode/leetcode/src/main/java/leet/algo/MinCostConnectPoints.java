package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class MinCostConnectPoints {
    private static Logger logger =  LogManager.getLogger(MinCostConnectPoints.class);

    class Edge implements Comparable<Edge>{
        int weight;
        int pa;
        int pb;
        public Edge(int[][] points, int pa, int pb){
            this.weight = Math.abs(points[pa][0]-points[pb][0])+Math.abs(points[pa][1]-points[pb][1]);//i,0
            this.pa = pa;
            this.pb = pb;
        }
        @Override
        public int compareTo(Edge o) {
            if (this.weight-o.weight!=0) {
                return this.weight - o.weight;
            } else{
                if (this.pa - o.pa!=0){
                    return this.pa-o.pa;
                }else{
                    return this.pb-o.pb;
                }
            }
        }
        public String toString(){
            return String.format("%d: %d->%d", weight, pa, pb);
        }
    }

    public class UnionFind {
        int[] parent; //parent node
        int[] count; //the count of sub nodes

        public String toString(){
            String s = "id:" + Arrays.toString(parent);
            s+="\n" + "count:" + Arrays.toString(count);
            return s;
        }

        public UnionFind(int n){
            parent = new int[n];
            count = new int[n];
            for (int i=0; i<n; i++){
                parent[i] = i;
                count[i]=1;
            }
        }

        public int getCount(int idx){
            return count[root(idx)];
        }

        public int root(int idx){
            while (parent[idx]!=idx){
                idx= parent[idx];
            }
            return idx;
        }

        public void union(int i, int j){
            //moving smaller tree to be sub-tree of the other
            int ri = root(i);
            int rj = root(j);
            if (ri!= rj){
                if (count[ri]>count[rj]){
                    parent[rj]=ri;
                    count[ri]+=count[rj];
                }else{
                    parent[ri]=rj;
                    count[rj]+=count[ri];
                }
            }
        }
        public boolean isConnected(int i, int j){
            return (root(i)==root(j));
        }
    }

    public int prim1(int[][] points){
        PriorityQueue<Edge> pq = new PriorityQueue<>((e1, e2)->e1.weight-e2.weight);
        for (int i=0; i<points.length; i++){
            for (int j=i+1; j<points.length; j++){
                Edge edge = new Edge(points, i, j);
                pq.add(edge);
            }
        }
        UnionFind uf = new UnionFind(points.length);
        int count=0;
        int cost=0;
        while (!pq.isEmpty()){
            Edge edge = pq.poll();
            if (!uf.isConnected(edge.pa, edge.pb)){
                uf.union(edge.pa, edge.pb);
                cost+=edge.weight;
                count++;
                if (count>=points.length){
                    break;
                }
            }
        }
        return cost;
    }

    public int minCostConnectPoints(int[][] points) {
        return prim1(points);
    }

    public int prim0(int[][] points){
        Set<Integer> u = new HashSet<>();
        Set<Integer> v = new HashSet<>();
        SortedSet<Edge> sortedEdges = new TreeSet<>();//all edges has are (u, v) and weight sorted
        Map<String, Edge> soredEdgesMap = new HashMap<>();//key: u+v

        u.add(0);
        for (int i=1; i<points.length; i++){
            v.add(i);
            Edge edge = new Edge(points, 0, i);
            sortedEdges.add(edge);
            soredEdgesMap.put(edge.pa + ":" + edge.pb, edge);
        }
//
//        logger.info("u:"+u);
//        logger.info("v:"+v);
//        logger.info("sorted edge:"+sortedEdges);
//        logger.info("sorted edge map:"+soredEdgesMap);

        int cost=0;
        while (!v.isEmpty()) {
            Edge edge = sortedEdges.first();//lowest weight
            int v1 = edge.pb;
            cost+=edge.weight;

            u.add(v1);
            for (int a: u){//remove all edges (u to pb)
                Edge edge1 = soredEdgesMap.remove( a + ":" + edge.pb);
                if (edge1!=null){
                    sortedEdges.remove(edge1);
                }
            }

            v.remove(v1);
            for (int b: v){//add all edges (pb to v)
                Edge edge1 = new Edge(points, v1, b);
                sortedEdges.add(edge1);
                soredEdgesMap.put(edge1.pa + ":" + edge1.pb, edge1);
            }

            sortedEdges.remove(edge);//remove edge(pa, pb)
            soredEdgesMap.remove(edge.pa + ":" + edge.pb);

//            logger.info("add:" + edge);
//            logger.info("u:"+u);
//            logger.info("v:"+v);
//            logger.info("sorted edge:"+sortedEdges);
//            logger.info("sorted edge map:"+soredEdgesMap);
        }
        return cost;
    }



    public static void main(String[] argv){
        MinCostConnectPoints minCostConnectPoints = new MinCostConnectPoints();
        int ret;

        ret = minCostConnectPoints.minCostConnectPoints(IOUtil.getIntArrayArray(
                "[[0,0],[2,2],[3,10],[5,2],[7,0]]"));
        logger.info(ret);
        assertTrue(20==ret);

        ret = minCostConnectPoints.minCostConnectPoints(IOUtil.getIntArrayArray(
                "[[3,12],[-2,5],[-4,1]]"));
        logger.info(ret);
        assertTrue(18==ret);

        ret = minCostConnectPoints.minCostConnectPoints(IOUtil.getIntArrayArray(
                "[[0,0],[1,1],[1,0],[-1,1]]"));
        logger.info(ret);
        assertTrue(4==ret);

        ret = minCostConnectPoints.minCostConnectPoints(IOUtil.getIntArrayArray(
                "[[-1000000,-1000000],[1000000,1000000]]"));
        logger.info(ret);
        assertTrue(4000000==ret);

        ret = minCostConnectPoints.minCostConnectPoints(IOUtil.getIntArrayArray(
                "[[0,0]]"));
        logger.info(ret);
        assertTrue(0==ret);

    }
}
