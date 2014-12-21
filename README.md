Punto CMS and Web Framework
Matthew James
===========================

Any feedback/comments welcome as long as its constructive, happy for people to use parts of the code or contribute as well.

Taken ideas from Play Framework and Laravel, trying to create a web framework with the following properties -
- Makes use of Java 8 functionality to improve things such as routing
- Projects created like a normal JEE app, just needing to add the Punto dependency
- No XML files (no files should be needed for routing, no web.xml needed by Punto)
- Routes uses Lambdas to be as clean as Laravels
- Would like to add client-side sessions
- HttpServletRequest/Response are used and can be accessed if necessary, but Punto adds wrappers around these through HttpContext
- Have tried to do a CMS under /cms to simplify building a CMS, it has worked in a project but is messy and needs to be rethought, /form is the beginning of this rethinking.
  MongoDB was used as the CMS store, but have removed that as a requirement for Punto to work.

The current build works, but mainly I've just worked on the routes so far.
I've tested on Wildfly and Jetty with Java 8.

The following is a sample router I used in a project, creating this class is all that is needed to register the routes (done using reflections).
A sample controller for the login is below.


	public class MySiteRoutes extends Router{
		public void makeRoutes(Route route) {

			route.get("/login", 		        method(Login.class, "get"));
			route.post("/login", 		        method(Login.class, "post"));

			route.group("/",subroute->{
				subroute.get("/images/**", 	    ctx -> fileContent("/www",ctx.getPath()));
				subroute.get("/css/**", 		ctx -> fileContent("/www",ctx.getPath()));
				subroute.get("/js/**", 		    ctx -> fileContent("/www",ctx.getPath()));
				subroute.get("/fonts/**", 		ctx -> fileContent("/www",ctx.getPath()));
				subroute.get("/plugins/**",     ctx -> fileContent("/www",ctx.getPath()));
			});

			route.group("/", subroute -> {
				subroute.get("/cms/files/{id}", ctx -> cmsFile( ctx.parameter("id") ) );
				subroute.get("/", 				ctx -> redirect("/home"));
				subroute.get("/home", 			method(Home.class, "get")).before(new Common());
				subroute.get("/about", 			method(About.class, "get")).before(new Common());
				subroute.get("/events", 		method(Events.class, "get")).before(new Common());
				subroute.get("/services", 		method(Services.class, "get")).before(new Common());
				subroute.get("/contact", 		method(Contact.class, "get")).before(new Common());
				subroute.post("/sendmail", 	    method(Email.class, "send")).before(new Common());
			}).filter(	ctx -> 	ctx.user().isPresent()==false,
						ctx -> {
							ctx.rememberPath();
							return redirect("/login");
			}); //Similarly you can do .before( ctx -> ...) and .after( ctx -> ...) like in laravel
		}
	}

Sample login controller (returns a velocity template for GET).

	public class Login extends Controller{

		public Response get(HttpContext ctx){
			return vm("/pages/login.vm");
		}

		public Response post(HttpContext ctx){
			if( authenticate(ctx, ctx.input("username").get(),ctx.input("password").get()) ){
				Optional<Response> response = ctx.returnPath();
				if(response.isPresent())
					return response.get();
				else
					return redirect("/");
			} else {
				ctx.putAttribute("error", "Incorrect Password");
				return redirect("/login");
			}
		}
	}
