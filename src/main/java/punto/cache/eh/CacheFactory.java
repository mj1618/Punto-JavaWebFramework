package punto.cache.eh;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class CacheFactory{

	private final static String SESSION_CACHE_NAME="sessions";
	private final static String DISK_PATH=".";
	private static Cache sessionCache; 
	private static boolean hasInit=false;
	private final static CacheManager cacheManager = CacheManager.create();
	
	public static synchronized void init(){
		if(!hasInit){
			Cache cache = new Cache(SESSION_CACHE_NAME, 10000, MemoryStoreEvictionPolicy.LRU, true, DISK_PATH, false, 5, 2, true, 120, null); 
			cacheManager.addCache(sessionCache);
			sessionCache = cacheManager.getCache(SESSION_CACHE_NAME);
			hasInit=true;
		}
	}
	
	
	
}
