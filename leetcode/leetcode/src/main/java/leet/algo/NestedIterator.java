package leet.algo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NestedIterator implements Iterator<Integer> {
	Iterator<Integer> ilit;
	
	public NestedIterator(List<NestedInteger> nestedList) {
        List<Integer> alist = new ArrayList<Integer>();
        toList(nestedList, alist);
        ilit = alist.iterator();
    }
	
	private void toList(List<NestedInteger> node, List<Integer> output){
		for (NestedInteger ni:node){
			if (ni.isInteger()){
				output.add(ni.getInteger());
			}else{
				toList(ni.getList(), output);
			}
		}
	}

    @Override
    public Integer next() {
        return ilit.next();
    }

    @Override
    public boolean hasNext() {
        return ilit.hasNext();
    }
}
