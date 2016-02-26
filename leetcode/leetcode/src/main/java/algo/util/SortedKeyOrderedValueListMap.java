package algo.util;

import java.util.PriorityQueue;
import java.util.TreeMap;

//implements a key sorted map with value ordered
public class SortedKeyOrderedValueListMap<K, V> {
	
	private TreeMap<K, PriorityQueue<V>> tmpq = new TreeMap<K, PriorityQueue<V>>();
	
	public void put(K k, V v){
		if (!tmpq.containsKey(k)){
			PriorityQueue<V> pq = new PriorityQueue<V>();
			tmpq.put(k, pq);
		}
		tmpq.get(k).offer(v);
	}
	
	public V get(K k){
		if (tmpq.containsKey(k)){
			PriorityQueue<V> pq = tmpq.get(k);
			return pq.peek();
		}else{
			return null;
		}
	}
	
	public void removeTop(K k){
		if (tmpq.containsKey(k)){
			PriorityQueue<V> pq = tmpq.get(k);
			pq.remove();
			if (pq.isEmpty()){
				tmpq.remove(k);
			}
		}
	}
	
	public boolean isEmpty(){
		return tmpq.isEmpty();
	}
	
	public K lastKey(){
		return tmpq.lastKey();
	}

}
