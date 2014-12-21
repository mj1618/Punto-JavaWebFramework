package punto.route;

import punto.http.HttpContext;

public interface FilterCondition {
	public boolean check(HttpContext ctx);
}
