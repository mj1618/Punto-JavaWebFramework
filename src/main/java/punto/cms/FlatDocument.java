package punto.cms;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MattUpstairs on 15/09/2014.
 */
public class FlatDocument {

    Map<String, ContentItem> items = new HashMap<>();

    public FlatDocument(){}


    public void putItem(String key, ContentItem value){
        items.put(key,value);
    }

    public Map<String, ContentItem> getItems() {
        return items;
    }

    public void putAll(FlatDocument flatten) {
        items.putAll(flatten.getItems());
    }

    public ContentItem getItem(String name){
        return items.get(name);
    }
    public ContentItem getOrCreateItem(String name){

        if(items.containsKey(name))return items.get(name);
        else {
            ContentItem item = new ContentItem("Item",name);
            items.put(name,item);
            return item;
        }
    }
    public boolean containsKey(String key){
        return items.containsKey(key);
    }




}
