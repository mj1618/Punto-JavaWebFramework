package punto.route;

import punto.log.Log;
import punto.http.HttpMethodType;
import punto.mvc.BaseController;
import punto.mvc.Controller;
import punto.mvc.ControllerMethod;

import java.util.ArrayList;
import java.util.List;

public class Route {
	
	String path;
	List<Route> groupRoutes = new ArrayList<Route>();
	List<IndividualRoute> subRoutes = new ArrayList<IndividualRoute>();
	List<Filter> filters = new ArrayList<Filter>();
	List<PreProcessor> preProcessors = new ArrayList<>();
    List<PostProcessor> postProcessors = new ArrayList<>();

    public void addPostProcessors(List<PostProcessor> postProcessors) {
        this.postProcessors.addAll(postProcessors);
    }

    public void addPreProcessors(List<PreProcessor> preProcessors) {
        this.preProcessors.addAll(preProcessors);
    }
    public Route before(PreProcessor proc){
        preProcessors.add(proc);
        return this;
    }

    public Route after(PostProcessor proc){
        postProcessors.add(proc);
        return this;
    }

    public List<Route> getGroupRoutes() {
		return groupRoutes;
	}

	public void setGroupRoutes(List<Route> groupRoutes) {
		this.groupRoutes = groupRoutes;
	}


	public List<IndividualRoute> getSubRoutes() {
		return subRoutes;
	}

	public void setSubRoutes(List<IndividualRoute> subRoutes) {
		this.subRoutes = subRoutes;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	public Route(String path){
		this.path = path;
	}
	
//	public static final Route get(String path, Class c){
//		return new Route(path,c);
//	}
	
	public Route() {
		path="";
	}
//
//	public final <T extends BaseController> Route get(String path, T c){
//		return c.route(ctx);
//	}
//	
//	
//	public static final Route route(String path, List<HttpMethod> methods, 
//			Class<? extends BaseController> controller, List<Filter> preFilters, List<Filter> postFilters){
//		return new Route(path);
//	}
//	

    public final IndividualRoute routeMethod(String subPath,HttpMethodType type ){
        IndividualRoute r = new IndividualRoute(path,subPath);
        r.setHttpMethod(type);
        subRoutes.add(r);
        return r;
    }

    public IndividualRoute routeMethod(String subPath, ControllerMethod method, HttpMethodType type){
        IndividualRoute r = new IndividualRoute(path,subPath);
        r.setHttpMethod(type);
        r.controller(method);
        subRoutes.add(r);
        return r;
    }


    public final IndividualRoute delete(String subPath){
        return routeMethod(subPath,HttpMethodType.DELETE);
    }

    public IndividualRoute delete(String subPath, ControllerMethod method) {
        return routeMethod(subPath,method,HttpMethodType.DELETE);
    }




    public final IndividualRoute put(String subPath){
        return routeMethod(subPath,HttpMethodType.PUT);
    }

    public IndividualRoute put(String subPath, ControllerMethod method) {
        return routeMethod(subPath,method,HttpMethodType.PUT);
    }


    public final IndividualRoute post(String subPath){
        return routeMethod(subPath,HttpMethodType.POST);
    }

	public IndividualRoute post(String subPath, ControllerMethod method) {
		return routeMethod(subPath,method,HttpMethodType.POST);
	}
	
	public final IndividualRoute get(String subPath){
        return routeMethod(subPath,HttpMethodType.GET);
	}
	
	public IndividualRoute get(String subPath, ControllerMethod method) {
        return routeMethod(subPath,method,HttpMethodType.GET);
	}
	
	public IndividualRoute get(String subPath, Class<? extends Controller> c) {


        IndividualRoute r = new IndividualRoute(path,subPath);
        try {
            r.controller(c);
            subRoutes.add(r);
            return r;
        } catch (IllegalAccessException | InstantiationException e) {
            Log.error("Couldn't instantiate controller" + c.getName());
            e.printStackTrace();
            // TODO
            return null;
        }
	}
	
	public IndividualRoute get(String subPath, BaseController c) {
		IndividualRoute r = new IndividualRoute(path,subPath);
		r.controller(c);
		subRoutes.add(r);
		return r;
	}
	
	public Route group(String path, Group group) {

        if(this.path.endsWith("/")&&path.startsWith("/"))
            path = path.substring(1);


		Route r = new Route(this.path+path);
		group.makeRoutes(r);
		groupRoutes.add(r);
		return r;
	}


	private void cascadeSub() {

        subRoutes.stream().forEach(r -> {
            r.addPreProcessors(this.preProcessors);
            r.addPostProcessors(this.postProcessors);
        });
    }
    private void cascadeGroup(){
        groupRoutes.stream().forEach(g -> {
            g.addPreProcessors(this.preProcessors);
            g.addPostProcessors(this.postProcessors);
        });
	}


	public List<IndividualRoute> cascadeGetSubRoutes() {
        cascadeSub();
		return subRoutes;
	}
	
	public List<Route> cascadeGetGroupRoutes() {
        cascadeGroup();
		return groupRoutes;
	}

	public Route filter(FilterCondition cond, BaseController c) {
		Filter filter = new Filter(cond,c);
		filters.add(filter);
		groupRoutes.forEach(group -> group.filter(cond, c));
		subRoutes.forEach(r -> r.filter(cond, c));
        return this;
	}

	
}
