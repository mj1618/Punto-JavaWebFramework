package punto.jee;

import punto.cache.SessionCacheManager;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by MattUpstairs on 22/09/2014.
 */
public class SessionListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent sesh) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sesh) {
        SessionCacheManager.killSession(sesh.getSession().getId());
    }
}
