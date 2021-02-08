package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class HitBricks {
    private static Logger logger =  LogManager.getLogger(HitBricks.class);

    void showGrid(int[][] grid){
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        for (int i=0; i<grid.length; i++){
            for (int j=0; j<grid[i].length; j++){
                sb.append(grid[i][j]).append(",");
            }
            sb.append("\n");
        }
        logger.info(sb.toString());
    }

    List<List<Integer>> getNeighbors(int[][] grid, List<Integer> point, boolean hasUp){
        int x = point.get(0);
        int y = point.get(1);
        return getNeighbors(grid, x, y, hasUp);
    }

    List<List<Integer>> getNeighbors(int[][] grid, int x, int y, boolean hasUp){
        List<List<Integer>> ret = new ArrayList<>();
        int rs = grid.length;
        int cs = grid[0].length;
        if (x>0){
            if (hasUp) {
                if (grid[x - 1][y] == 1) {
                    List<Integer> pt = new ArrayList<>();
                    pt.add(x - 1);
                    pt.add(y);
                    ret.add(pt);
                }
            }
        }
        if (y>0){
            if (grid[x][y-1]==1) {
                List<Integer> pt = new ArrayList<>();
                pt.add(x);
                pt.add(y - 1);
                ret.add(pt);
            }
        }
        if (x<rs-1){
            if (grid[x+1][y]==1) {
                List<Integer> pt = new ArrayList<>();
                pt.add(x + 1);
                pt.add(y);
                ret.add(pt);
            }
        }
        if (y<cs-1){
            if (grid[x][y+1]==1) {
                List<Integer> pt = new ArrayList<>();
                pt.add(x);
                pt.add(y + 1);
                ret.add(pt);
            }
        }
        return ret;
    }

    //return the nodes visited following dfs, if hit top return empty set
    Set<List<Integer>> dfs(int[][] grid, List<Integer> start, List<Integer> block){
        Stack<List<Integer>> stack = new Stack<>();
        Set<List<Integer>> visited = new HashSet<>();
        if (start.get(0)==0){
            return visited;
        }
        stack.push(start);
        while (!stack.isEmpty()){
            List<Integer> node = stack.pop();
            List<List<Integer>> neighbors = getNeighbors(grid, node, true);
            for (List<Integer> neighbor: neighbors){
                if (!block.equals(neighbor) && !visited.contains(neighbor)){
                    if (neighbor.get(0)==0){//return top return empty
                        visited.clear();
                        return visited;
                    }else{
                        stack.push(neighbor);
                    }
                }
            }
            visited.add(node);
        }
        //logger.info("dfs:" + start + ", visited:" + visited);
        return visited;
    }

    void falling(int[][] grid, Set<List<Integer>> nodes){
        for (List<Integer> node: nodes) {
            grid[node.get(0)][node.get(1)]=0;
        }
    }

    int hit(int[][] grid, int[] hit){
        List<Integer> head = new ArrayList<>();
        head.add(hit[0]);
        head.add(hit[1]);
        int ret=0;
        List<List<Integer>> nodes = getNeighbors(grid, head, false);
        for (List<Integer> node: nodes){
            if (grid[node.get(0)][node.get(1)]==1) {
                Set<List<Integer>> fallingNodes = dfs(grid, node, head);
                if (fallingNodes!=null && fallingNodes.size()>0) {
                    falling(grid, fallingNodes);
                    ret+=fallingNodes.size();
                }
            }
        }
        //fall hit node
        grid[hit[0]][hit[1]]=0;
        return ret;
    }

    public int[] hitBricks0(int[][] grid, int[][] hits) {
        int[] ret = new int[hits.length];
        for (int i=0; i<hits.length; i++){
            //showGrid(grid);
            //logger.info("hit:" + Arrays.toString(hits[i]));
            ret[i] = hit(grid, hits[i]);
        }
        return ret;
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

    public void joinUF(UnionFind uf, int i, int j, int w, int[][] status){
        int idx = i * w + j + 1;
        if (i == 0) {
            //connect to the top node
            uf.union(0, idx);
        }
        List<List<Integer>> neighbors = getNeighbors(status, i, j, true);
        for (List<Integer> neighbor: neighbors){
            int neighborIdx = neighbor.get(0) * w + neighbor.get(1)+1;
            uf.union(idx, neighborIdx);
        }
    }

    public int[] hitBricks(int[][] grid, int[][] hits) {
        int h = grid.length;
        int w = grid[0].length;

        UnionFind uf = new UnionFind(w*h+1);
        int[] ret = new int[hits.length];
        int[][] status = Arrays.stream(grid).map(int[]::clone).toArray(int[][]::new);
        for (int[] hit: hits){
            status[hit[0]][hit[1]]=0;
        }

        for (int i=0; i<h; i++){
            for (int j=0; j<w; j++){
                if (status[i][j]==1) {
                    joinUF(uf, i, j, w, status);
                }
            }
        }
        for (int i=hits.length-1; i>=0; i--){
            int[] hit = hits[i];
            if (grid[hit[0]][hit[1]]==0){
                ret[i]=0;
            }else{
                int prev = uf.getCount(0);
                joinUF(uf, hit[0], hit[1], w, status);
                int post = uf.getCount(0);
                ret[i] = Math.max(0, post-prev-1);
                status[hit[0]][hit[1]]=1;
            }
        }

        return ret;
    }

    public static void main(String[] args){
        HitBricks hitBricks = new HitBricks();
        int[][] grid;
        int[][] hits;
        int[] ret;

        grid = new int[][]{{1,0,0,0},{1,1,1,0}};
        hits = new int[][]{{1,0}};
        ret = hitBricks.hitBricks(grid, hits);
        logger.info(Arrays.toString(ret));
        assertTrue(Arrays.toString(ret).equals("[2]"));


        grid = new int[][]{{1,0,0,0},{1,1,0,0}};
        hits = new int[][]{{1,1},{1,0}};
        ret = hitBricks.hitBricks(grid, hits);
        logger.info(Arrays.toString(ret));
        assertTrue(Arrays.toString(ret).equals("[0, 0]"));

        grid = new int[][]{{0,1,0,1},{1,1,1,1},{0,1,0,0}};
        hits = new int[][]{{1,1},{0,3},{0,1}};
        ret = hitBricks.hitBricks(grid, hits);
        logger.info(Arrays.toString(ret));
        assertTrue(Arrays.toString(ret).equals("[2, 2, 0]"));

        grid = new int[][]{{1,1,1},{0,1,0},{0,0,0}};
        hits = new int[][]{{0,2},{2,0},{0,1},{1,2}};
        ret = hitBricks.hitBricks(grid, hits);
        logger.info(Arrays.toString(ret));
        assertTrue(Arrays.toString(ret).equals("[0, 0, 1, 0]"));

    }
}
