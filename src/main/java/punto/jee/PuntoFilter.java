package punto.jee;

import punto.log.Log;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class PuntoFilter {
    
	static PuntoFilter instance = new PuntoFilter();
	
	public static void DoFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException, ServletException {
		instance.doFilter(srequest, sresponse, chain);
	}
	public PuntoFilter() {
		super();
	}

	private void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException, ServletException{

		if (srequest instanceof HttpServletRequest == false) { 
			Log.warning("Warning, request:" + srequest.getClass().getName() + " is not recognized");
		}
			
		//HttpContext ctx = new HttpContext((HttpServletRequest) srequest,(HttpServletResponse) sresponse);
		//boolean isFile = Utils.pathIsFile(ctx.getPath());
		
		switch(srequest.getDispatcherType()){
		case ERROR:
			Log.warning("Error request not implemented");
			break;
		case ASYNC:
			Log.warning("async request come through");
		case FORWARD:
		case INCLUDE:
		case REQUEST:
//			if(isFile)
//				Response.file(ctx.getPath()).respond(ctx);
//			else 
            srequest.getServletContext().getNamedDispatcher(PuntoServlet.name).forward(srequest, sresponse);
            //PuntoServlet.service(ctx);
			break;
		default:
			break;
		}
	}
	
    
}
