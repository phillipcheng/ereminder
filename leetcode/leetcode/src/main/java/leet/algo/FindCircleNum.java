package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static junit.framework.TestCase.assertTrue;

public class FindCircleNum {
    private static Logger logger =  LogManager.getLogger(FindCircleNum.class);

    //return the visited idx
    Set<Integer> dfs(int start, int[][] graph){
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> stack = new Stack();
        stack.push(start);
        while(!stack.isEmpty()){
            int idx = stack.pop();
            if (!visited.contains(idx)){
                int[] nexts = graph[idx];
                for (int i=0; i<nexts.length; i++){
                    if (nexts[i]==1){
                        stack.push(i);
                    }
                }
                visited.add(idx);
            }
        }
        return visited;
    }

    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        Set<Integer> set = new HashSet<>();
        for (int i=0; i<n; i++){
            set.add(i);
        }
        int cn = 0;
        while (!set.isEmpty()){
            int start = set.iterator().next();
            Set<Integer> nodes = dfs(start, isConnected);
            set.removeAll(nodes);
            cn++;
        }
        return cn;
    }

    public static void main(String[] args){
        FindCircleNum findCircleNum = new FindCircleNum();
        int[][] isConnected;
        int circleNum;

        isConnected = new int[][]{{1,1,0},{1,1,0},{0,0,1}};
        circleNum = findCircleNum.findCircleNum(isConnected);
        logger.info(circleNum);
        assertTrue(circleNum == 2);

        isConnected = new int[][]{{1,0,0},{0,1,0},{0,0,1}};
        circleNum = findCircleNum.findCircleNum(isConnected);
        logger.info(circleNum);
        assertTrue(circleNum == 3);

    }
}
