package org.cld.util;

import java.util.Map;

public class MapUtil<K, V> {
	public boolean mapEquals(Map<K, V> m1, Map<K, V> m2){
		if (m1.size() != m2.size())
		      return false;
	   for (K key: m1.keySet())
	      if (!m1.get(key).equals(m2.get(key)))
	         return false;
	   return true;
	}
}
