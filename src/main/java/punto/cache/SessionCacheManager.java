package punto.cache;


import punto.cache.map.MapCacheManager;

import javax.cache.Cache;
import javax.cache.CacheManager;

public class SessionCacheManager {

	private final static String COMMON_CACHE_NAME="common";
    private final static String SESSION_STATE_PREFIX="sessionState:";
    private final static String SESSION_STORE_PREFIX="sessionStore:";

    static String siteName;
	static CacheManager cacheManager;
	
	public static void init(String siteName){
        SessionCacheManager.siteName=siteName;
        cacheManager = new MapCacheManager();//siteName);
	}
	
	public static Cache getSessionState(String sessionId){
		return cacheManager.getCache(SESSION_STATE_PREFIX+sessionId);
	}
    public static Cache getSessionStore(String sessionId){
        return cacheManager.getCache(SESSION_STORE_PREFIX+sessionId);
    }
	public static Cache getCache() {
		return cacheManager.getCache(COMMON_CACHE_NAME);
	}

    public static void killSession(String id) {
        cacheManager.destroyCache(SESSION_STATE_PREFIX+id);
        cacheManager.destroyCache(SESSION_STORE_PREFIX+id);

    }
}
