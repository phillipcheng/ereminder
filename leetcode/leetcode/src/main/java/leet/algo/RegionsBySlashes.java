package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

public class RegionsBySlashes {
    private static Logger logger =  LogManager.getLogger(RegionsBySlashes.class);

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
        }

        public int root(int idx){
            while (parent[idx]!=idx){
                idx= parent[idx];
            }
            return idx;
        }

        public void union(int i, int j){
            //logger.info(String.format("union: %d, %d", i, j));
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
        public int getCount(int idx){
            return count[root(idx)];
        }
        public int getTreeCount(){
            Set<Integer> roots = new HashSet<>();
            for(int i=0; i<count.length; i++){
                roots.add(root(i));
            }
            return roots.size();
        };
        public boolean isAllConnected(){
            return maxCount == count.length;
        }

    }

    public char getChar(String[] grid, int x, int y){
        int n = grid.length;
        if (x>=n) return 0;
        if (y>=n) return 0;
        return grid[x].charAt(y);
    }

    public void innerUnion(char ch, int n, int i, int j, UnionFind uf){
        int base = 4*(i*n+j);
        if (ch==' '){
            uf.union(base+3, base);
            uf.union(base, base+1);
            uf.union(base+1, base+2);
            uf.union(base+2, base+3);
        }else if (ch=='\\'){
            uf.union(base+2, base+3);
            uf.union(base, base+1);
        }else if (ch=='/'){
            uf.union(base, base+3);
            uf.union(base+1, base+2);
        }
    }

    public void outerUnionDown(int n, int i, int j, UnionFind uf){
        //(i,j), (i+1,j)
        if (i+1>=n){
        }else{
            uf.union(4*(i*n+j)+2, 4*((i+1)*n+j));
        }
    }
    public void outerUnionLeft(int n, int i, int j, UnionFind uf){
        if (j+1>=n){
        }else{
            uf.union(4*(i*n+j)+1, 4*(i*n+j+1)+3);
        }
    }

    public int regionsBySlashes(String[] grid) {
        int n = grid.length;
        UnionFind uf = new UnionFind(n*n*4);
        for (int sum=0; sum<=n-1; sum++){
            for (int i=0; i<=sum; i++){
                int j = sum - i;
                char ch = getChar(grid, i, j);
                innerUnion(ch, n, i, j, uf);
                outerUnionDown(n, i, j, uf);
                outerUnionLeft(n, i, j, uf);
            }
        }
        for (int sum=n; sum<2*n-1; sum++){
            for (int i=sum-n+1; i<n; i++){
                int j= sum -i;
                char ch = getChar(grid, i, j);
                innerUnion(ch, n, i, j, uf);
                outerUnionDown(n, i, j, uf);
                outerUnionLeft(n, i, j, uf);
            }
        }
        return uf.getTreeCount();
    }

    public static void main(String[] args){
        RegionsBySlashes regionsBySlashes = new RegionsBySlashes();
        int ret;

        ret = regionsBySlashes.regionsBySlashes(new String[]{
                " /",
                "/ "});
        logger.info(ret);
        assertTrue(ret==2);

        ret = regionsBySlashes.regionsBySlashes(new String[]{
                " /",
                "  "});
        logger.info(ret);
        assertTrue(ret==1);

        ret = regionsBySlashes.regionsBySlashes(new String[]{
                "\\/",
                "/\\"});
        logger.info(ret);
        assertTrue(ret==4);

        ret = regionsBySlashes.regionsBySlashes(new String[]{
                "/\\",
                "\\/"});
        logger.info(ret);
        assertTrue(ret==5);

        ret = regionsBySlashes.regionsBySlashes(new String[]{
                "//",
                "/ "});
        logger.info(ret);
        assertTrue(ret==3);
    }
}
