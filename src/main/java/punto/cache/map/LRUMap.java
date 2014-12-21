package punto.cache.map;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by MattUpstairs on 18/11/2014.
 */
public class LRUMap < K, V > extends LinkedHashMap< K, V > {

    private int capacity; // Maximum number of items in the cache.

    public LRUMap(int capacity) {
        super(capacity+1, 1.0f, true); // Pass 'true' for accessOrder.
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry entry) {
        return (size() > this.capacity);
    }
}