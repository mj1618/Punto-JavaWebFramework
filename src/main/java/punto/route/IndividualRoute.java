package punto.route;

import punto.log.Log;
import punto.functional.Declarative;
import punto.http.HttpContext;
import punto.http.HttpMethodType;
import punto.mvc.BaseController;
import punto.mvc.Controller;
import punto.mvc.ControllerMethod;
import punto.mvc.Response;
import punto.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IndividualRoute {

	String path;
	ControllerMethod controlMethod;
	List<String> parameters;
	HttpMethodType httpMethod = HttpMethodType.GET;
	List<Filter> filters = new ArrayList<Filter>();
	Controller controlClass;

    BaseController controlLambda;

    List<PreProcessor> preProcessors = new ArrayList<>();
    List<PostProcessor> postProcessors = new ArrayList<>();

    public void addPostProcessors(List<PostProcessor> postProcessors) {
        this.postProcessors.addAll(postProcessors);
    }

    public void addPreProcessors(List<PreProcessor> preProcessors) {
        this.preProcessors.addAll(preProcessors);
    }

    public void preProcess(HttpContext ctx){
        preProcessors.forEach( proc -> {
            proc.process(ctx,createControllerObject());
        });
    }

    public void postProcess(HttpContext ctx, Response response){
        postProcessors.forEach( proc -> {

            proc.process(ctx,createControllerObject(),response);
        });
    }

	
	ControllerType controlType=ControllerType.NONE;
	
	boolean isAsync=false;
	
	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public void filter(FilterCondition filter, BaseController c) {
		filters.add(new Filter(filter,c));
	}
	
	public IndividualRoute(String path,String subPath) {
        if(subPath.startsWith("/")&&path.endsWith("/"))subPath = subPath.substring(1);
        this.path=path+subPath;
	}

	public IndividualRoute controller(ControllerMethod method) {
		controlType=ControllerType.METHOD;
		controlMethod=method;
		return this;
	}

	public IndividualRoute controller(Class<? extends Controller> c) throws IllegalAccessException, InstantiationException {
		controlType=ControllerType.CLASS;
		controlClass = c.newInstance();
		return this;
	}
	public IndividualRoute controller(BaseController c){
		controlType=ControllerType.LAMBDA;
		controlLambda = c;
		return this;
	}
	
	public boolean isAsync() {
		return isAsync;
	}
	public void setAsync(boolean isAsync) {
		this.isAsync = isAsync;
	}
	public HttpMethodType getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(HttpMethodType httpMethod) {
		this.httpMethod = httpMethod;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	
	
//	public Object[] getArguments(String path){
//		Map<String, PathParameter> argumentValues = PathParameter.arguments(this, path);
//		List<Object> arguments = new ArrayList<Object>();
//		
//		parameters.forEach(paramName -> {
//			if(paramName.equals("")==false&&argumentValues.containsKey(paramName)){
//				PathParameter arg = argumentValues.get(paramName);
//				arguments.add(arg.getObject());
//			} else {
//				Debug.error("Route - "+this.getPath()+" has no argument "+paramName);
//			}
//		});
//		
//		return arguments.toArray();
//	}

//	private Response invoke(HttpContext ctx, String path) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		List<Object> params = new ArrayList<Object>();
//		
//		Annotation[][] paramAnnotations = method.getParameterAnnotations();
//		
//		Map<String,String[]> queryParams = Utils.QueryParams(ctx);
//		Map<String,String> pathParams = Utils.PathParams(this,path);
//		
//		List<Class> types = new ArrayList<Class>(Arrays.asList(method.getParameterTypes() ));
//		
//		int parami = 0;
//		for(Annotation[] param : paramAnnotations){
//			Class type = types.get(parami++);
//			Object o = null;
//			for(Annotation a : param){
//				if(a instanceof PathParam){
//					o = Utils.StringToType(pathParams.get(((PathParam)a).name()), type);
//					break;
//				} else if (a instanceof QueryParam){
//					o = Utils.StringArrayToType(queryParams.get(((QueryParam)a).name()), type);
//					break;
//				} else if (a instanceof HttpContextParam){
//					o = ctx;
//					break;
//				}
//			}
//			params.add(o);
//		}
//		
//		return (Response)RouteManager.Invoke(this,params);
//	}
	public boolean matches(String accessedPath) {
		//Debug.test("Path:"+path+" servlet:"+accessedPath);
		if(accessedPath.equals("")&&path.equals("/"))return true;
		//if(accessedPath.equals("/")&&path.equals("/")==false)return false;
        List<Pair<String,String>> portions =
                Declarative
                        .zipLoose(path.split("/"), accessedPath.split("/"))
                        .collect(Collectors.toList());

        return Declarative.upto(
                portions,
                pair -> ((pair.first != null && pair.first.equals("**")) ? true : false)
        ).allMatch(pair -> matches(pair.first, pair.second));
	}
	
	public static void main(String args[]){
		//IndividualRoute r = new IndividualRoute("/asdf");
		//Debug.test(""+r.matches("/"));
	}
	
	private boolean matches(String first, String second) {
		//Debug.test("comparing: "+first+" "+second);
		if(first==null&&second==null)return true;
		else if(first==null||second==null)return false;
		else if(first.equals("*"))return true;
		else if(PathParameter.isParameter(first))return true;
		else return Pattern.compile(first).matcher(second).matches();
	}
    public IndividualRoute before(PreProcessor proc){
        preProcessors.add(proc);
        return this;
    }

    public IndividualRoute after(PostProcessor proc){
        postProcessors.add(proc);
        return this;
    }
    public Controller createControllerObject() {
        try {
            switch (controlType) {
                case CLASS:
                    return controlClass;
                case METHOD:
                    return this.controlMethod.getControlClass().newInstance();
                case LAMBDA:
                    return null;//throw new RuntimeException("no controller object for lambda");
                default:
                    throw new RuntimeException("no object to create in this object");
            }
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
	public Object createObject() {
		try {
            switch (controlType) {
                case CLASS:
                    return controlClass;
                case METHOD:
                    return this.controlMethod.getMethod().getDeclaringClass().newInstance();
                case LAMBDA:
                    return controlLambda;
                default:
                    throw new RuntimeException("no object to create in this object");
            }
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
	}


	public Response invoke(HttpContext ctx) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

        preProcess(ctx);

        Response response;

        switch(controlType){
		case CLASS:
			throw new RuntimeException("class return not created yet");
		case METHOD:
			try{
                response = (Response) controlMethod.getMethod().invoke(createObject(), new Object[]{ctx});
            } catch(Exception e){
                e.printStackTrace();
                Log.error(e.getMessage());
                return Response.errorMessage();
            }
            break;
		case LAMBDA:
			response = controlLambda.route(ctx);
            break;
		default:
			throw new RuntimeException("no controller in route");
		}

        postProcess(ctx, response);

        return response;
	}


	
//	private static Response invoke(Route route, HttpContext ctx, String path) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		List<Object> params = new ArrayList<Object>();
//		
//		Annotation[][] paramAnnotations = route.getMethod().getParameterAnnotations();
//		
//		Map<String,String[]> queryParams = MwfUtils.QueryParams(ctx);
//		Map<String,String> pathParams = MwfUtils.PathParams(route,path);
//		
//		List<Class> types = new ArrayList<Class>(Arrays.asList( route.getMethod().getParameterTypes() ));
//		
//		int parami = 0;
//		for(Annotation[] param : paramAnnotations){
//			Class type = types.get(parami++);
//			Object o = null;
//			for(Annotation a : param){
//				if(a instanceof PathParam){
//					o = MwfUtils.StringToType(pathParams.get(((PathParam)a).name()), type);
//					break;
//				} else if (a instanceof QueryParam){
//					o = MwfUtils.StringArrayToType(queryParams.get(((QueryParam)a).name()), type);
//					break;
//				} else if (a instanceof HttpContextParam){
//					o = ctx;
//					break;
//				}
//			}
//			params.add(o);
//		}
//		
//		
//		return (Response)route.getMethod().invoke(ServletManager.GetServlet(route.getServletClassName()), params.toArray());
//	}
}
