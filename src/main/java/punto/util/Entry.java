package punto.util;

import java.util.Map;

/**
 * Created by MattUpstairs on 10/09/2014.
 */
public class Entry<K,V> implements Map.Entry<K,V>{

    private final K key;
    private V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}
