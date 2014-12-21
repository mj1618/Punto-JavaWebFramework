package punto.cache.mongo;


import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

/**
 * Created by MattUpstairs on 22/09/2014.
 */
public class MongoCacheManager implements javax.cache.CacheManager{

    MongoCacheStore store;

    public MongoCacheManager(String siteName){
        store = new MongoCacheStore(siteName);
    }

    @Override
    public void close() {
        throw new RuntimeException("this method is not implemented yet");
    }


    @Override
    public <K, V> Cache<K, V> createCache(
            String name, Configuration<K,V> arg1) throws IllegalArgumentException {
        return getCache(name);
    }

    @Override
    public void destroyCache(String name) {
        store.deleteCache(name);
    }

    @Override
    public void enableManagement(String arg0, boolean arg1) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public void enableStatistics(String arg0, boolean arg1) {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        store.ensureCache(name);
        return new MongoCache(store, name);
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name, Class<K> arg1, Class<V> arg2) {
        return getCache(name);
    }



    @Override
    public Iterable<String> getCacheNames() {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public CachingProvider getCachingProvider() {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public Properties getProperties() {
        throw new RuntimeException("this method is not implemented yet");
    }


    @Override
    public URI getURI() {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public boolean isClosed() {
        throw new RuntimeException("this method is not implemented yet");
    }

    @Override
    public <T> T unwrap(Class<T> arg0) {
        throw new RuntimeException("this method is not implemented yet");
    }
}
