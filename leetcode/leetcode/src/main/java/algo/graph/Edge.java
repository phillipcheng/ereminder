package algo.graph;

public class Edge implements IWeightedEdge{
	
	public int from;
	public int to;
	public int weight;
	
	public Edge(int from, int to, int weight){
		this.from = from;
		this.to  = to;
		this.weight = weight; 
	}
	
	public String toString(){
		return String.format("from:%d, to:%d, weight:%d", from, to, weight);
	}

	public int getWeight() {
		return weight;
	}

}
