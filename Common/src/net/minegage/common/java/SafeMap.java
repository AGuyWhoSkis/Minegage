package net.minegage.common.java;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Typesafe hashmap wrapper
 *
 * @param <K>
 *        Key type
 * @param <V>
 *        Value type
 */
public class SafeMap<K, V> {
	
	protected HashMap<K, V> map;
	
	public SafeMap() {
		map = new HashMap<K, V>();
	}
	
	public SafeMap(SafeMap<K, V> copy) {
		this();

		for (Entry<K, V> entry : copy.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
	}
	
	public void put(K key, V value) {
		map.put(key, value);
	}
	
	public V remove(K key) {
		return map.remove(key);
	}
	
	public V get(K key) {
		return map.get(key);
	}
	
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}
	
	public Set<K> keySet() {
		return map.keySet();
	}
	
	public Collection<V> values() {
		return map.values();
	}
	
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}
	
	public boolean containsValue(V value) {
		return map.containsValue(value);
	}
	
	public void clear() {
		map.clear();
	}
	
	public int size() {
		return map.size();
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	public V getOrDefault(K key, V value) {
		return map.getOrDefault(key, value);
	}
	
	public V putIfAbsent(K key, V value) {
		return map.putIfAbsent(key, value);
	}
	
	public Map<K, V> getWrappedMap() {
		return map;
	}
	
}
