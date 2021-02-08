package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class FindCriticalAndPseudoCriticalEdges {

    private static Logger logger =  LogManager.getLogger(FindCriticalAndPseudoCriticalEdges.class);

    public class UnionFind {
        int maxCount=0;
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
                    maxCount = Math.max(maxCount, count[ri]);
                }else{
                    parent[ri]=rj;
                    count[rj]+=count[ri];
                    maxCount = Math.max(maxCount, count[rj]);
                }
            }
        }
        public boolean isConnected(int i, int j){
            return (root(i)==root(j));
        }

        public boolean isAllConnected(){
            return maxCount == count.length;
        }
    }

    class Edge {
        int weight;
        int pa;
        int pb;
        int idx;
        public Edge(int a, int b, int weight, int idx){
            this.weight = weight;
            this.pa = a;
            this.pb = b;
            this.idx = idx;
        }
        public String toString(){
            return String.format("w:%d, a:%d, b:%d, idx:%d", weight, pa, pb, idx);
        }
    }

    //prim exclude edge
    public int primExEdge(int n, int[][] edges, int exEdge){
        PriorityQueue<Edge> pq = new PriorityQueue<>((e1, e2)->e1.weight-e2.weight);
        for (int i=0; i<edges.length; i++){
            if (i!=exEdge){
                int[] edgeArray = edges[i];
                pq.add(new Edge(edgeArray[0], edgeArray[1], edgeArray[2], i));
            }
        }
        UnionFind uf = new UnionFind(n);
        int count=0;
        int cost=0;
        while (!pq.isEmpty()){
            Edge edge = pq.poll();
            if (!uf.isConnected(edge.pa, edge.pb)){
                uf.union(edge.pa, edge.pb);
                cost+=edge.weight;
                count++;
                if (count==n-1){
                    break;
                }
            }
        }
        if (!uf.isAllConnected()){
            return -1;
        }
        return cost;
    }

    //prim include edge
    public int primInEdge(int n, int[][] edges, int inEdge){
        PriorityQueue<Edge> pq = new PriorityQueue<>((e1, e2)->e1.weight-e2.weight);
        for (int i=0; i<edges.length; i++){
            if (i!=inEdge){
                int[] edgeArray = edges[i];
                pq.add(new Edge(edgeArray[0], edgeArray[1], edgeArray[2], i));
            }
        }
        UnionFind uf = new UnionFind(n);
        int count=0;
        int cost=0;
        uf.union(edges[inEdge][0], edges[inEdge][1]);
        cost+=edges[inEdge][2];
        count++;
        while (!pq.isEmpty()){
            Edge edge = pq.poll();
            if (!uf.isConnected(edge.pa, edge.pb)){
                uf.union(edge.pa, edge.pb);
                cost+=edge.weight;
                count++;
                if (count==n-1){
                    break;
                }
            }
        }
        if (!uf.isAllConnected()){
            return -1;
        }
        return cost;
    }

    public List<List<Integer>> findCriticalAndPseudoCriticalEdges(int n, int[][] edges) {
        int lowCost = primExEdge(n, edges, -1);
        List<List<Integer>> ret = new ArrayList<>();
        List<Integer> critical = new ArrayList<>();
        for (int i=0; i<edges.length; i++){
            int cost = primExEdge(n, edges, i);
            if (cost!=lowCost){//bigger or -1(not connected)
                critical.add(i);
            }
        }
        List<Integer> noncritical = new ArrayList<>();
        for (int i=0; i<edges.length; i++){
            int cost = primInEdge(n, edges, i);
            if (cost == lowCost){
                noncritical.add(i);
            }
        }
        noncritical.removeAll(critical);
        ret.add(critical);
        ret.add(noncritical);
        return ret;
    }

    public static void main(String[] args){
        FindCriticalAndPseudoCriticalEdges findCriticalAndPseudoCriticalEdges = new FindCriticalAndPseudoCriticalEdges();
        List<List<Integer>> ret;

        ret = findCriticalAndPseudoCriticalEdges.findCriticalAndPseudoCriticalEdges(5,
                new int[][]{{0,1,1},{1,2,1},{2,3,2},{0,3,2},{0,4,3},{3,4,3},{1,4,6}});
        logger.info(ret);
        assertTrue(ret.toString().equals("[[0, 1], [2, 3, 4, 5]]"));

        ret = findCriticalAndPseudoCriticalEdges.findCriticalAndPseudoCriticalEdges(4,
                new int[][]{{0,1,1},{1,2,1},{2,3,1},{0,3,1}});
        logger.info(ret);
        assertTrue(ret.toString().equals("[[], [0, 1, 2, 3]]"));

        ret = findCriticalAndPseudoCriticalEdges.findCriticalAndPseudoCriticalEdges(4,
                new int[][]{{0,1,1},{0,3,1},{0,2,1},{1,2,1},{1,3,1},{2,3,1}});
        logger.info(ret);
        assertTrue(ret.toString().equals("[[], [0, 1, 2, 3, 4, 5]]"));


        ret = findCriticalAndPseudoCriticalEdges.findCriticalAndPseudoCriticalEdges(3,
                new int[][]{{0,1,1},{0,2,2},{1,2,3}});
        logger.info(ret);
        assertTrue(ret.toString().equals("[[0, 1], []]"));

    }
}
