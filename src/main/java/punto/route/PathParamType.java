package punto.route;

public enum PathParamType {
	STRING,INT,PATTERN;

	public static PathParamType fromString(String token) {
		if(token.equals("String"))return STRING;
		else if(token.equals("int"))return INT;
		else if(token.equals("Pattern"))return PATTERN;
		else return STRING;
	}
}
