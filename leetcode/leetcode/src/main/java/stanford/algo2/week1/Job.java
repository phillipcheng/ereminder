package stanford.algo2.week1;

public class Job implements Comparable<Job>{
	int weight;
	int length;
	public Job(int weight, int length){
		this.weight = weight;
		this.length = length;
	}
	
	public int compareTo(Job o) {
		return o.weight - this.weight;
	}
	public String toString(){
		return String.format("w:%d, l:%d, w-l:%d, w/l:%.2f", weight, length, weight-length, (float)weight/(float)length);
	}
}