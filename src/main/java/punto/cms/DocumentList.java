package punto.cms;

import org.mongodb.morphia.annotations.Embedded;
import punto.util.Utils;

import java.util.Iterator;
import java.util.Optional;

@Embedded
public class DocumentList extends CmsList<Document> implements Iterable<Document>{

    public DocumentList(String name, String path, String label) {
        super(name,path,label);
    }
    public static DocumentList create(String name, String path,String label) {
        return new DocumentList(name,path,label);
    }

    public Document createNew(String listName,String listPath, String id){
        return new Document(listName,listPath,id);
    }

    @Override
    public Iterator<Document> iterator() {
        return ls.values().iterator();
    }

    public void addDocument(DocumentBuilder builder){
        builder.build(createNew(Utils.createId()));
    }

    public Document addListDocument(Document d) {

        Document n = createNew(Utils.createId());

        d.setName(n.getName());
        d.recursiveRepath(n.getPath());
        d.changeId();
        return d;
    }

    public Optional<Document> getAny() {
        return this.ls.values().stream().findAny();
    }
}
