package punto.route;

import org.reflections.Reflections;
import punto.log.Log;
import punto.http.HttpContext;
import punto.http.HttpMethodType;
import punto.mvc.BaseController;
import punto.mvc.Response;
import punto.mvc.Router;
import punto.util.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class RouteManager {
	static final String ROUTE_FILE="routes";
	static RouteManager instance;
	List<IndividualRoute> routes = new ArrayList<IndividualRoute>();
	private ServletContext context;
    private BaseController pageNotFound;

    public BaseController getPageNotFound() {
        return pageNotFound;
    }

    public static void SetPageNotFound(BaseController c){
        instance.setPageNotFound(c);
    }

    public void setPageNotFound(BaseController c){
        this.pageNotFound=c;
    }

    public static Collection<Class<? extends Router>> getAllRouters(){

        Reflections refl = new Reflections("");

        Set<Class<? extends Router>> rs = refl.getSubTypesOf(Router.class);
        rs.forEach(r -> {
            Log.test(r.getName());
        });

//        List<Class<? extends Router>> rs = new ArrayList<>();
//        rs.add(Parkour.class);
//        rs.add(Manage.class);
        return rs;
    }

	public static void Init(ServletContext servletContext) {
		Log.test("initting routemanager");
		instance = new RouteManager();
		instance.init(servletContext);
	}
	

	private void init(ServletContext context){
		this.context = context;
		try {
			loadRoutes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static URL getPathUrl(String file){
		try {
			return GetServletContext().getResource(file);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	private void loadRoutes() throws IOException{
		//Optional<Path> path = Utils.fullPathOfFile(ROUTE_FILE);
        getAllRouters().stream().map( routerClass -> {
					try {
						Route route = new Route();
						Router router = routerClass.newInstance();
						router.makeRoutes(route);
						return Optional.of(route);
					} catch (Exception e) {
						e.printStackTrace();
						return Optional.empty();
					}
				})
				.map(opt -> { 
					if(opt.isPresent()) return Optional.of(expandRoutes((Route) opt.get()));
					else return opt;
				})
				.forEach(opt -> {
					if(opt.isPresent())
						this.routes.addAll((List<IndividualRoute>) opt.get());	
				});
        this.routes.forEach(route -> Log.test("route: " + route.getPath()));
	}
	
	public List<IndividualRoute> expandRoutes(Route route){
		List<IndividualRoute> routes = new ArrayList<IndividualRoute>();
		
		Stack<Route> stack = new Stack<Route>();
		stack.add(route);
		
		while(stack.size()>0){
			
			Route r = stack.pop();
			
			for(IndividualRoute subRoute : r.cascadeGetSubRoutes())
				routes.add(subRoute);
			
			for(Route groupRoute : r.cascadeGetGroupRoutes())
					stack.push(groupRoute);
		}
		
		return routes;
	}
//	public static File[] getPackageContent(String packageName) throws IOException{
//	    ArrayList<File> list = new ArrayList<File>();
//	    Enumeration<URL> urls = Thread.currentThread().getContextClassLoader()
//	                            .getResources(packageName);
//	    while (urls.hasMoreElements()) {
//	        URL url = urls.nextElement();
//	        File dir = new File(url.getFile());
//	        Debug.test(""+dir+" "+dir.listFiles().length);
//	        for (File f : dir.listFiles()) {
//	            list.add(f);
//	            Debug.test(""+f);
//	        }
//	    }
//	    return list.toArray(new File[]{});
//	}
//	


	public static void forward(String path,HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		GetServletContext().getRequestDispatcher(path).forward(request,response);
	}
	
//	public static Response Invoke(IndividualRoute route, List<Object> params) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		try {
//			route.invoke(GetControllerObject(route), params.toArray());
//			//return (Response)route.getMethod().invoke(GetControllerObject(route), params.toArray());
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	private static Object GetControllerObject(IndividualRoute route) throws InstantiationException, IllegalAccessException {
		return GetInstance().getControllerObject(route);
	}


	private Object getControllerObject(IndividualRoute route) throws InstantiationException, IllegalAccessException {
		return route.createObject();
	}
	
	public static Response Handball(IndividualRoute route, HttpContext ctx, String path) {
		return GetInstance().handball(route, ctx, path);
	}
	
	public static void HandballAsync(IndividualRoute route, HttpContext ctx, String path) {
		GetInstance().handballAsync(route, ctx, path);
	}
	
	private void handballAsync(IndividualRoute route, HttpContext ctx,
			String path) {
		respondAsync(route,ctx,path);
	}

	public Response handball(IndividualRoute route, HttpContext ctx, String path) {

        parameters(route, ctx, path);

		for(Filter f:route.getFilters()){
			if(f.check(ctx)){
				return f.action(ctx);
			}
		}
		 return respondSync(route,ctx,path);
	}

    private void parameters(IndividualRoute route, HttpContext ctx, String path) {

        Utils.parameters(route.getPath(), path).forEach(param -> ctx.putParameter(param.getKey(),param.getValue()));

    }

    private void respondAsync(IndividualRoute route, final HttpContext ctx, final String path){
		
		ctx.createAsyncContext().start( () -> {
			try {
				Log.test("responding async");
				Response r = invoke(route, ctx);
				r.respondAsync(ctx);
			} catch (IllegalArgumentException | InvocationTargetException | 
					IOException | IllegalAccessException | 
					ServletException | InstantiationException e) {
				e.printStackTrace();
				return;
			}
		});
	}
	
	private Response respondSync(IndividualRoute route, HttpContext ctx, String path){
		
		try {
			return invoke(route, ctx);
			//r.respond(ctx);
		} catch (IllegalArgumentException
				| IllegalAccessException | 
				InvocationTargetException | InstantiationException e) {
            Log.error("error invoking sync response", e);
            return Response.text("Sorry, an error occurred.");
		}
	}
	
	private Response invoke(IndividualRoute route, HttpContext ctx) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		return route.invoke(ctx);
	}
	

	public static List<IndividualRoute> MatchingRoutes(final String path, final HttpMethodType method) {
		return GetRoutes()
				.stream()
				.filter(r -> r.getHttpMethod().equals(method))
				.filter(r -> r.matches(path))
				.collect(Collectors.toList());
	}
	
	
	
	private static List<IndividualRoute> GetRoutes() {
		return GetInstance().getRoutes();
	}
	public List<IndividualRoute> getRoutes() {
		return routes;
	}
	public void setRoutes(List<IndividualRoute> routes) {
		this.routes = routes;
	}
	public static ServletContext GetServletContext() {
		return GetInstance().getServletContext();
	}
	private ServletContext getServletContext() {
		return context;
	}


	private static RouteManager GetInstance() {
		return instance;
	}

	public static Response route(String path, HttpContext ctx) {
		List<IndividualRoute> matches = RouteManager.MatchingRoutes(path,ctx.getMethod());
    	
    	switch(matches.size()){
    	case 1:
    		IndividualRoute route = matches.get(0);
    		return RouteManager.Handball(route,ctx, path);
    	case 0:
    		Log.error("Warning, no servlets match path. Path=\'" + path + "\'");
            if(RouteManager.instance().getPageNotFound()!=null)
                return RouteManager.instance().getPageNotFound().route(ctx);
            break;
    	default:
            Log.error("Error, more than one servlet matches path. Path=\'" + path + "\', Matching Servlets:" );
            for(IndividualRoute r : matches){
                Log.error(r.getPath());
            }
            break;
    	}
        return Response.error("Sorry an error occurred");
	}

    private static RouteManager instance() {return instance;
    }


    public static String GetContextRoot() {
		return GetServletContext().getContextPath();
	}


	
//	
//	protected Reflections reflections;
//	protected List<Servlet> classlets = new ArrayList<Servlet>();
//	protected List<Route> routes = new ArrayList<Route>();
//	protected List<Object> servlets = new ArrayList<Object>();
//	boolean hasInit = false;
//	
//	static ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
//	
//	public static ScheduledExecutorService GetExecutor() {
//		return executor;
//	}
//
//	public static void forwardToServlet(HttpServletRequest request,
//			HttpServletResponse response) throws ServletException, IOException {
//		GetServletContext().getNamedDispatcher(TrafficDirector.name).forward(request, response);
//	}
//	public List<Object> getServlets() {
//		return servlets;
//	}
//	public void setServlets(List<Object> servlets) {
//		this.servlets = servlets;
//	}
//	private static ServletManager instance;
//	
//	private static ServletManager GetInstance(){
//		if(instance==null)instance = new ServletManager();
//		return instance;
//	}
//	private void init(ServletContext servletContext){
//		setServletContext(servletContext);
//		initRoutes();
//	}
//	
//	private void initRoutes
//
//	public static List<Servlet> GetClasslets() {
//		return GetInstance().getClasslets();
//	}
//	public List<Servlet> getClasslets() {
//		return classlets;
//	}
//	public void setClasslets(List<Servlet> classlets) {
//		this.classlets = classlets;
//	}
//
//	public Object getServlet(String className){
//		for(Object servlet:servlets){
//			if(servlet.getClass().getName().equals(className))
//				return servlet;
//		}
//		return null;
//	}
//	public static Object GetServlet(String className){
//		Debug.test("getservlet:"+className);
//		for(Object s:GetServlets()){
//			Debug.test("servlet:"+s.getClass().getName());
//		}
//		return GetInstance().getServlet(className);
//	}
//	
//	private static List<Object> GetServlets() {
//		return GetInstance().getServlets();
//	}
//	
//	public static void UnInit(){
//
//		ServletManager.GetInstance().clear();
//	}
//	
//	private void clear(){
//
//		classlets.clear();
//		routes.clear();
//		servlets.clear();
//		hasInit=false;
//	}
//	
////	private void initRegistrations(){
////		if(hasInit)return;
////		hasInit=true;
////		
////		
////		
////		Set<Class<?>> subTypes = 
////				ServletManager.GetReflections().getTypesAnnotatedWith(MwfServlet.class);
//////		Set<Class<? extends Controller>> subTypes = 
//////				ServletManager.GetReflections().getSubTypesOf(Controller.class);
////		
////		Debug.test("reflections found:"+subTypes.size());
////		
////		for(Class<?> c : subTypes){
////			Object servlet;
////			try {
////				servlet = c.newInstance();
////			} catch (InstantiationException | IllegalAccessException e) {
////				e.printStackTrace();
////				continue;
////			}
////			servlets.add(servlet);
////			
////			Debug.test("adding subtype:"+c.getName());
////			String path="";
////			
////			if(c.isAnnotationPresent(PATH.class)){
////				PATH root = (PATH) c.getAnnotation(PATH.class);
////				if(root!=null) path = MwfUtils.CreatePath(root.path());
////			} else if(c.isAnnotationPresent(MwfServlet.class)){
////				MwfServlet root = (MwfServlet) c.getAnnotation(MwfServlet.class);
////				if(root!=null && root.path()!=null) path = MwfUtils.CreatePath(root.path());
////			}
////			
////			if(path.endsWith("/"))
////				path = path.substring(0, path.length()-1);
////			Debug.test("path:"+path);
////			
////			Servlet details = new Servlet();
////			details.setPath(path);
////			details.setServletClass(c);
////			//details.setServletName();
////			
////			classlets.add(details);
////		
////			
////			addAllMethods(details);
////		}		
////	}
//	
////	private void addAllMethods(Servlet details) {
////		Class<?> c = details.getServletClass();
////		String path = details.getPath();
////		
////		for(Method m : details.getServletClass().getMethods()){
////			Debug.test("doing method:"+m.getName());
////			for(Annotation a : m.getAnnotations()){
////				
////				Debug.test("has annotation:"+a.toString());
////				Debug.test("is instance of GET");
////				
////				Methlet detail = new Methlet();
////				detail.setMethod(m);
////				detail.setServletClassName(c.getName());
////				
////				if(a instanceof GET){
////					detail.setHttpMethod(HttpMethodType.GET);
////					detail.setPath(path+((GET) a).path());
////					detail.setAsync(((GET) a).async());
////				} else if (a instanceof POST){
////					detail.setHttpMethod(HttpMethodType.POST);
////					detail.setPath(path+((POST) a).path());
////				} else if (a instanceof PUT){
////					detail.setHttpMethod(HttpMethodType.PUT);
////					detail.setPath(path+((PUT) a).path());
////				} else if (a instanceof DELETE){
////					detail.setHttpMethod(HttpMethodType.DELETE);
////					detail.setPath(path+((DELETE) a).path());
////				} else if (a instanceof CONNECT){
////					detail.setHttpMethod(HttpMethodType.CONNECT);
////					detail.setPath(path+((CONNECT) a).path());
////				} else if (a instanceof OPTIONS){
////					detail.setHttpMethod(HttpMethodType.OPTIONS);
////					detail.setPath(path+((OPTIONS) a).path());
////				} else if (a instanceof TRACE){
////					detail.setHttpMethod(HttpMethodType.TRACE);
////					detail.setPath(path+((TRACE) a).path());
////				} else if (a instanceof HEAD){
////					detail.setHttpMethod(HttpMethodType.HEAD);
////					detail.setPath(path+((HEAD) a).path());
////				} else if (a instanceof PATH){
////					detail.setHttpMethod(HttpMethodType.PATH);
////					detail.setPath(path+((PATH) a).path());
////				} else if (a instanceof MwfServlet){
////					detail.setHttpMethod(HttpMethodType.PATH);
////					detail.setPath(path+((MwfServlet) a).path());
////				}
////				
////				routes.add(detail);
////			}
////		}
////	}
//	public static ServletContext GetServletContext() {
//		return ServletManager.GetInstance().getServletContext();
//	}
//	public static void Init(ServletContext servletContext) {
//		ServletManager.GetInstance().init(servletContext);
//	}
//	
//	public RouteManager(){
//		reflections = new Reflections("");
//	}
//	
//	public Reflections getReflections() {
//		return reflections;
//	}
//
//	public void setReflections(Reflections reflections) {
//		this.reflections = reflections;
//	}
//
//	public ServletContext getServletContext() {
//		return servletContext;
//	}
//	public void setServletContext(ServletContext servletContext) {
//		this.servletContext = servletContext;
//	}
//
//	public static Reflections GetReflections() {
//		return GetInstance().getReflections();
//	}
//	public static List<Route> GetRoutes() {
//		return GetInstance().getMethlets();
//	}
//	public List<Route> getMethlets() {
//		return routes;
//	}
//	public void setMethlets(List<Route> routes) {
//		this.routes = routes;
//	}
}
