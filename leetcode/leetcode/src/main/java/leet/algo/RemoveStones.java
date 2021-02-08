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

        public int getNodeNum(){
            return unvisited.size();
        }

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
            //setup graph
            for (int[] node: stones){
                addRow(node[0], node[1]);
                addCol(node[1], node[0]);
                unvisited.add(Arrays.asList(new Integer[]{node[0], node[1]}));
            }
        }

        public Set<List<Integer>> dfsMarkVisit(List<Integer> start){
            Stack<List<Integer>> stack = new Stack<>();
            Set<List<Integer>> visited = new HashSet<>();
            stack.push(start);
            while (!stack.isEmpty()){
                List<Integer> node = stack.pop();
                visited.add(node);
                unvisited.remove(node);
                List<List<Integer>> adjacent = getAdjacentNodes(node.get(0), node.get(1));
                for (List<Integer> an: adjacent){
                    if (!visited.contains(an)){
                        stack.push(an);
                    }
                }
            }
            return visited;
        }
    }

    public int removeStones(int[][] stones) {
        Graph graph = new Graph(stones);
        int orgNodeNum = graph.getNodeNum();
        List<Integer> node = graph.getUnvisited();
        int numSCC = 0;
        while (node!=null){
            Set<List<Integer>> treeNodes = graph.dfsMarkVisit(node);
            //logger.info("tree:" + treeNodes);
            numSCC++;
            node = graph.getUnvisited();
        }
        return orgNodeNum-numSCC;
    }
    
    public static void main(String[] args){
        RemoveStones removeStones = new RemoveStones();
        int[][] stones;
        int ret;

        stones = IOUtil.getIntArrayArray("[[0,0],[0,1],[1,0],[1,2],[2,1],[2,2]]");
        ret = removeStones.removeStones(stones);
        logger.info(ret);
        assertTrue(ret == 5);

        stones = IOUtil.getIntArrayArray("[[0,0],[0,2],[1,1],[2,0],[2,2]]");
        ret = removeStones.removeStones(stones);
        logger.info(ret);
        assertTrue(ret == 3);

        stones = IOUtil.getIntArrayArray("[[0,0]]");
        ret = removeStones.removeStones(stones);
        logger.info(ret);
        assertTrue(ret == 0);
    }
}
