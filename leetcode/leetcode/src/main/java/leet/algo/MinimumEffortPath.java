package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import static junit.framework.TestCase.assertTrue;

public class MinimumEffortPath {
    private static Logger logger =  LogManager.getLogger(MinimumEffortPath.class);

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

    class Edge{
        int weight;
        int fromIdx;
        int toIdx;
        public Edge(int weight, int from, int to){
            this.weight = weight;
            this.fromIdx = from;
            this.toIdx = to;
        }
    }
    int idx(int c, int i, int j){
        return c*i+j;
    }
    public int minimumEffortPath(int[][] heights) {
        int r = heights.length;
        int c = heights[0].length;
        PriorityQueue<Edge> pq = new PriorityQueue<>(new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                return o1.weight-o2.weight;
            }
        });

        UnionFind uf = new UnionFind(r*c);
        for (int i=0; i<r; i++){
            for (int j=0; j<c; j++){
                //(i,j) to (i+1, j), (i, j+1)
                if (i+1<r){
                    Edge edge = new Edge(Math.abs(heights[i][j]-heights[i+1][j]), idx(c, i, j), idx(c, i+1, j));
                    pq.add(edge);
                }
                if (j+1<c){
                    Edge edge = new Edge(Math.abs(heights[i][j]-heights[i][j+1]), idx(c, i, j), idx(c, i, j+1));
                    pq.add(edge);
                }
            }
        }
        while (!pq.isEmpty()){
            Edge edge = pq.poll();
            uf.union(edge.fromIdx, edge.toIdx);
            if (uf.isConnected(0, r*c-1)){
                return edge.weight;
            }
        }
        return 0;
    }

    public static void main(String args[]){
        MinimumEffortPath minimumEffortPath = new MinimumEffortPath();
        int ret;

        ret = minimumEffortPath.minimumEffortPath(IOUtil.getIntArrayArray("[[1,2,2],[3,8,2],[5,3,5]]"));
        logger.info(ret);
        assertTrue(ret==2);


        ret = minimumEffortPath.minimumEffortPath(IOUtil.getIntArrayArray("[[1,2,3],[3,8,4],[5,3,5]]"));
        logger.info(ret);
        assertTrue(ret==1);


        ret = minimumEffortPath.minimumEffortPath(IOUtil.getIntArrayArray(
                "[[1,2,1,1,1],[1,2,1,2,1],[1,2,1,2,1],[1,2,1,2,1],[1,1,1,2,1]]"));
        logger.info(ret);
        assertTrue(ret==0);
    }
}
