package punto.iam;

import org.reflections.Reflections;
import punto.Punto;
import punto.data.mongo.SecurityStore;
import punto.log.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MattUpstairs on 22/09/2014.
 */
public class IamManager {
    static SecurityStore store;
    public static void init(){
        store = new SecurityStore(Punto.siteName);


        if(store.isEmpty()){
            createUsers();
        }

    }

    private static void createUsers(){

        List<User> users = new ArrayList<>();

        new Reflections("").getSubTypesOf(UserInitiator.class).forEach(ui -> {
            try {
                users.addAll(ui.newInstance().createUsers());
            } catch (InstantiationException e) {
                Log.error("Could not instantiate initiator class: " + ui.getName());
            } catch (IllegalAccessException e) {
                Log.error("Could not access initiator class: " + ui.getName());
            }
        });

        store.addUsers(users);
    }
}
