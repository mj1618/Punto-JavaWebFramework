package punto.form;

import org.reflections.Reflections;
import punto.Punto;
import punto.cms.CmsPage;
import punto.log.Log;
import punto.mvc.Initiator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by MattWork on 8/10/2014.
 */
public class FormManager implements Initiator {

    Map<String,Form> forms = new HashMap<>();

    @Override
    public void init(){
        forms = getForms();
    }

    public Map<String,Form> getForms(){

        Set<Class<? extends Form>> fs = new Reflections("").getSubTypesOf(Form.class);
        Map<String,Form> forms = new HashMap<>();
        for(Class<? extends Form> f : fs){
            try {
                Form inst = f.newInstance();
                inst.build();
                forms.put(inst.getFormName(), inst);

            } catch (InstantiationException e) {
                Log.error("Could not instantiate CMS contents class: " + f.getName());
            } catch (IllegalAccessException e) {
                Log.error("Could not access CMS contents class: " + f.getName());
            }
        }
        return forms;

    }

}
