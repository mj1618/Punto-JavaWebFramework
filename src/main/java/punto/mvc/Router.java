package punto.mvc;

import punto.Punto;
import punto.log.Log;
import punto.http.HttpContext;
import punto.route.Route;
import punto.route.RouteManager;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public abstract class Router {
	
	public abstract void makeRoutes(Route route);

    public void setPageNotFound(BaseController c){
        RouteManager.SetPageNotFound(c);
    }

	public ControllerMethod method(Class<? extends Controller> c, final String method){
		
		Optional<Method> meth = Arrays.stream(c.getMethods())
			.filter(m -> m.getName().equals(method))
			.filter(m -> m.getParameters().length==1 && m.getParameters()[0].getType().equals(HttpContext.class))
			.findFirst();
		
		if(meth.isPresent()) {
			//Debug.test("Method:"+meth.get().getClass().getName());
			return new ControllerMethod(meth.get(),c);
		}
		else throw new IllegalArgumentException("Couldn't find method. Class:"+c.getName()+" Method:"+method+" Parameters: (punto.mvc.HttpContext)");
		
	}
	
	public Response redirect(String path){
		Log.test("redirecting:" + RouteManager.GetContextRoot() + path);
		return Response.redirect(RouteManager.GetContextRoot()+path);
	}
	
	public Response cmsFile(String fileId){
        return Response.cmsFile(Punto.getSiteName(), fileId);
    }
	public Response forward(String path, HttpContext ctx){
		return RouteManager.route(path, ctx);
	}
	
	public Response fileContent(String path){
		return Response.file(path);
	}
	public Response fileContent(String prefix,String path){

        return Response.file(prefix+path);
	}
}
