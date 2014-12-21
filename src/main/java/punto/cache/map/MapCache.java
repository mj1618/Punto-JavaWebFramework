package punto.cache.map;

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

public class MapCache<K,V> implements Cache<K,V>{

	Map<K, V> map = new LRUMap<K,V>(1000);
	
	public MapCache(){}

	@Override
	public void clear() {
		map.clear();
	}

    @Override
    public Configuration<K, V> getConfiguration() {
        return null;
    }

    @Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public void deregisterCacheEntryListener(
			CacheEntryListenerConfiguration<K, V> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public V get(K key) {
		return map.get(key);
	}

	@Override
	public Map<K, V> getAll(Set<? extends K> arg0) {
		return null;
	}

	@Override
	public V getAndPut(K arg0, V arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V getAndRemove(K arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V getAndReplace(K arg0, V arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CacheManager getCacheManager() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public <C extends Configuration<K, V>> C getConfiguration(Class<C> arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T invoke(K arg0, EntryProcessor<K, V, T> arg1, Object... arg2)
			throws EntryProcessorException {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public <T> Map<K, T> invokeAll(Set<? extends K> ks, EntryProcessor<K, V, T> kvtEntryProcessor, Object... objects) {
        return null;
    }

//	@Override
//	public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> arg0,
//			EntryProcessor<K, V, T> arg1, Object... arg2) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<javax.cache.Cache.Entry<K, V>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadAll(Set<? extends K> arg0, boolean arg1,
			CompletionListener arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void put(K key, V val) {
		// TODO Auto-generated method stub
		map.put(key,val);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean putIfAbsent(K arg0, V arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerCacheEntryListener(
			CacheEntryListenerConfiguration<K, V> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean remove(K key) {
		return map.remove(key) != null;
	}

	@Override
	public boolean remove(K arg0, V arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAll(Set<? extends K> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean replace(K arg0, V arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean replace(K arg0, V arg1, V arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


}
