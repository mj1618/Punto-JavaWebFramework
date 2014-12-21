package punto.route;

import punto.Punto;
import punto.cms.CmsPage;
import punto.cms.ContentManager;
import punto.http.HttpContext;
import punto.mvc.Controller;

/**
 * Created by MattUpstairs on 14/09/2014.
 */
public interface PreProcessor {
    public void process(HttpContext ctx, Controller controller);

    public default ContentManager contentManager(){
        return ContentManager.instance(Punto.getSiteName());
    }

    public default CmsPage cms(String name){
        return contentManager().readPage(name);
    }

}
