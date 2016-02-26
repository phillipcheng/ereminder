package algo.graph;

public class UndirectedWithHeapWeightEdge implements IWeightedEdge{
	
	public int from;
	public int to;
	public int weight;
	public int heapWeight;//customized heap weight, for example for Dijkstra SP, this is sp[n]+weight
	
	public UndirectedWithHeapWeightEdge(int from, int to, int weight, int heapWeight){
		this.from = from;
		this.to  = to;
		this.weight = weight; 
		this.heapWeight = heapWeight;
	}
	
	public String toString(){
		return String.format("from:%d, to:%d, weight:%d", from, to, weight);
	}
	
	public boolean equals(Object o){
		if (o instanceof UndirectedWithHeapWeightEdge){
			UndirectedWithHeapWeightEdge edgeB = (UndirectedWithHeapWeightEdge)o;
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
		return heapWeight;
	}

}
