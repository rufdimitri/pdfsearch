package rd.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Concurrent {
    /**
     * Provides synchronized Map-Operations for given map
     * @param <K> Key
     * @param <V> Value
     */
    public static class SynchronizedMap<K,V> implements Map<K,V> {
        Map<K,V> map;

        public static <K,V> SynchronizedMap<K,V> of (Map<K,V> map) {
            SynchronizedMap<K,V> smap = new SynchronizedMap<>();
            smap.map = map;
            return smap;
        }

        private SynchronizedMap() {}

        @Override
        synchronized public int size() {
            return map.size();
        }

        @Override
        synchronized public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        synchronized public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        synchronized public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        @Override
        synchronized public V get(Object key) {
            return map.get(key);
        }

        @Override
        synchronized public V put(K key, V value) {
            return map.put(key, value);
        }

        @Override
        synchronized public V remove(Object key) {
            return map.remove(key);
        }

        @Override
        synchronized public void putAll(Map<? extends K, ? extends V> m) {
            map.putAll(m);
        }

        @Override
        synchronized public void clear() {
            map.clear();
        }

        @Override
        synchronized public Set<K> keySet() {
            return map.keySet();
        }

        @Override
        synchronized public Collection<V> values() {
            return map.values();
        }

        @Override
        synchronized public Set<Entry<K, V>> entrySet() {
            return map.entrySet();
        }

        @Override
        synchronized public V getOrDefault(Object key, V defaultValue) {
            return map.getOrDefault(key, defaultValue);
        }

        @Override
        synchronized public void forEach(BiConsumer<? super K, ? super V> action) {
            map.forEach(action);
        }

        @Override
        synchronized public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            map.replaceAll(function);
        }

        @Override
        synchronized public V putIfAbsent(K key, V value) {
            return map.putIfAbsent(key, value);
        }

        @Override
        synchronized public boolean remove(Object key, Object value) {
            return map.remove(key, value);
        }

        @Override
        synchronized public boolean replace(K key, V oldValue, V newValue) {
            return map.replace(key, oldValue, newValue);
        }

        @Override
        synchronized public V replace(K key, V value) {
            return map.replace(key, value);
        }

        @Override
        synchronized public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            return map.computeIfAbsent(key, mappingFunction);
        }

        @Override
        synchronized public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return map.computeIfPresent(key, remappingFunction);
        }

        @Override
        synchronized public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return map.compute(key, remappingFunction);
        }

        @Override
        synchronized public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            return map.merge(key, value, remappingFunction);
        }
    }
}
