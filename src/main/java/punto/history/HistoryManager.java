package punto.history;

import punto.Punto;
import punto.data.mongo.HistoryStore;

/**
 * Created by MattUpstairs on 3/10/2014.
 */
public class HistoryManager {


    private HistoryStore store;
    private static HistoryManager manager;
    public static HistoryManager instance(){
        if(manager==null)
            manager=new HistoryManager();
        return manager;
    }

    public HistoryManager(){
        store = new HistoryStore(Punto.siteName);
    }

    public void record(RecordDO d){
        store.insert(d);
    }
}
