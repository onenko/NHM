package net.nenko.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NanoHashMap<K, V> extends AbstractMap<K,V> implements Map<K,V> {

    private HashMap<K, V> map = new HashMap<>();

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return null;
    }

}
