package leet.algo;

public class GasStation {
	
	public int canCompleteCircuit(int[] gas, int[] cost) {
		int length = gas.length;
        int cur=0;
        int gasSum=gas[cur];
        int costSum = cost[cur];
        int stops=0;
        int tried=0;
        while (true){
        	while (gasSum>=costSum && stops<length){
        		cur = (cur+1)%length;
        		tried++;
        		gasSum+=gas[cur];
        		costSum+=cost[cur];
        		stops++;
        	}
        	if (stops==length){
        		return cur;
        	}else{
        		cur = (cur+1)%length;
        		tried++;
        		if (tried>=length){
        			return -1;
        		}else{
	        		gasSum=gas[cur];
	        		costSum = cost[cur];
	        		stops=0;
        		}
        	}
        }
    }

}
