package punto.route;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import punto.functional.Declarative;
import punto.util.Pair;
import static punto.route.StringParser.*;

public class PathParameter {
	String name;
	String stringValue;
	Integer intValue;
	PathParamType type;
	Pattern pattern;
	
	private PathParameter() {}
	
	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public PathParamType getType() {
		return type;
	}

	public void setType(PathParamType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Pattern getPattern() {
		return pattern;
	}
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	public static boolean isVariable(String first) {
		return first.startsWith("{")&&first.endsWith("}");
	}

	public static String GetName(String first) {
		return first.substring(1, first.length()-1);
	}
//	public static Map<String,PathParameter> arguments(IndividualRoute route, String path) {
//
//		final Map<String,PathParameter> params = new HashMap<String,PathParameter>();
//		
//		Declarative
//			.zip(route.getPath().split("/"), path.split("/"))
//			.map(pair -> Parse(pair))
//			.map(param -> params.put(param.getName(), param));
//		return params;
//	}
	

//	private static PathParameter Parse(Pair<String, String> pair) {
//		return Parse(pair.first,pair.second);
//	}

//	private static PathParameter Parse(String def, String value) {
//		PathParameter param = new PathParameter();
//		StringParser parse = new StringParser(def);
//		parse.strip("{", "}");
//		param.name = parse.nextToken(new String[]{":",""});
//		if(parse.finished()){
//			param.type = PathParamType.STRING;
//			param.stringValue=value;
//		} else {
//			param.type = PathParamType.fromString(parse.nextToken(new String[]{""}));
//			switch(param.type){
//			case STRING:
//				param.stringValue = value;
//				break;
//			case INT:
//				param.intValue=Integer.parseInt(value);
//				break;
//			case PATTERN:
//				// TODO
//				break;
//			default:
//				break;
//			}
//		}
//
//		if(parse.finished()&&parse.isValid) return param;
//		else return null;
//	}

	public static boolean isParameter(String def) {
		StringParser parse = new StringParser(def);
		parse.strip("{", "}");
		return parse.isValid;
	}

//	public static boolean isValidValue(String def, String value) {
//		return Parse(def,value)!=null;
//	}

	public Object getObject() {
		switch(type){
		case STRING: return stringValue;
		case INT: return intValue;
		case PATTERN: return stringValue;
		default: return null;
		}
	}

}
