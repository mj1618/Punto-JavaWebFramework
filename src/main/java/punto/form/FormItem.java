package punto.form;

import org.apache.commons.fileupload.FileItem;

/**
 * Created by MattWork on 8/10/2014.
 */
public abstract class FormItem extends FormComponent{


    public FormItem(){
        super();
    }

    public abstract String render();

    public abstract void load(String value);
    public abstract void loadImage(FileItem file);

    @Override
    public abstract Object clone();
}
