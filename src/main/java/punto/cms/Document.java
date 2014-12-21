package punto.cms;

import org.mongodb.morphia.annotations.Embedded;
import punto.data.mongo.MongoId;
import punto.util.Resource;

import java.util.*;

/**
 * Created by MattUpstairs on 11/09/2014.
 */
@Embedded
public class Document extends Storable{
    Map<String,ContentItem> items = new HashMap<>();
    Map<String,Document> documents = new HashMap<>();
    Map<String,DocumentList> documentLists = new HashMap<>();
    Map<String,ContentItemList> itemLists = new HashMap<>();


    public Document(String name, String path){
        super(name,path);
    }
    public Document(String name, String path, String id){
        super(name,path,id);
    }


    public List<Document> documents(){
        return new ArrayList<>(documents.values());
    }
    public List<DocumentList> documentLists(){
        return new ArrayList<DocumentList>(documentLists.values());
    }
    public List<ContentItemList> itemLists(){
        return new ArrayList<ContentItemList>(itemLists.values());
    }

    public void addItem(String name,ContentItem value) {
        items.put(name,value);
    }
    public byte[] fileBytes(String resource){
        return Resource.web(resource).bytes();
    }


    public static Document createBase(){
        return new Document("doc","");
    }

    public String content(String name){
        return items.get(name).output();
    }

    public DocumentList docLists(String name){
        return documentLists.get(name);
    }

    public Document docs(String name){
        return documents.get(name);
    }

    public Document document(String name, boolean createIfNotExists){
        if(documents.containsKey(name)==false) {
            if (createIfNotExists)
                documents.put(name, new Document(name, subPath(name)));
            else return null;
        }

        return documents.get(name);
    }
    public DocumentList documentList(String name, String label, boolean createIfNotExists){
        if(documentLists.containsKey(name)==false) {
            if (createIfNotExists)
                documentLists.put(name, new DocumentList(name, subPath(name),label));
            else return null;
        }

        return documentLists.get(name);
    }

    public ContentItemList getItemList(String name){
        return itemLists.get(name);
    }

    public ContentItemList itemList(String name, String label, boolean createIfNotExists){
        if(itemLists.containsKey(name)==false) {
            if (createIfNotExists)
                itemLists.put(name, new ContentItemList(name, subPath(name),label));
            else return null;
        }

        return itemLists.get(name);
    }

    public ContentItemList items(String name){
        return itemLists.get(name);
    }

    public List<ContentItem> items(){
        return new ArrayList<>(items.values());
    }

    public Document newDocumentList(String name,String label) {
        DocumentList ls = DocumentList.create(name, subPath(name),label);
        documentLists.put(name,ls);
        return this;
    }

    public Document newContentList(String name,String label) {
        ContentItemList ls = ContentItemList.create(name, subPath(name),label);
        itemLists.put(name, ls);
        return this;
    }

    public Document loadDocumentList(String name,String label, DocumentListLoader loader) {
        DocumentList ls = DocumentList.create(name, subPath(name),label);
        loader.load(ls);
        documentLists.put(name,ls);
        return this;
    }

    public Document loadContentList(String name,String label, ContentItemListLoader loader) {
        ContentItemList ls = ContentItemList.create(name, subPath(name),label);
        loader.load(ls);
        itemLists.put(name, ls);
        return this;
    }

    public Document loadImage(String name, MongoId id) {
        ContentItem it = ContentItem.createFile(name,subPath(name), id);

        items.put(name,it);
        return this;
    }

    public Document loadText(String name,String text) {
        ContentItem it = ContentItem.createText(name,subPath(name),text);
        it.setText(text);
        items.put(name,it);
        return this;
    }


    public Optional<ContentItem> findItem(String path) {
        ContentItem ret = null;
        for(ContentItem v : items.values())
            if(v.getPath().startsWith(path))return Optional.of(v);

        for(Document d:documents.values()) {
            Optional<ContentItem> c = d.findItem(path);
            if (c.isPresent()) return c;
        }
        for(DocumentList ls:documentLists.values()){
            for(Document d:ls) {
                Optional<ContentItem> c = d.findItem(path);
                if (c.isPresent()) return c;
            }
        }

        for(ContentItemList ls:itemLists.values()){
            for(ContentItem v:ls) {
                if(v.getPath().startsWith(path))return Optional.of(v);
            }
        }

        return Optional.empty();
    }

    public Optional<ContentItem> addContentListItem(CmsPage page,String path) {
        for(Document d:documents.values()) {
            Optional<ContentItem> c = d.addContentListItem(page,path);
            if (c.isPresent()) return c;
        }
        for(DocumentList ls:documentLists.values()){
            for(Document d:ls){
                Optional<ContentItem> ci = d.addContentListItem(page,path);
                if(ci.isPresent())return ci;
            }
        }

        for(ContentItemList ls:itemLists.values()){
            if(ls.getPath().startsWith(path)){
                return Optional.of(ls.copyAdd(page));
            }
        }
        return Optional.empty();
    }

    public void recursiveRepath(String path) {
        this.setPath(path);

        for(ContentItem c:items.values()){
            c.setPath(subPath(c.getName()));
        }
        for(Document d:documents.values()){
            d.recursiveRepath(subPath(d.getName()));
        }
        for(ContentItemList ls:itemLists.values()){
            ls.setPath(subPath(ls.getName()));
            for(ContentItem c:ls)
                c.setPath(ls.subPath(c.getName()));
        }
        for(DocumentList ls:documentLists.values()){
            ls.setPath(subPath(ls.getName()));
            for(Document d:ls){
                d.recursiveRepath(ls.subPath(d.getName()));
            }
        }
    }


    public DocumentList findDocumentList(String path) {
        for(DocumentList ls:documentLists()){
            if(ls.getPath().equals(path))
                return ls;
        }

        for(DocumentList ls:documentLists()){
            for(Document d: ls.ls.values()){
                DocumentList found = d.findDocumentList(path);
                if(found!=null)return found;
            }
        }
        for(Document d:documents.values()){
            DocumentList found = d.findDocumentList(path);
            if(found!=null)return found;
        }
        return null;
    }
}
