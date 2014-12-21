package punto.jee;

import punto.Punto;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.EnumSet;

@WebListener
public class ContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		javax.servlet.ServletRegistration.Dynamic td = event.getServletContext().addServlet(PuntoServlet.name, PuntoServlet.class);
		td.setAsyncSupported(true);

        event.getServletContext().addListener(SessionListener.class);

		Dynamic r= event.getServletContext().addFilter("Reciever", EntryFilter.class);
		EnumSet<DispatcherType> set = EnumSet.noneOf(DispatcherType.class);
		set.add(DispatcherType.REQUEST);
		set.add(DispatcherType.INCLUDE);
		set.add(DispatcherType.FORWARD);
		set.add(DispatcherType.ASYNC);
		set.add(DispatcherType.ERROR);		
		r.addMappingForUrlPatterns(set, false, "/*");
		r.setAsyncSupported(true);
		
		Punto.Init(event.getServletContext());
	}

}
