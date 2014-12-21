package punto.http;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public enum HttpMethodType {
	GET,POST,DELETE,PUT,HEAD,OPTIONS,TRACE,CONNECT, /* PATH implies 'any method' */ PATH
}
