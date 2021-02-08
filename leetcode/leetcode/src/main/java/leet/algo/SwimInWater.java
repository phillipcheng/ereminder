package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class SwimInWater {

    private static Logger logger =  LogManager.getLogger(SwimInWater.class);

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

    class Point{
        int weight;
        int r;
        int c;
        public Point(int weight, int r, int c){
            this.weight = weight;
            this.r = r;
            this.c = c;
        }
    }
    List<Point> getNeighborPoints(int[][] grid, int i, int j){
        List<Point> points = new ArrayList<>();
        int r = grid.length;
        int c = grid[0].length;
        //(i,j) to (i+1, j), (i-1, j), (i, j+1), (i, j-1)
        if (i+1<r){
            points.add(new Point(grid[i+1][j], i+1, j));
        }
        if (i-1>=0){
            points.add(new Point(grid[i-1][j], i-1, j));
        }
        if (j+1<c){
            points.add(new Point(grid[i][j+1], i, j+1));
        }
        if (j-1>=0){
            points.add(new Point(grid[i][j-1], i, j-1));
        }
        return points;
    }

    int idx(int c, int i, int j){
        return c*i+j;
    }
    public int swimInWater(int[][] grid) {
        int r = grid.length;
        int c = grid[0].length;
        PriorityQueue<Point> pq = new PriorityQueue<>(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return o1.weight-o2.weight;
            }
        });
        for (int i=0; i<r; i++){
            for (int j=0; j<c; j++){
                pq.add(new Point(grid[i][j], i, j));
            }
        }
        UnionFind uf = new UnionFind(r*c);
        while (!pq.isEmpty()){
            Point point = pq.poll();
            //(i,j) to (i+1, j), (i-1, j), (i, j+1), (i, j-1)
            List<Point> points = getNeighborPoints(grid, point.r, point.c);
            for (Point neighbor: points){
                if (point.weight>neighbor.weight){
                    uf.union(idx(c, point.r, point.c), idx(c, neighbor.r, neighbor.c));
                }
            }
            if (uf.isConnected(0, r*c-1)){
                return point.weight;
            }
        }
        return 0;
    }

    public static void main(String args[]){
        SwimInWater swimInWater = new SwimInWater();
        int ret;

        ret = swimInWater.swimInWater(IOUtil.getIntArrayArray("[[0,2],[1,3]]"));
        logger.info(ret);
        assertTrue(ret==3);


        ret = swimInWater.swimInWater(IOUtil.getIntArrayArray(
                "[[0,1,2,3,4],[24,23,22,21,5],[12,13,14,15,16],[11,17,18,19,20],[10,9,8,7,6]]"));
        logger.info(ret);
        assertTrue(ret==16);

    }
}
