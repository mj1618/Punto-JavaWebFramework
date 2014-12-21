package punto.cms;

import punto.util.Utils;

/**
 * Created by MattUpstairs on 15/09/2014.
 */
public class Storable implements Identifiable{
    private String path;
    private String name;
    String id;

    public Storable(String path) {
        this("",path);
    }

    @Override
    public String id(){
        return id;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Storable(String name, String path) {
        this(name,path,Utils.createId());
    }

    public Storable(String name, String path,String id) {
        this.name = name;
        this.path=path;
        this.id=id;
    }

    public String path(){
        return getPath();
    }
    public String name(){
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Storable storable = (Storable) o;

        if (name != null ? !name.equals(storable.name) : storable.name != null) return false;
        if (path != null ? !path.equals(storable.path) : storable.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String subPath(String name){
        return getPath()+"/"+name;
    }
//    public String subList(String name,String id){
//        return getPath()+"/"+name+"$"+id;
//    }
    public String listPath(String id, String label){
        return getPath()+"$"+id+"$"+label;
    }

    public String listName(String name){
        return name;
    }


    public void changeId() {

        this.id = Utils.createId();
    }
}
