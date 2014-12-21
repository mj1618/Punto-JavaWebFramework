package punto.form;

import punto.form.items.TextItem;
import punto.util.Builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MattWork on 8/10/2014.
 */
public class FormObject extends FormComponent {

    Map<String,FormItem> items = new HashMap<>();
    Map<String,FormObject> objects = new HashMap<>();
    Map<String,FormObjectList> objectLists = new HashMap<>();
    Map<String,FormItemList> itemLists = new HashMap<>();

    public FormObject() {
        super();
    }

    @Override
    public Object clone(){
        FormObject obj = new FormObject();
        obj.setItems(new HashMap<>(items));
        obj.setItemLists(new HashMap<>(itemLists));
        obj.setObjectLists(new HashMap<>(objectLists));
        obj.setObjects(new HashMap<>(objects));

        obj.fromComponent(this);
        return obj;
    }

    @Override
    public String render(){

        String html="<div id='"+this.getId()+"'>\n";

        for(FormComponent c : getAllFormComponents()){
            html+=c.render();
        }

        html+="</div>\n";

        return html;

    }




    //////////////////// Builders ///////////////////////////////
    public void buildItemList(String name, String label, Builder<FormItemList> builder) {
        FormItemList ls = new FormItemList();
        ls.setName(name);
        ls.setLabelName(label);
        ls.setPath(getSubPath());
        builder.build(ls);
        itemLists.put(name,ls);
    }
    public void buildItem(FormItem item){
        item.setPath(getSubPath());
        items.put(item.getName(), item);
    }

    public void buildObject(String name, String label, Builder<FormObject> builder){
        FormObject object = new FormObject();
        object.setName(name);
        object.setLabelName(label);
        object.setPath(getSubPath());
        builder.build(object);
        objects.put(name,object);
    }

    public void buildObjectList(String name, String label, Builder<FormObjectList> builder){
        FormObjectList ls = new FormObjectList();
        ls.setName(name);
        ls.setLabelName(label);
        ls.setPath(getSubPath());
        builder.build(ls);
        objectLists.put(name,ls);
    }

    //////////////////// Getters and Setters //////////////////////
    public List<FormComponent> getAllFormComponents(){
        List<FormComponent> ls = new ArrayList<>();
        ls.addAll(items.values());
        ls.addAll(objects.values());
        ls.addAll(objectLists.values());
        ls.addAll(itemLists.values());
        return ls;
    }
    public Map<String, FormItem> getItems() {
        return items;
    }

    public void setItems(Map<String, FormItem> items) {
        this.items = items;
    }

    public Map<String, FormObject> getObjects() {
        return objects;
    }

    public void setObjects(Map<String, FormObject> objects) {
        this.objects = objects;
    }

    public Map<String, FormObjectList> getObjectLists() {
        return objectLists;
    }

    public void setObjectLists(Map<String, FormObjectList> objectLists) {
        this.objectLists = objectLists;
    }

    public Map<String, FormItemList> getItemLists() {
        return itemLists;
    }

    public void setItemLists(Map<String, FormItemList> itemLists) {
        this.itemLists = itemLists;
    }


}
