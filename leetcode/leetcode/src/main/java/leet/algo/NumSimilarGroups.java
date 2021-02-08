package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

public class NumSimilarGroups {
    private static Logger logger =  LogManager.getLogger(NumSimilarGroups.class);

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

        public int rootNum(){
            Set<Integer> roots = new HashSet<>();
            for (int i=0; i<parent.length; i++){
                roots.add(root(i));
            }
            return roots.size();
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

    boolean similar(String a, String b){
        int count=0;
        for (int i=0; i<a.length(); i++){
            char c1 = a.charAt(i);
            char c2 = b.charAt(i);
            if (c1!=c2){
                count++;
            }
        }
        if (count==2 || count==0){
            return true;
        }else{
            return false;
        }
    }

    public int numSimilarGroups(String[] strs) {
        UnionFind uf = new UnionFind(strs.length);
        for (int i=0; i<strs.length; i++){
            for (int j=i+1; j<strs.length; j++){
                if (!uf.isConnected(i, j)){
                    if (similar(strs[i], strs[j])){
                        uf.union(i, j);
                    }
                }
            }
        }
        return uf.rootNum();
    }

    public static void main(String[] args){
        NumSimilarGroups numSimilarGroups = new NumSimilarGroups();
        int ret;

        ret = numSimilarGroups.numSimilarGroups(new String[]{"tars","rats","arts","star"});
        logger.info(ret);
        assertTrue(ret==2);

        ret = numSimilarGroups.numSimilarGroups(new String[]{"omv","ovm"});
        logger.info(ret);
        assertTrue(ret==1);

        ret = numSimilarGroups.numSimilarGroups(new String[]{"abc","abc"});
        logger.info(ret);
        assertTrue(ret==1);
    }
}
