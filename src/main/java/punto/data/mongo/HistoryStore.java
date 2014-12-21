package punto.data.mongo;

import punto.Punto;
import punto.history.RecordDO;
import punto.util.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by MattUpstairs on 3/10/2014.
 */
public class HistoryStore {
//    MongoStore store;

    public static String HISTORY_COLLECTION="history";

    public HistoryStore(String dbName){


//        store = new MongoStore(dbName, HISTORY_COLLECTION);
    }

    public void insert(RecordDO d){

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Punto.logFile, true)));
            out.println(Utils.toJson(d));
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
//        store.insert(d);
    }

}
