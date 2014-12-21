package punto.cms;

import punto.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MattUpstairs on 30/09/2014.
 */
public abstract class CmsList<T extends Identifiable> extends Storable  {
    Map<String,T> ls = new HashMap<>();

    String label;
    public String getLabel(){
        return label;
    }
    public CmsList(String name, String path, String label) {
        super(name,path);
        this.label=label;
    }
    public T createNew(String id){

        T t = createNew(label,listPath(id,label),id);

        ls.put( id, t );
        return t;
    }

    public void add(T c){
        ls.put(c.id(),c);
    }
    public abstract T createNew(String name, String path,String id);
    public String name(){
        return getName();
    }

    public T get(String id){
        return ls.get(id);
    }
    public T get(String id, boolean create){
        if(ls.containsKey(id)==false){
            if(create) {
                return createNew(id);
            }
            else
                return null;
        }
        return ls.get(id);
    }
}
