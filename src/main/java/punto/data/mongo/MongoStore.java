package punto.data.mongo;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.util.JSON;
import punto.util.Utils;

import java.net.UnknownHostException;
import java.util.Optional;

/**
 * Created by MattUpstairs on 22/09/2014.
 */
public class MongoStore {

    static MongoStore mongoStore;



    public static MongoStore instance(){
        return mongoStore;
    }
    public void close(){
        mongoClient.close();
        mongoClient=null;
        db=null;

    }


    String dbName;
    public static String SERVER="localhost";
    String collectionName;

    public MongoStore(String dbName, String collectionName){
        this.dbName=dbName;
        this.collectionName=collectionName;
        ttl();
    }
    public void ttl(){
        collection().createIndex(new BasicDBObject("createdAt", 1), new BasicDBObject("expireAfterSeconds", 3600));
    }

    public void insert(String obj){
        collection().insert((DBObject) JSON.parse(obj));
    }


    public DBCollection collection(){
        return db().getCollection(collectionName);
    }

    MongoClient mongoClient;
    DB db;
    public DB db(){

        if(db!=null)return db;
        try {
            mongoClient = new MongoClient(SERVER);
            db = mongoClient.getDB(dbName);
            return db;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public Datastore ds(){
//        try {
//            return new Morphia().createDatastore(new MongoClient(SERVER), dbName);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    public <T> Object get(Class<T> c) {
//        return ds().find(c).get();
//        //pages().update(page.getQuery(),page,true,false);
//    }

    public GridFS gridFS(String name){
        return new GridFS( db(),name );
    }


    public <K> Optional<K> findOne(String k, String v, Class<K> clazz) {
        DBObject q= new BasicDBObject(k,v);
        DBObject res = collection().findOne(q);
        if(res==null)return Optional.empty();
        return Optional.of(Utils.gson().fromJson(res.toString(), clazz));
    }

    public void insert(Object o) {
        collection().insert((DBObject)JSON.parse(Utils.toJson(o)));
    }
}
