package punto.mvc;

import punto.http.HttpContext;

public interface BaseController {
	public Response route(HttpContext ctx);
	//public Response route(HttpContext ctx, Object... params);
}
