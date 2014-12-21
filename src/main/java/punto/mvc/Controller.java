package punto.mvc;

import punto.Punto;
import punto.cms.CmsPage;
import punto.cms.ContentManager;
import punto.log.Log;
import punto.http.HttpContext;
import punto.iam.Auth;
import punto.iam.Credential;
import punto.iam.User;
import punto.route.RouteManager;

import java.util.List;


public abstract class Controller{



	public Response jsp(String jspFile){
		return Response.jsp(jspFile);
	}
	public static Response vm(String vmFile){
		return Response.vm(vmFile);
	}
	
	public static Response redirect(String path){
		Log.test("redirecting:" + RouteManager.GetContextRoot() + path);
		return Response.redirect(RouteManager.GetContextRoot() + path);
	}

    public static Response redirectAbsolute(String path){
        Log.test("redirecting:" + path);
        return Response.redirect(path);
    }
    public static Response redirectRelative(HttpContext ctx,String path){
        Log.test("redirecting:" + ctx.getPath()+path);
        return Response.redirect(ctx.getPath()+path);
    }


    public static Response forward(String path){
        return Response.forward(path);
    }
    public ContentManager contentManager(){
        return ContentManager.instance(Punto.getSiteName());
    }

    public CmsPage cms(String name){
        try {
            return contentManager().readPage(name);
        }catch (Exception e){
            Log.error("Couldn't load cms page:" + name);
            return null;
        }
    }



    public Response json(Object o){
        return Response.json(o);
    }

	public boolean authenticate(HttpContext ctx, String username, String password){
         try{
             if(Auth.authenticate(new Credential(username, password))){
                 ctx.login(new User(username));
                 return true;
             } else {
                 return false;
             }
         } catch (Exception e){
             e.printStackTrace();
             return false;
         }
	}

    public List<String> cmsPages(){
        return contentManager().getPageNames();
    }
	
}

