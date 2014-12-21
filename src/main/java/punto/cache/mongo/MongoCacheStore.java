package punto.cache.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import punto.data.mongo.MongoStore;
import punto.util.Utils;

import java.util.Map;

/**
 * Created by MattUpstairs on 22/09/2014.
 */
public class MongoCacheStore {
    public static String CACHES_COLLECTION = "caches";

    MongoStore store;
    public MongoCacheStore(String name){
        store = new MongoStore(name, CACHES_COLLECTION);
    }

    public void clearCache(String name){

        store.collection().update(new BasicDBObject("_id", name), (DBObject)JSON.parse(new CacheDO(name).toString()));
    }

    public void deleteCache(String id){
        store.collection().remove(new BasicDBObject("_id",id));
    }

    public boolean containsKey(String id,String key){
        DBObject q = BasicDBObjectBuilder.start().add("_id",id).add("cache." + key, new BasicDBObject("$ne", null)).get();
        return store.collection().findOne(q)!=null;
    }

    public boolean containsKey(String key){
        DBObject q = BasicDBObjectBuilder.start().add("cache."+key, new BasicDBObject("$ne", null)).get();
        return store.collection().findOne(q)!=null;
    }


    public void removeValue(String id, String key) {
        DBObject newValue = unsetDBObject("cache."+key);

        BasicDBObject searchQuery = new BasicDBObject().append("_id", id);

        store.collection().update(searchQuery, newValue);
    }

    public void putValue(String id, String key, String val) {
        DBObject newValue = setDBObject("cache."+key,val);

        BasicDBObject searchQuery = new BasicDBObject().append("_id", id);

        store.collection().update(searchQuery, newValue);
    }

    public DBObject setDBObject(String key, Object value){
        return new BasicDBObject("$set",new BasicDBObject(key,value));
    }
    public DBObject unsetDBObject(String key){
        return new BasicDBObject("$unset",new BasicDBObject(key,""));
    }

    public String getValue(String id, String key) {
        BasicDBObject searchQuery = new BasicDBObject().append("_id", id);

        DBObject fields = new BasicDBObject("cache."+key,true);

        DBObject result = store.collection().findOne(searchQuery,fields);
        return (String)((Map)result.get("cache")).get(key);
    }

    public void ensureCache(String id) {
        BasicDBObject searchQuery = new BasicDBObject().append("_id", id);
        if(store.collection().findOne(searchQuery)==null){
            store.insert(new CacheDO(id).toString());
        }
    }

    public CacheDO getCacheDO(String id) {
        BasicDBObject doc = new BasicDBObject("_id", id);
        return Utils.gson().fromJson(store.collection().findOne(doc).toString(), CacheDO.class);
    }
}
