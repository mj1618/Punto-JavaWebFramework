package punto.route;

import punto.log.Log;

public class StringParser {
	String def;
	boolean isValid=true;
	
	public StringParser(String def) {
		this.def = def;
	}
	
	public static void main(String args[]){
		StringParser p = new StringParser("{asdf}");
		p.strip("{","}");
		Log.test("" + p.isValid);
	}

	public String strip(String pre, String post){
		if(def.startsWith(pre)&&def.endsWith(post))
			return def.substring(pre.length(), def.length()-post.length());
		else return notValid();
	}

	private String notValid() {
		isValid=false;
		return null;
	}

	public boolean finished(){
		return def.length()==0;
	}
	
	private String nextToken(String split) {
		int index = def.indexOf(split);
		String result = def.substring(0, index);
		cut(index+split.length());
		return result;
	}

	private void cut(int i){
		if(i>def.length())notValid();
		else def = def.substring(i);
	}

	public String nextToken(String[] splits) {
		for(String split:splits){
			if(split.length()==0||def.indexOf(split)!=-1){
				return nextToken(split);
			}
		}
		return notValid();
	}
}
