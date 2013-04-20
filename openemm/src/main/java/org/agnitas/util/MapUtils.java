package org.agnitas.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

public class MapUtils {
	public static <K extends Comparable<K>, V> LinkedHashMap<K, V> sort(Map<K, V> map) {
		LinkedHashMap<K, V> returnMap = new LinkedHashMap<K, V>();
		List<K> keys = new LinkedList<K>(map.keySet());
		Collections.sort(keys);
		for (K key : keys) {
			returnMap.put(key, map.get(key));
		}
		return returnMap;
	}

	public static <K, V> LinkedHashMap<K, V> sort(Map<K, V> map, Comparator<K> comparator) {
		LinkedHashMap<K, V> returnMap = new LinkedHashMap<K, V>();
		List<K> keys = new LinkedList<K>(map.keySet());
		Collections.sort(keys, comparator);
		for (K key : keys) {
			returnMap.put(key, map.get(key));
		}
		return returnMap;
	}
	
	public static <K, V extends Comparable<V>> LinkedHashMap<K, V> sortByValues(Map<K, V> map) {
		LinkedHashMap<K, V> returnMap = new LinkedHashMap<K, V>();
		List<V> values = new LinkedList<V>(map.values());
		Collections.sort(values);
		for (V value : values) {
			for (Entry<K, V> entry : map.entrySet()) {
				if (value == entry.getValue()) {
					returnMap.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return returnMap;
	}
	
	public static <K, V> LinkedHashMap<K, V> sortEntries(Map<K, V> map, Comparator<Entry<K, V>> comparator) {
		LinkedHashMap<K, V> returnMap = new LinkedHashMap<K, V>();
		List<Entry<K, V>> entries = new LinkedList<Entry<K, V>>(map.entrySet());
		Collections.sort(entries, comparator);
		for (Entry<K, V> entry : entries) {
			returnMap.put(entry.getKey(), entry.getValue());
		}
		return returnMap;
	}

	public static <K extends Comparable<K>, V, L extends LinkedHashMap<K, V>> L sort(L map) {
         @SuppressWarnings("unchecked")
         L returnMap = (L) map.clone();
         returnMap.clear();
         List<K> keys = new LinkedList<K>(map.keySet());
         Collections.sort(keys);
         for (K key : keys) {
             returnMap.put(key, map.get(key));
         }
         return returnMap;
     }

     public static <K, V, L extends LinkedHashMap<K, V>> L sort(L map, Comparator<K> comparator) {
         @SuppressWarnings("unchecked")
         L returnMap = (L) map.clone();
         returnMap.clear();
         List<K> keys = new LinkedList<K>(map.keySet());
         Collections.sort(keys, comparator);
         for (K key : keys) {
             returnMap.put(key, map.get(key));
         }
         return returnMap;
     }

     public static <K, V, L extends LinkedHashMap<K, V>> L sortEntries(L map, Comparator<Entry<K, V>> comparator) {
         @SuppressWarnings("unchecked")
         L returnMap = (L) map.clone();
         returnMap.clear();
         List<Entry<K, V>> entries = new LinkedList<Entry<K, V>>(map.entrySet());
         Collections.sort(entries, comparator);
         for (Entry<K, V> entry : entries) {
             returnMap.put(entry.getKey(), entry.getValue());
         }
         return returnMap;
     }

     public static <K, V, L extends HashMap<K, V>> L filterEntries(L map, Predicate<Entry<K, V>> predicate) {
         @SuppressWarnings("unchecked")
         L returnMap = (L) map.clone();
         returnMap.clear();
         for (Entry<K, V> entry : map.entrySet()) {
        	 if (predicate.evaluate(entry))
        		 returnMap.put(entry.getKey(), entry.getValue());
         }
         return returnMap;
     }
     
     /**
      * This method sums up all the keys inside a map which refer to the same value.
      * The keys are stored in a List for each found value.
      * 
      * @param map
      * @return
      */
     public static <K, V> Map<V, List<K>> groupKeysByValue(Map<K, V> map) {
    	 Map<V, List<K>> returnMap = new HashMap<V, List<K>>();
    	 for (Entry<K, V> entry : map.entrySet()) {
    		 if (!returnMap.containsKey(entry.getValue())) {
    			 returnMap.put(entry.getValue(), new ArrayList<K>());
    		 }
    		 
    		 returnMap.get(entry.getValue()).add(entry.getKey());
    	 }
    	 return returnMap;
     }
     
     /**
      * This method sums up all the String keys inside a map which refer to the same value.
      * The keys are sorted, concatenated and used as new keys in the resulting map.
      * 
      * @param map
      * @param separator
      * @return
      */
     public static <V> Map<String, V> joinStringKeysByValue(Map<String, V> map, String separator) {
    	 Map<V, List<String>> groupedMap = groupKeysByValue(map);
    	 Map<String, V> returnMap = new HashMap<String, V>();
    	 for (Entry<V, List<String>> entry : groupedMap.entrySet()) {
    		 // Sort Listentries caseinsensitive
    		 Collections.sort(entry.getValue(), String.CASE_INSENSITIVE_ORDER);
    		 returnMap.put(StringUtils.join(entry.getValue(), separator), entry.getKey());
    	 }
    	 return returnMap;
     }
}
