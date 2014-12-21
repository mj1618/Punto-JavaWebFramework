package punto.jee;

import punto.http.HttpContext;
import punto.route.RouteManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PuntoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public final static String name="PuntoServlet";
       
    public PuntoServlet() {
        super();
    }

    
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        HttpContext ctx = new HttpContext(request,response);
        RouteManager.route(ctx.getPath(), ctx).respond(ctx);
    }

    public static void service(HttpContext ctx) throws ServletException, IOException{

        RouteManager.route(ctx.getPath(), ctx).respond(ctx);

    }


}
