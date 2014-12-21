package punto.data.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;
import org.bson.types.ObjectId;
import punto.cms.CmsPage;
import punto.cms.FlatCmsPage;
import punto.log.Log;
import punto.util.Utils;

import java.io.InputStream;

public class ContentStore {

    MongoStore store;

    public ContentStore(String dbName){
        store = new MongoStore(dbName,CmsPage.PAGES_COLLECTION);
    }

    public DBCollection pages(){
        return store.collection();
    }

	public static ContentStore create(String dbName) {
		return new ContentStore(dbName);
	}



	public void writeFlatPage(FlatCmsPage page) {
		//ds().save(contents);
        pages().update(
                new BasicDBObject("_id", page._id),
                (DBObject) JSON.parse(Utils.toJson(page)), true, false);
		//pages().update(page.getQuery(),page,true,false);
	}

    public FlatCmsPage readFlatPage(String field, String value){
        BasicDBObject doc = new BasicDBObject(field, value);
        return Utils.gson().fromJson(pages().findOne(doc).toString(), FlatCmsPage.class);
        //return ds().createQuery(clazz).filter("name =",name).get();
    }

    public GridFS gridFS(){
        return store.gridFS("cmsfiles");
    }

	public Object writeFile(byte[] bytes) {
        GridFS grid =gridFS();
        String md5 = Utils.md5(bytes);

        if(grid.findOne(md5)!=null)
            return grid.findOne(md5).getId();

        GridFSInputFile in = grid.createFile( bytes );
        in.setFilename(md5);
        in.save();
        Log.test("dbName:" + store.dbName);
        Log.test("writing file:" + in.getId().toString());

        return in.getId();
	}

	public void drop() {
        GridFS grid = gridFS();
        grid.getFileList().forEach(obj -> {
            grid.remove(obj);
        });
        pages().drop();
	}

    public InputStream readFile(String cid) {
        ObjectId oid = new ObjectId(cid);
        try{
            return gridFS().findOne(oid).getInputStream();

        }catch(Exception e){
            Log.error("No such file in mongo with id:" + cid);
            return null;
        }
    }
}
