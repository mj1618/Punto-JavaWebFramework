package punto.data.mongo;

import org.bson.types.ObjectId;

/**
 * Created by MattUpstairs on 29/09/2014.
 */
public class MongoId {
    String id;

    public MongoId(String id) {
        if(id.length()!=24)throw new RuntimeException("Error, id isn't 24 bytes:"+id);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return id;
    }

    public ObjectId toObjectId() {
        return new ObjectId();
    }
}
