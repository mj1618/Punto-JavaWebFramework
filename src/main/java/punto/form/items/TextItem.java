package punto.form.items;

import org.apache.commons.fileupload.FileItem;
import punto.form.FormItem;

/**
 * Created by MattWork on 8/10/2014.
 */
public class TextItem extends FormItem {

    public TextItem(){
        super();
    }

    public TextItem(String name, String labelName, String value) {
        super();
        super.setName(name);
        super.setLabelName(labelName);
        super.setValue(value);
    }

    @Override
    public String render() {
        return "<input type='text' value='"+getValue()+"' name='"+this.getAbsolutePath()+"' id='"+this.getId()+"'/>\n";
    }

    @Override
    public void load(String value) {

    }

    @Override
    public void loadImage(FileItem file) {

    }

    @Override
    public Object clone(){
        TextItem item = new TextItem();
        item.fromComponent(this);
        return item;
    }

    public String getText(){
        return this.getValue();
    }

    public void setText(String text){
        this.setValue(text);
    }
}
