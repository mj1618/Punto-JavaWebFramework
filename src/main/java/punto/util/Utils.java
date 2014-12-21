package punto.util;

import com.google.gson.Gson;
import fm.jiecao.lib.Hashids;
import punto.Punto;
import punto.functional.Declarative;
import punto.http.HttpContext;
import punto.route.RouteManager;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class Utils {
	public static String CreatePath(String url) {
		if(url==null)return "";
		if(url.startsWith("/")==false)return "/"+url;
		else return url;
	}

    public static Integer decodeIdHash(String hash) {

        try {
            return (int)new Hashids(Punto.HASH_SALT).decode(hash)[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String encodeId(int id) {

        try {
            return new Hashids(Punto.HASH_SALT).encode(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String pathFromRequest(HttpServletRequest request){
		try{
			return StripContextRoot(new URL(request.getRequestURL().toString()).getPath());
		}catch( MalformedURLException e){
			e.printStackTrace();
			return null;
		}
	}




	public static String StripContextRoot(String path){
		String contextRoot = RouteManager.GetServletContext().getContextPath();
		//Debug.test("stripper: "+path);
		if(path.startsWith(contextRoot)){
			return path.substring(contextRoot.length());
		} else if(path.startsWith("/"+contextRoot)){
			return path = path.substring(("/"+contextRoot).length());
		} else 
			return path;
	}
//	public static boolean PathMatch(String path, String servletPath) {
//		Debug.test("Path:"+path+" servlet:"+servletPath);
//		
//		List<String> ptokens = new ArrayList<String>(Arrays.asList(path.split("/")));
//		if(path.endsWith("/"))ptokens.add("");
//		List<String> stokens = new ArrayList<String>(Arrays.asList(servletPath.split("/")));
//		if(servletPath.endsWith("/"))stokens.add("");
//		
//		Iterator<String> p = ptokens.iterator();
//		
//		Debug.test(MwfUtils.toJson(ptokens)+" "+MwfUtils.toJson(stokens)+" ");
//		
//		for(String stok : stokens){
//			if(stok.equals("**"))return true;
//			if(stok.equals("*")){
//				if(p.hasNext()==false)return false;
//				p.next();
//				continue;
//			}
//			if(p.hasNext()==false){
//				Debug.test("doesn't have next");
//				return false;
//			}
//			String ptok = p.next();
//			
//			if(stok.startsWith("{")&&stok.endsWith("}")&&stok.length()>2)continue;
//			
//			if(stok.contains("*")){
//				String toks[] = stok.split("*",2);
//				if(ptok.startsWith(toks[0]) && ptok.endsWith(toks[1]))continue;
//				else return false;
//			}
//			
//			if(stok.equals(ptok))continue;
//			
//			Debug.test("Not equal:"+stok +" "+ptok);
//			
//			return false;
//		}
//		if(p.hasNext())return false;
//		return true;
//	}
	
	public static String toJson(Object object){

        return gson().toJson(object);
    }

    public static <V> V fromJson(String s, Class<V> c){

        return gson().fromJson(s, c);
    }
//	public static List<Route> GetMatchingRoutes(String path, HttpMethodType method) {
//		
//
//		List<Route> matches = new ArrayList<Route>();
//		for(Route methlet : ServletManager.GetMethlets()){
//			Debug.test("checking:"+path+" "+methlet.getPath());
//			if(		MwfUtils.MethodMatch(method,methlet.getHttpMethod()) &&
//					MwfUtils.PathMatch(path, methlet.getPath()) ){
//				matches.add(methlet);
//			}
//		}
//		
//    	return matches;
//	}

//	private static boolean MethodMatch(HttpMethodType urlMethod,
//			HttpMethodType servletMethod) {
//		
//		return servletMethod==HttpMethodType.PATH || servletMethod==urlMethod;
//	}
	
	

	public static Object StringArrayToType(String[] strs, Class type) {
		if(strs.length==1){
			String s = strs[0];
			if(type.equals(Integer.class)){
				return Integer.parseInt(s);
			}
			if(type.equals(String.class)){
				return s;
			}
			
		} else {
			if(type.equals(Integer[].class)){
				List<Integer> is = new ArrayList<Integer>();
				for(String s:strs)
					is.add(Integer.parseInt(s));
				return is.toArray();
			}
		}
		return null;
	}

	public static Object StringToType(String s, Class type) {
		if(type.equals(Integer.class)){
			return Integer.parseInt(s);
		}
		if(type.equals(String[].class)){
			String[] ss = new String[1];
			ss[0]=s;
			return ss;
		}
		return s;
	}

	public static Map<String,String[]> QueryParams(HttpContext ctx) {
		return ctx.getRequest().getParameterMap();
	}
//
//	private static List<Object> PathQueryTokens(List<Class> types, List<String> tokens){
//		Iterator<Class> ic = types.iterator();
//		Iterator<String> is = tokens.iterator();
//		
//		List<Object> params = new ArrayList<Object>();
//		
//		while(ic.hasNext() && is.hasNext()){
//			
//			Class c = ic.next();
//			String s = is.next();
//			
//			if(c.getName().equals("java.lang.Integer")){
//				try{
//					Integer i = Integer.parseInt(s);
//					params.add(i);
//				} catch (Exception e){
//					throw new IllegalArgumentException("Could not convert path parameter "+s+" to an Integer type");
//				}
//				
//			} else if(c.getName().equals("java.lang.String")) {
//				params.add(s);
//			}
//			
//		}
//		
//		return params;
//	}
	
	

//	public static boolean pathIsFile(String path) {
//		String []s = path.split("/");
//		if(s.length==0)return false;
//		return s[s.length-1].contains(".");
//	}

//	public static String hotFile(String path) {
//		if(hotFiles==false)
//			return path;
//		else
//			return "C:\\Users\\MattUpstairs\\git\\perthparkourwebapp\\PerthParkourWeb\\WebContent"+path;
//	}
//	public static Object webContentPath() {
////		if(Punto.development)
////			return "C:\\Users\\MattUpstairs\\IdeaProjects\\Punto\\web";
////		else
//			return Utils.fullPathOfFile("/").toString();
//	}

    public static Stream<Entry<String,String>> parameters(String path, String accessedPath) {

        if(accessedPath.equals("")&&path.equals("/"))return new ArrayList<Entry<String,String>>().stream();
        //if(accessedPath.equals("/")&&path.equals("/")==false)return false;
        return
                Declarative
                        .zip(path.split("/"), accessedPath.split("/"))
                        .filter(pair -> isParameter(pair.first))
                        .map(pair -> new Entry(parseParam(pair.first), pair.second));
    }
    public static String parseParam(String x){
        return x.substring(1, x.length()-1);
    }
    public static boolean isParameter(String x){
        return x.startsWith("{")&&x.endsWith("}");
    }

    public static Gson gson(){
        return new Gson();
    }


    public static String createId() {

        return UUID.randomUUID().toString();
    }

    public static String md5(byte[] bytes) {

        try {
            MessageDigest d = MessageDigest.getInstance("MD5");
            byte[] array = d.digest(bytes);

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return createId();
        }
    }
}
