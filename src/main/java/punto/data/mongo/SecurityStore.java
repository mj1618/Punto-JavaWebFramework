package punto.data.mongo;

import punto.iam.User;

import java.util.List;
import java.util.Optional;

/**
 * Created by MattUpstairs on 22/09/2014.
 */
public class SecurityStore {
    public static String SECURITY_COLLECTION="security";


    MongoStore store;

    public SecurityStore(String dbName){
        store = new MongoStore(dbName, SECURITY_COLLECTION);
    }

    public Optional<User> user(String username){
        return store.findOne("username",username, User.class);
    }


    public void addUsers(List<User> users) {
        for(User u:users){
            store.insert(u);
        }
    }

    public boolean isEmpty() {
        return store.collection().count()<=0;
    }
}
