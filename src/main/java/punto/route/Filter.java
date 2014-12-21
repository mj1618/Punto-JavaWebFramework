package punto.route;

import java.util.Optional;

import punto.http.HttpContext;
import punto.mvc.BaseController;
import punto.mvc.Response;

public class Filter {

	FilterCondition condition;
	BaseController filterController;
	public Filter(FilterCondition condition, BaseController filterController) {
		super();
		this.condition = condition;
		this.filterController = filterController;
	}
	
	public boolean check(HttpContext ctx){
		return condition.check(ctx);
	}
	
	public Response action(HttpContext ctx){
		return filterController.route(ctx);
	}
	
	public Optional<Response> checkAndAction(HttpContext ctx){
		if(check(ctx))
			return Optional.of(filterController.route(ctx));
		else
			return Optional.empty();
	}
	
	
}
