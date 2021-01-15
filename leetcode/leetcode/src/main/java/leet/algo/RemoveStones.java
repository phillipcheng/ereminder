package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class RemoveStones {

    private static Logger logger =  LogManager.getLogger(RemoveStones.class);

    class Graph{
        Map<Integer, List<Integer>> rowToCol = new HashMap<>();
        Map<Integer, List<Integer>> colToRow = new HashMap<>();
        Set<List<Integer>> unvisited = new HashSet<>();

        public List<Integer> getUnvisited(){
            if (unvisited.isEmpty())
                return null;
            else {
                return unvisited.iterator().next();
            }
        }
        void addRow(int row, int col){
            if (rowToCol.containsKey(row)){
                rowToCol.get(row).add(col);
            }else{
                List<Integer> l = new ArrayList<>();
                l.add(col);
                rowToCol.put(row, l);
            }
        }

        void addCol(int col, int row){
            if (colToRow.containsKey(col)){
                colToRow.get(col).add(row);
            }else{
                List<Integer> l = new ArrayList<>();
                l.add(row);
                colToRow.put(col, l);
            }
        }

        public List<List<Integer>> getAdjacentNodes(int row, int col){
            List<List<Integer>> ret = new ArrayList<>();
            if (rowToCol.containsKey(row)){
                List<Integer> cols = rowToCol.get(row);
                for (Integer c: cols){
                    ret.add(Arrays.asList(new Integer[]{row, c}));
                }
            }
            if (colToRow.containsKey(col)){
                List<Integer> rows = colToRow.get(col);
                for (Integer r: rows){
                    ret.add(Arrays.asList(new Integer[]{r, col}));
                }
            }
            return ret;
        }

        public Graph(int[][] stones){
            int nrow = stones.length;
            int ncol = stones[0].length;
            //setup graph
            for (int row=0; row<nrow; row++){
                for (int col=0; col<ncol; col++){
                    if (stones[row][col] == 1){
                        addRow(row, col);
                        addCol(col, row);
                        unvisited.add(Arrays.asList(new Integer[]{row, col}));
                    }
                }
            }
        }

        public void dfsMarkVisit(List<Integer> start){

        }
    }

    public int removeStones(int[][] stones) {
        Graph graph = new Graph(stones);
        List<Integer> node = graph.getUnvisited();
        int numSCC = 0;
        while (node!=null){
            graph.dfsMarkVisit(node);
            numSCC++;
            node = graph.getUnvisited();
        }
        return numSCC;
    }
    
    public static void main(String[] args){
        RemoveStones removeStones = new RemoveStones();
        int[][] stones;
        int ret;

        stones = IOUtil.getIntArrayArray("[[0,0],[0,1],[1,0],[1,2],[2,1],[2,2]]");
        ret = removeStones.removeStones(stones);
        logger.info(ret);
        assertTrue(ret == 5);
    }
}
