package algo.graph;

import java.util.Comparator;

//return min
public class EdgeWeightComparator implements Comparator<IWeightedEdge>{

	public int compare(IWeightedEdge o1, IWeightedEdge o2) {
		return o1.getWeight()-o2.getWeight();
	}

}
