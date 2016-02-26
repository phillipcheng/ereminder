package algo.graph;

public class UndirectedEdge implements IWeightedEdge{
	
	public int from;
	public int to;
	public int weight;
	
	public UndirectedEdge(int from, int to, int weight){
		this.from = from;
		this.to  = to;
		this.weight = weight; 
	}
	
	public String toString(){
		return String.format("from:%d, to:%d, weight:%d", from, to, weight);
	}
	
	public boolean equals(Object o){
		if (o instanceof UndirectedEdge){
			UndirectedEdge edgeB = (UndirectedEdge)o;
			if (weight == edgeB.weight && (from==edgeB.from && to==edgeB.to) || (from==edgeB.to && to==edgeB.from)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	public int getWeight() {
		return weight;
	}

}
