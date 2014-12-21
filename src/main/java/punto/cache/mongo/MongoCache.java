package punto.cache.mongo;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by MattUpstairs on 22/09/2014.
 */
public class MongoCache<K,V> implements Cache<K,V> {

    String sessionId;
    MongoCacheStore store;


    public MongoCache(MongoCacheStore store, String sessionId){
        this.store=store;
        this.sessionId=sessionId;
    }

    @Override
    public void clear() {
        store.clearCache(sessionId);
    }

    @Override
    public Configuration<K, V> getConfiguration() {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public void close() {
        throw new RuntimeException("this method is not implemented yet");

    }

    @Override
    public boolean containsKey(K key) {
        return store.containsKey(sessionId,(String)key);
    }

    @Override
    public void deregisterCacheEntryListener(
            CacheEntryListenerConfiguration<K, V> arg0) {
        throw new RuntimeException("this method is not implemented yet");

    }

    @Override
    public V get(K key) {
        return (V)store.getValue(sessionId,(String)key);
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> arg0) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public V getAndPut(K arg0, V arg1) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public V getAndRemove(K arg0) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public V getAndReplace(K arg0, V arg1) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public CacheManager getCacheManager() {
        throw new RuntimeException("this method is not implemented yet");
    }


    @Override
    public String getName() {
        return this.sessionId;
    }

    @Override
    public <T> T invoke(K arg0, EntryProcessor<K, V, T> arg1, Object... arg2)
            throws EntryProcessorException {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public <T> Map<K, T> invokeAll(Set<? extends K> ks, EntryProcessor<K, V, T> kvtEntryProcessor, Object... objects) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public boolean isClosed() {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return store.getCacheDO(this.sessionId).cache.entrySet().iterator();
    }

    @Override
    public void loadAll(Set<? extends K> arg0, boolean arg1,
                        CompletionListener arg2) {
        throw new RuntimeException("this method is not implemented yet");

    }

    @Override
    public void put(K key, V val) {
        store.putValue(sessionId,(String)key, val.toString());
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> arg0) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public boolean putIfAbsent(K arg0, V arg1) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public void registerCacheEntryListener(
            CacheEntryListenerConfiguration<K, V> arg0) {
        throw new RuntimeException("this method is not implemented yet");

    }

    @Override
    public boolean remove(K key) {
        store.removeValue(sessionId, (String)key);
        return true;
    }

    @Override
    public boolean remove(K arg0, V arg1) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public void removeAll() {
        throw new RuntimeException("this method is not implemented yet");

    }

    @Override
    public void removeAll(Set<? extends K> arg0) {
        throw new RuntimeException("this method is not implemented yet");

    }

    @Override
    public boolean replace(K arg0, V arg1) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public boolean replace(K arg0, V arg1, V arg2) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public <T> T unwrap(Class<T> arg0) {
        throw new RuntimeException("this method is not implemented yet");
    }


}
