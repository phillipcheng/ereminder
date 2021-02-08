package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class MakeConnected {
    private static Logger logger =  LogManager.getLogger(MakeConnected.class);

    public class UnionFind {
        int maxCount=0;
        int[] parent; //parent node
        int[] count; //the count of sub nodes
        int treeCount=0;

        public String toString(){
            String s = "id:" + Arrays.toString(parent);
            s+="\ncount:" + Arrays.toString(count);
            return s;
        }

        public UnionFind(int n){
            parent = new int[n];
            count = new int[n];
            for (int i=0; i<n; i++){
                parent[i] = i;
                count[i]=1;
            }
            treeCount=n;
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
                treeCount--;
            }
        }
        public boolean isConnected(int i, int j){
            return (root(i)==root(j));
        }
        public int getCount(int idx){
            return count[root(idx)];
        }
        public int getTreeCount(){return treeCount;};
        public boolean isAllConnected(){
            return maxCount == count.length;
        }

    }

    public int makeConnected(int n, int[][] connections) {
        UnionFind uf = new UnionFind(n);
        int spareNum=0;
        for (int i=0; i<connections.length; i++){
            int[] edge = connections[i];
            if (!uf.isConnected(edge[0], edge[1])){
                uf.union(edge[0], edge[1]);
            }else{
                spareNum++;
            }
        }
        int needNum = uf.treeCount - 1;
        if (spareNum>=needNum){
            return needNum;
        }else{
            return -1;
        }
    }

    public static void main(String[] args){
        MakeConnected makeConnected = new MakeConnected();
        int ret;

        ret = makeConnected.makeConnected(4, IOUtil.getIntArrayArray("[[0,1],[0,2],[1,2]]"));
        logger.info(ret);
        assertTrue(ret==1);

        ret = makeConnected.makeConnected(6, IOUtil.getIntArrayArray("[[0,1],[0,2],[0,3],[1,2],[1,3]]"));
        logger.info(ret);
        assertTrue(ret==2);

        ret = makeConnected.makeConnected(6, IOUtil.getIntArrayArray("[[0,1],[0,2],[0,3],[1,2]]"));
        logger.info(ret);
        assertTrue(ret==-1);

        ret = makeConnected.makeConnected(5, IOUtil.getIntArrayArray("[[0,1],[0,2],[3,4],[2,3]]"));
        logger.info(ret);
        assertTrue(ret==0);


    }
}
