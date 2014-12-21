package punto.form;

import punto.util.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MattWork on 8/10/2014.
 */
public class FormObjectList extends FormComponent{

    FormObject referenceObject;
    Map<String,FormObject> objectList = new HashMap<>();

    public FormObjectList() {
    }

    public FormObject getReferenceObject() {
        return referenceObject;
    }

    @Override
    public Object clone(){
        FormObjectList ls = new FormObjectList();
        ls.fromComponent(this);
        ls.setObjectList(new HashMap<>(objectList));
        ls.setReferenceObject((FormObject)referenceObject.clone());
        return ls;
    }

    public void setReferenceObject(FormObject referenceObject) {
        this.referenceObject = referenceObject;
    }

    /////////// Builders //////////
    public void buildReferenceObject(String name, String label, Builder<FormObject> builder){
        FormObject obj = new FormObject();
        obj.setName(name);
        obj.setLabelName(label);
        obj.setPath(getListSubPath(obj.getId()));
        builder.build(obj);
        setReferenceObject(obj);
    }
    ///////////////////////////

    /////// loaders /////////////
    public void loadObject(String name, String label, Builder<FormObject> builder) {
        FormObject obj = (FormObject)getReferenceObject().clone();
        obj.setName(name);
        obj.setLabelName(label);
        builder.build(obj);
        objectList.put(obj.getId(),obj);
    }

    ////////////////////////////
    @Override
    public String render(){

        String html = "<ul id='"+getId()+"'>\n";

        for(FormObject object:objectList.values()){
            html+="<li>\n";
            html+=object.render();
            html+="</li>\n";
        }

        html+="</ul>\n";

        return html;
    }
    public Map<String, FormObject> getObjectList() {
        return objectList;
    }

    public void setObjectList(Map<String, FormObject> objectList) {
        this.objectList = objectList;
    }


//    @Override
//    public Optional<FormItem> findOrCreate(String path){
//        return Optional.empty();
//    }

}
