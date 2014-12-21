package punto.form;

import punto.util.Utils;

/**
 * Created by MattWork on 8/10/2014.
 */
public abstract class FormComponent {
    String name="default";
    String labelName="default";
    String id = Utils.createId();
    String path;
    String value="";

    public abstract String render();

    public FormComponent() {
    }




    public String getListSubPath(String id){
        return getPath()+name+"["+id+"]/";
    }


    public String getSubPath(){
        return getPath()+name+"/";
    }

    public String getAbsolutePath(){
        return getPath()+name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void fromComponent(FormComponent c) {
        setName(c.getName());
        setLabelName(c.getLabelName());
        setPath(c.getPath());
        setValue(c.getValue());
    }

    //public abstract Optional<FormItem> findOrCreate(String key);
}
