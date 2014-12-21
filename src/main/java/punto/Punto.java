package punto;

import org.reflections.Reflections;
import punto.cache.SessionCacheManager;
import punto.log.Log;
import punto.mvc.Initiator;
import punto.mvc.SiteInitiator;
import punto.route.RouteManager;

import javax.servlet.ServletContext;

public class Punto {

    public static String HASH_SALT="wp4998wyb4yu5nvsh";
    public static String siteName;
    public static String googlePass = System.getProperty("punto.googlePassword");
    public static String logFile = System.getProperty("punto.logFile");

    public static void Init(ServletContext servletContext) {

        new Reflections("").getSubTypesOf(SiteInitiator.class).forEach(site -> {
            try {
                Punto.siteName = site.newInstance().getSiteName();
//                ContentManager.instance(site.newInstance().getSiteName());
            } catch (InstantiationException e) {
                Log.error("Could not instantiate initiator class: " + site.getName());
            } catch (IllegalAccessException e) {
                Log.error("Could not access initiator class: " + site.getName());
            }
        });

        SessionCacheManager.init(siteName);
		RouteManager.Init(servletContext);
//		IamManager.init();

		new Reflections("").getSubTypesOf(Initiator.class).forEach(ic -> {
            try {
                ic.newInstance().init();
            } catch (InstantiationException e) {
                Log.error("Could not instantiate initiator class: " + ic.getName());
            } catch (IllegalAccessException e) {
                Log.error("Could not access initiator class: " + ic.getName());
            }
        });


	}


    public static String getSiteName(){
        return siteName;
    }


    public static boolean dropdata() {

        String drop = System.getProperty("punto.dropdata");
        return drop!=null&&drop.equals("true");
    }
}
