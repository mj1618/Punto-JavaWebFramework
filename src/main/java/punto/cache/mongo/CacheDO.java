package punto.cache.mongo;

import punto.util.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MattUpstairs on 22/09/2014.
 */
public class CacheDO {
    String _id;
    Map cache = new HashMap();

    Date createdAt = new Date();
    public CacheDO(){}

    public CacheDO(String id){
        this._id=id;
    }
    public String toString(){
        return Utils.toJson(this);
    }
}
