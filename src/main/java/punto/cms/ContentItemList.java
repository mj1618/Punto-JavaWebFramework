package punto.cms;

import org.mongodb.morphia.annotations.Embedded;
import punto.data.mongo.MongoId;
import punto.log.Log;
import punto.util.Utils;

import java.util.Iterator;
import java.util.Optional;

/**
 * Created by MattUpstairs on 11/09/2014.
 */
@Embedded
public class ContentItemList extends CmsList<ContentItem> implements Iterable<ContentItem>{

    public ContentItemList(String name, String path,String label) {
        super(name,path,label);
    }
    public static ContentItemList create(String name, String path,String label) {
        return new ContentItemList(name,path,label);
    }

//    public ContentItemList addText(String text){
//        String name = "item";
//        ContentItem it = ContentItem.createText("item"+ls.size(),subList(name,ls.size()),text);
//        ls.add(it);
//        return this;
//    }
//
//    public ContentItemList addImage(MongoId id){
//        String name = "item"+ls.size();
//        ContentItem it = ContentItem.createFile("item"+ls.size(),subList(name,ls.size()),id);
//        ls.add(it);
//        return this;
//    }

    @Override
    public Iterator<ContentItem> iterator() {
        return ls.values().iterator();
    }

    public ContentItem addImage(MongoId id){
        ContentItem it=  createNew(Utils.createId());
        it.setType(ContentType.FILE);
        it.setId(id.getId());
        return it;
    }
    public ContentItem addText(String text){
        ContentItem it=  createNew(Utils.createId());
        it.setType(ContentType.TEXT);
        it.setText(text);
        return it;
    }

    public ContentItem createNew(String name,String path,String id){
        return ContentItem.createWithId(name,path,id);
    }

    public ContentItem copyAdd(CmsPage page) {

        Optional<ContentItem> item =
                page.flatten().getDoc().getItems().values().stream()
                    .filter(it -> it.getPath().startsWith(this.getPath()))
                    .findAny();


        if(item.isPresent()==false) {
            Log.error("couldn't find example item for list:" + getPath());
            return null;
        }
        ContentItem c = item.get();

        switch(c.getType()){
            case FILE:
                return addImage(new MongoId(c.getFileId()));

            case TEXT:
                return addText(c.getText());

        }
        return null;
    }
}
