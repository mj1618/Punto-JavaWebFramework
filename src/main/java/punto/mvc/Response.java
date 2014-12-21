package punto.mvc;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import punto.cms.ContentManager;
import punto.http.HttpContext;
import punto.log.Log;
import punto.type.ResponseType;
import punto.util.Resource;
import punto.util.Utils;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Response {
    public static Response file(String filename){
        return new Response(ResponseType.FILE,filename);
    }
	public static Response text(String s){
		return new Response(ResponseType.TEXT,s);
	}
	public static Response jsp(String jsp){
		return new Response(ResponseType.JSP,jsp);
	}
	public static Response json(Object o){
		return new Response(ResponseType.JSON,Utils.toJson(o));
	}
	public static Response error(Object o){
		return new Response(ResponseType.ERROR);
	}
	public static Response redirect(String to){
		return new Response(ResponseType.REDIRECT, to);
	}
    public static Response forward(String to){
        return new Response(ResponseType.FORWARD, to);
    }
	public static Response login(){
		return new Response(ResponseType.LOGIN);
	}
    public static Response cmsFile(String siteName, String fileId){
        return new Response(ResponseType.CMS, siteName, fileId);
    }

	public static Response vm(String vmFile) {
		return new Response(ResponseType.VM,vmFile);
	}
	private Response(ResponseType type){
		this.type=type;
	}

    ContentManager contentManager;
    String fileId;
    String forwardTo;

    private Response(ResponseType type, String s, String fileId) {
        this.type = type;
        switch (type) {
            case CMS:
                this.contentManager=ContentManager.instance(s);
                this.fileId = fileId;
                break;
            default:
                Log.error("Error in Response constructor, type:" + type.toString());
                break;
        }
    }

//	private Response(Object o, ResponseType type){
//		this.json = Utils.toJson(o);
//		this.type=type;
//	}
	private Response(ResponseType type, String s){
		this.type=type;
		switch(type){
        case JSON:
            this.json=s;
            break;
		case TEXT:
			this.html=s;
			break;
		case JSP:
			this.jspPath=s;
			break;
		case VM:
			this.vmFile=s;
			break;
		case REDIRECT:
			this.redirectTo=s;
			break;
        case FORWARD:
            this.forwardTo=s;
            break;
		case FILE:
			this.filename = s;
			break;
        case CMS:
            this.contentManager=ContentManager.instance(s);
            break;
		default:
			Log.error("Error in Response constructor, type:" + type.toString());
			break;
		}
	}

	private String redirectTo;
	private String html;
	private String jspPath;
	private String json;
	private ResponseType type;
	private String vmFile;
	private String filename;

	public String getRedirectTo() {
		return redirectTo;
	}
	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
	private Response(){}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getJspPath() {
		return jspPath;
	}

	public void setJspPath(String jspPath) {
		this.jspPath = jspPath;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public ResponseType getType() {
		return type;
	}

	public void setType(ResponseType type) {
		this.type = type;
	}
	public void respond(HttpContext ctx) throws IOException, ServletException {
		switch(getType()){
		case VM:
			writeVM(ctx);
			break;
		case TEXT:
			ctx.write(getHtml());
			break;
		case JSON:
            ctx.setContentType("application/json");
			ctx.write(getJson());
			break;
		case REDIRECT:
			ctx.redirect(getRedirectTo());
			break;
        case FORWARD:
            ctx.forward(forwardTo);
            break;
		case JSP:
			ctx.jsp(getJspPath());
			break;
		case FILE:
			ctx.file(filename);
			break;
        case CMS:
            ctx.cms(contentManager,fileId);
            break;
		default:
			break;
		}
        ctx.destruct();
	}

    VelocityEngine engine;

    public static List<String> libraries = new ArrayList<>();

	public VelocityEngine vEngine() throws MalformedURLException {
        if(engine!=null)return engine;

        engine = new VelocityEngine();

        Log.test("FULL PATH:" + Resource.absolute("/"));

        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        engine.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
        engine.setProperty("file.resource.loader.path", Resource.webPath());// PuntoFilter.REDIRECT_VMS(""));//);
        engine.setProperty("eventhandler.include.class", "org.apache.velocity.app.event.implement.IncludeRelativePath");
        engine.setProperty("velocimacro.library.autoreload", "true");



        engine.setProperty("velocimacro.library",  StringUtils.join(libraries, ","));

        engine.init();
        return engine;
    }

	private void writeVM(HttpContext ctx) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, IOException {
		

        Template t = vEngine().getTemplate( vmFile );
        VelocityContext context = new VelocityContext(ctx.getAttributes());

        t.merge( context, ctx.getResponse().getWriter() );
	}
	
	public void respondAsync(HttpContext ctx) throws IOException, ServletException {
		switch(getType()){
		case TEXT:
			ctx.write(getHtml());
			ctx.completeAsync();
			break;
		case JSON:
			ctx.write(getJson());
			ctx.completeAsync();
			break;
		case REDIRECT:
			ctx.redirect(getRedirectTo());
			ctx.completeAsync();
			break;
		case JSP:
			ctx.jspAsync(getJspPath());
			break;
		case FILE:
			ctx.file(filename);
		default:
			break;
		}
		ctx.destruct();
	}


    public static Response errorMessage() {
        return Response.text("Sorry, an error occurred");
    }
}
