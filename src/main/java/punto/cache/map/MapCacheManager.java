package punto.cache.map;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

public class MapCacheManager implements javax.cache.CacheManager{

	Map<String,Cache> caches = new LRUMap<String,Cache>(1000);
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <K, V> Cache<K, V> createCache(
			String name, Configuration<K,V> arg1) throws IllegalArgumentException {
		MapCache<K,V> cache = new MapCache<K,V>();
		caches.put(name, cache);
		return cache;
	}

	@Override
	public void destroyCache(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableManagement(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableStatistics(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <K, V> Cache<K, V> getCache(String c) {
		if(caches.containsKey(c)==false)createCache(c, null);
        return caches.get(c);
	}

	@Override
	public <K, V> Cache<K, V> getCache(String name, Class<K> arg1, Class<V> arg2) {
		return caches.get(name);
	}

	@Override
	public Iterable<String> getCacheNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CachingProvider getCachingProvider() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public ClassLoader getClassLoader() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}


    @Override
	public URI getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
