package punto.log;

import punto.util.Utils;

import java.util.logging.Logger;

public class Log {

    private final static Logger log = Logger.getLogger(Log.class.getName());
    public static void main(String args[]){
        log.fine("test log message");
        System.out.println(Log.class.getClassLoader().getResource("logging.properties"));
    }

    public static void test(Object o){
        System.out.println("[Punto] Test: "+ Utils.toJson(o));
    }
    public static void test(String s){
        System.out.println("[Punto] Test: "+s);
    }

    public static void note(String s){
        System.out.println("[Punto] Note: "+s);
    }

    public static void warning(String s){
		System.out.println("[Punto] Warning: "+s);

	}
//	public static void test(String s){
//		System.out.println("--MWF-- TESTING: "+s);
//
//	}
//
//	public static void log(String s){
//		System.out.println("--MWF-- DEBUG: "+s);
//	}
//
//	public static void json(Object o) {
//		test(new Gson().toJson(o));
//	}

	public static void error(String s) {
		System.err.println("[Punto] Error: "+s);
	}
    public static void error(String s,Exception e) {
        System.err.println("[Punto] Error: "+s);
        e.printStackTrace(System.err);
    }
}
