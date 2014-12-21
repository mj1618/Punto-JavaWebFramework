package punto.form;

import punto.util.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MattWork on 8/10/2014.
 */
public class FormItemList extends FormComponent {

    FormItem referenceItem;
    Map<String,FormItem> items = new HashMap<>();

    public FormItemList() {
    }

    public FormItem getReferenceItem() {
        return referenceItem;
    }

    public void setReferenceItem(FormItem referenceItem) {
        this.referenceItem = referenceItem;
    }

    ///////////// Builders /////////////////
    public void buildItem(FormItem item){
        item.setPath(getListSubPath(item.getId()));
        setReferenceItem(item);
    }

    @Override
    public Object clone(){
        FormItemList ls = new FormItemList();
        ls.fromComponent(this);
        ls.setItems(new HashMap<>(items));
        ls.setReferenceItem((FormItem) referenceItem.clone());
        return ls;
    }

    @Override
    public String render(){

        String html = "<ul id='"+getId()+"'>\n";

        for(FormItem object: items.values()){
            html+="<li>\n";
            html+=object.render();
            html+="</li>\n";
        }

        html+="</ul>\n";

        return html;
    }
    public Map<String, FormItem> getItems() {
        return items;
    }

    public void setItems(Map<String, FormItem> items) {
        this.items = items;
    }

    public void loadItem(String name, String label, Builder<FormItem> builder) {
        FormItem item = (FormItem)referenceItem.clone();
        item.setName(name);
        item.setLabelName(label);
        builder.build(item);
        items.put(item.getId(),item);
    }




}
