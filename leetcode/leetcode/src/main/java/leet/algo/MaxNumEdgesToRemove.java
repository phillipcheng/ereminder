package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class MaxNumEdgesToRemove {
    private static Logger logger =  LogManager.getLogger(MaxNumEdgesToRemove.class);

    public class UnionFind {
        int maxCount=0;
        int[] parent; //parent node
        int[] count; //the count of sub nodes

        public String toString(){
            String s = "parent:" + Arrays.toString(parent);
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

    public int maxNumEdgesToRemove(int n, int[][] edges) {
        UnionFind uf1 = new UnionFind(n);
        UnionFind uf2 = new UnionFind(n);
        Arrays.sort(edges, (a, b)->{return b[0]-a[0];});
        int remove=0;
        for (int[] edge: edges){
            if (edge[0]==3){//common edge
                if (uf1.isConnected(edge[1]-1, edge[2]-1)){
                    remove++;
                }else{
                    uf1.union(edge[1]-1, edge[2]-1);
                    uf2.union(edge[1]-1, edge[2]-1);
                }
            }else if (edge[0]==1){
                if (uf1.isConnected(edge[1]-1, edge[2]-1)) {
                    remove++;
                }else{
                    uf1.union(edge[1]-1, edge[2]-1);
                }
            }else{//edge[0]==2
                if (uf2.isConnected(edge[1]-1, edge[2]-1)) {
                    remove++;
                }else{
                    uf2.union(edge[1]-1, edge[2]-1);
                }
            }
//            logger.info("uf1:" + uf1);
//            logger.info("uf2:" + uf2);
        }
        if (uf1.isAllConnected() && uf2.isAllConnected()){
            return remove;
        }else{
            return -1;
        }
    }

    public static void main(String args[]){
        MaxNumEdgesToRemove maxNumEdgesToRemove = new MaxNumEdgesToRemove();
        int ret;

        ret = maxNumEdgesToRemove.maxNumEdgesToRemove(4, IOUtil.getIntArrayArray(
                "[[3,1,2],[3,2,3],[1,1,3],[1,2,4],[1,1,2],[2,3,4]]"));
        logger.info(ret);
        assertTrue(ret==2);

        ret = maxNumEdgesToRemove.maxNumEdgesToRemove(4, IOUtil.getIntArrayArray(
                "[[3,1,2],[3,2,3],[1,1,4],[2,1,4]]"));
        logger.info(ret);
        assertTrue(ret==0);

        ret = maxNumEdgesToRemove.maxNumEdgesToRemove(4, IOUtil.getIntArrayArray(
                "[[3,2,3],[1,1,2],[2,3,4]]"));
        logger.info(ret);
        assertTrue(ret==-1);

    }
}
