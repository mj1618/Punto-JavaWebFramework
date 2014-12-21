package punto.http;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import punto.cache.SessionCacheManager;
import punto.cms.CmsPage;
import punto.cms.ContentManager;
import punto.data.Mybatis;
import punto.iam.User;
import punto.log.Log;
import punto.mvc.Controller;
import punto.mvc.Response;
import punto.route.RouteManager;
import punto.util.Resource;
import punto.util.Utils;

import javax.cache.Cache;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class HttpContext {
	HttpServletRequest request;
	HttpServletResponse response;
	HttpMethodType method;
	String path;
	AsyncContext actx;
	
	Map<String,Object> attributes = new HashMap<String,Object>();

    Map<String,String> parameters = new HashMap<String,String>();

    Map<String, List<String>> inputs = new HashMap<>();
    Map<String, FileItem> inputFiles = new HashMap<>();

    Cache sessionState;
    Cache sessionStore;
	Cache cache;

    Mybatis mybatis = new Mybatis();

	public HttpContext(HttpServletRequest request, HttpServletResponse response) {
		super();
		this.request = request;
		this.response = response;
		this.method = HttpMethodType.valueOf(request.getMethod());
		this.path = Utils.pathFromRequest(request);
		//Debug.test("sessioin:"+request.getSession().getId());

        sessionState = SessionCacheManager.getSessionState(request.getSession().getId());
        sessionStore = SessionCacheManager.getSessionStore(request.getSession().getId());

		cache = SessionCacheManager.getCache();

        try {
            parseFormInputs();
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }

    public <T> T mbMapper(Class<T> mapper){
        return mybatis.session().getMapper(mapper);
    }

    public Optional<String> header(String name){
        return Optional.ofNullable(request.getHeader(name));
    }

    public void parseFormInputs() throws FileUploadException {
        if(isMultipartForm()){
            Log.test("multipart form");
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    Log.test("form field:" + item.getFieldName());
                    if(inputs.containsKey(item.getFieldName())==false)
                        inputs.put(item.getFieldName(), new ArrayList<>());
                    inputs.get(item.getFieldName()).add(item.getString());
                } else {
                    Log.test("form file:" + item.getName());
                    if(item.getName().equals("")==false){
                        inputFiles.put(item.getFieldName(), item);
                    }
                }
            }

            Log.test("after write, inputs size:" + inputs.size());
        } else {
            request.getParameterMap().forEach((key,value)->{
                inputs.put(key, Arrays.asList(value));
            });
        }
    }

    public void setContentType(String type){
        response.setContentType(type);
    }

	public Optional<User> user(){
        if(sessionState.containsKey("user"))
            return Optional.of((User)sessionState.get("user"));
        else return Optional.empty();
	}
	
	public Cache session(){
		return sessionStore;
	}
	
	public Cache cache(){
		return SessionCacheManager.getCache();
	}
	
	public boolean isMultipartForm(){
        return ServletFileUpload.isMultipartContent(request);
    }


	public void putAttribute(String key, Object value){
		attributes.put(key, value);
	}
	
	
	public String getPath() {
		return path;
	}

	public boolean isLoggedIn(){
		return user().isPresent();
	}

	public void setPath(String path) {
		this.path = path;
	}


	public AsyncContext getActx() {
		return actx;
	}


	public void setActx(AsyncContext actx) {
		this.actx = actx;
	}


	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public <T> T input(String name, T clazz){
		T t = null;
		if(t instanceof String){
			return (T) request.getParameter(name);
		} else {
			throw new IllegalArgumentException("not doing anything but strings yet mate!");
		}	
	}

    public Map<String, List<String>> inputs(){
        return inputs;
    }

    public Map<String,FileItem> inputFiles(){
        return inputFiles;
    }

    public Optional<FileItem> inputFile(String name){
        return Optional.ofNullable(inputFiles.get(name));
    }

    public Optional<List<String>> inputList(String name){
        return Optional.ofNullable(inputs.get(name));
    }

	public Optional<String> input(String name){
        List<String> in = inputs.get(name);
        if(in==null)return Optional.empty();

        if(in.size()>1){
            Log.error("Warning: more than one input for parameter:" + name + ". Use inputList to get all elements");
        }
        return Optional.of(in.get(0));
	}
	
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

    public void putParameter(String k, String v){
        parameters.put(k,v);
    }

    public String getParameter(String k){
        return parameters.get(k);
    }

    public String parameter(String k){
        return getParameter(k);
    }

	
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	public HttpMethodType getMethod() {
		return method;
	}
	public void setMethod(HttpMethodType method) {
		this.method = method;
	}
	
	public void write(String s) throws IOException {
		//Debug.test("writing:"+s);
		response.getWriter().write(s);
	}
	public void redirect(String to) throws IOException {
		response.sendRedirect(to);
	}

	public void forward(String path) throws ServletException, IOException {
		RouteManager.forward(path,request, response);
		
	}
	public void forwardAsync(String path) throws ServletException, IOException {
		actx.dispatch(path);
	}
	
	public void jsp(String jspPath) throws ServletException, IOException {
		RouteManager.forward(jspPath, request, response);
	}
	
	public void file(String f){
		//Debug.test("file path:"+path);
//		if(path.startsWith("/www")==false){
//			path="/www"+path;
//		}
		//Debug.test("file path:"+path);

        try {
            response.setContentType(Files.probeContentType(Resource.web(f).absolutePath()));
            respondInputStream(Resource.web(f).inputStream());
        } catch (IOException e) {
            Log.error("Could not find " + Resource.web(f).absolutePath());
            try {
                write("Sorry, an error occurred");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
	}
    public void respondInputStream(InputStream is){
        try {
            IOUtils.copy(is, getResponse().getOutputStream());
        } catch (Exception e) {
            Log.error("Couldn't read from input stream");
        }
    }
    public void cms(ContentManager manager,String fileId){

        respondInputStream(manager.readFile(fileId));


    }

    public void destruct(){
        mybatis.close();
    }

	public void jspAsync(String jspPath) {
		actx.dispatch(jspPath);
		//actx.
		//actx.dispatch(request.getServletContext(), jspPath);
	}

	public AsyncContext createAsyncContext() {
		if(actx==null)actx = request.startAsync(request, response);
		return actx;
		
	}

	public void completeAsync() {
		actx.complete();
	}

	public void login(User u) {
		this.sessionState.put("user",u);
	}
    public void logout(){
        this.sessionState.remove("user");
    }
	static String REMEMBER_PATH="hitUrl";
	public void rememberPath() {
		session().put(REMEMBER_PATH, getFullPath());
	}
    public void rememberPath(String p) {
        session().put(REMEMBER_PATH, p);
    }
    public String getRememberedPath(){
        return (String)session().get(REMEMBER_PATH);
    }

    public String getFullPath(){
        StringBuffer url = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            url.append('?');
            url.append(queryString);
        }
        String requestURL = url.toString();
        return requestURL;
    }

    public String getRememberedPath(String alternative){

        String path = (String)session().get(REMEMBER_PATH);

        if(path==null)return alternative;
        else return path;
    }

    public Response returnPath(String alternative) {
        String path = (String) session().get(REMEMBER_PATH);
        //Debug.test("after login path:"+path);
        session().remove(REMEMBER_PATH);

        if(path==null)
            return Controller.redirect(alternative);
        else
            return Controller.redirect(path);
    }
	public Optional<Response> returnPath() {
		String path = (String) session().get(REMEMBER_PATH);
		//Debug.test("after login path:"+path);
		session().remove(REMEMBER_PATH);
		
		if(path==null)
			return Optional.empty();
		else
			return Optional.of(Controller.redirectAbsolute(path));
	}

    public void cmsPage(CmsPage home) {
        this.putAttribute("page",home);
    }
    public void cmsPage(String name, CmsPage page) {
        this.putAttribute(name,page);
    }

    public String getPathStrip(String s) {
        String path = getPath();
        if(path.startsWith(s))
            path=path.substring(s.length());
        return path;
    }

    public String getContextRoot() {
        if(header("CONTEXT_ROOT").isPresent()){
            return header("CONTEXT_ROOT").get();
        } else
            return RouteManager.GetServletContext().getContextPath();
    }
}
