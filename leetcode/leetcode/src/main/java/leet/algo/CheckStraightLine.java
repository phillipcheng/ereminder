package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static junit.framework.TestCase.assertTrue;

public class CheckStraightLine {

    private static Logger logger =  LogManager.getLogger(CheckStraightLine.class);

    public boolean checkStraightLine(int[][] coordinates) {
        int count = coordinates.length;
        if (count<=2){
            return true;
        }else{
            int baseX = coordinates[1][0] - coordinates[0][0];
            int baseY = coordinates[1][1] - coordinates[0][1];
            for (int i=2; i<count; i++){
                int vectorX = coordinates[i][0] - coordinates[0][0];
                int vectorY = coordinates[i][1] - coordinates[0][1];
                int cross = baseX*vectorY-baseY*vectorX;
                if (cross!=0){
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args){
        CheckStraightLine checkStraightLine = new CheckStraightLine();
        int[][] input;
        boolean ret;

        input = IOUtil.getIntArrayArray("[[1,2],[2,3],[3,4],[4,5],[5,6],[6,7]]");
        ret = checkStraightLine.checkStraightLine(input);
        assertTrue(ret == true);

        input = IOUtil.getIntArrayArray("[[1,1],[2,2],[3,4],[4,5],[5,6],[7,7]]");
        ret = checkStraightLine.checkStraightLine(input);
        assertTrue(ret == false);

    }
}
