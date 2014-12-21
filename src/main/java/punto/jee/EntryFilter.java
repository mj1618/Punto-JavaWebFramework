package punto.jee;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import punto.route.RouteManager;

public class EntryFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
		RouteManager.Init(config.getServletContext());
	}
	@Override
	public void doFilter(ServletRequest srequest, ServletResponse sresponse,
			FilterChain chain) throws IOException, ServletException {
		
		PuntoFilter.DoFilter(srequest, sresponse, chain);
		
	}
	@Override
	public void destroy() {	}
}
