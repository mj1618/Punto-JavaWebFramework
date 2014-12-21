package punto.history;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MattUpstairs on 3/10/2014.
 */
public class RecordDO {

    Date createdAt = new Date();
    RecordType type;
    String sessionId;
    public String note="";
    Map<String,String> data=new HashMap<>();

    public RecordDO(){}

    public RecordDO(RecordType type, String sessionId, String note, Map<String, String> data) {
        this.type = type;
        this.sessionId = sessionId;
        this.note = note;
        this.data = data;
    }

    public void addToNote(String s){
        note+=s+"\n";
    }

    public void put(String k, String v){
        data.put(k,v);
    }

    public RecordType getType() {
        return type;
    }

    public void setType(RecordType type) {
        this.type = type;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
