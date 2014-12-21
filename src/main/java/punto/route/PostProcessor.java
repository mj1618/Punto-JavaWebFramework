package punto.route;

import punto.http.HttpContext;
import punto.mvc.Controller;
import punto.mvc.Response;

/**
 * Created by MattUpstairs on 14/09/2014.
 */
public interface PostProcessor {
    public void process(HttpContext ctx, Controller controller, Response response);

}
