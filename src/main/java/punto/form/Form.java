package punto.form;

import punto.http.HttpContext;
import punto.util.Builder;

/**
 * Created by MattWork on 8/10/2014.
 */
public abstract class Form implements Builder<FormObject> {
    FormObject formObject;

    public void build(){
        formObject = new FormObject();
        formObject.setName(getFormName());
        formObject.setPath("/");
        build(formObject);
    }



    public void load(HttpContext ctx){

        //formObject.clear();

//        ctx.inputs().forEach((key, ls) -> {
//
//            String value = ls.get(0);
//
//            if(key.startsWith(formObject.getAbsolutePath())){
//                formObject.findOrCreate(key).load(value);
//            }
//
//        });
//
//        ctx.inputFiles().forEach((key,fileItem)->{
//            if(key.startsWith(formObject.getAbsolutePath())){
//                formObject.findOrCreate(key).loadImage(fileItem);
//            }
//        });
    }

    public abstract void build(FormObject form);
    public abstract String getFormName();
    public String render(){
        return formObject.render();
    }
}
