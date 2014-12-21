package punto.cms;

import org.apache.commons.io.IOUtils;
import org.mongodb.morphia.annotations.Entity;
import punto.data.mongo.MongoId;
import punto.log.Log;
import punto.util.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;


@Entity(CmsPage.PAGES_COLLECTION)
public abstract class CmsPage {

	public static final String PAGES_COLLECTION = "pages";
    public static final String PAGE_FIELD_NAME = "name";
    public abstract void load();
    public abstract String getName();
    String name;
    Document doc;
    transient ContentManager manager;


    public static CmsPage defaultCmsPage(final String n){
        return new CmsPage(){
            public void load(){}
            public String getName(){return n;}
        };
    }

    @Override
    public Object clone(){
        return this.flatten().expand();
    }
    public Document addListDocument(String siteName,String path) {

        Log.test("pageName:" + name);
        Log.test("siteName:" + siteName);
        Log.test("path:" + path);

        Document toAdd = ContentManager.instance(siteName).getOriginalPageClone(name).doc.findDocumentList(path).getAny().get();
        doc.findDocumentList(path).addListDocument(toAdd);

        return toAdd;
    }
    public Optional<ContentItem> addListItem(String siteName,String path) {
        return doc.addContentListItem(ContentManager.instance(siteName).getOriginalPageClone(name),path);
    }

    public static CmsPage create(Class<? extends CmsPage> c, ContentManager manager) throws IllegalAccessException, InstantiationException {
        CmsPage page = c.newInstance();
        page.init(manager);
        return page;
    }

    public String name(){
        return getName();
    }
    public Document document(){
        return doc;
    }





	public CmsPage(){
        name = getName();
    }
    public void init(ContentManager manager){
        this.manager=manager;
        doc = Document.createBase();
    }

    public String content(String name){
        return doc.content(name);
    }

    public DocumentList docs(String name){
        return doc.documentLists.get(name);
    }

    public ContentItemList items(String name){
        return doc.itemLists.get(name);
    }


    public MongoId newFile(String filename) {
        return newFile(fileBytes(filename));
    }

    public byte[] fileBytes(String resource){
        return Resource.web(resource).bytes();
    }

    public MongoId newFile(byte[] bytes) {
        return manager.writeFile(bytes);
    }

    public MongoId newFile(InputStream is) throws IOException {
        return newFile(IOUtils.toByteArray(is));
    }


    public void newDocumentList(String name,String label) {
        doc.newDocumentList(name,label);
    }

    public void newContentList(String name,String label) {
        doc.newContentList(name,label);
    }

    public void loadDocumentList(String name,String label, DocumentListLoader loader) {
        doc.loadDocumentList(name,label,loader);
    }

    public void loadContentList(String name,String label, ContentItemListLoader loader) {
        doc.loadContentList(name,label,loader);
    }

    public void loadImage(String name, MongoId id) {
        doc.loadImage(name,id);
    }

    public void loadText(String name,String text) {
        doc.loadText(name,text);
    }


    public void setDocument(Document document) {
        this.doc = document;
    }

    public FlatCmsPage flatten() {
        return DocumentTranslator.flatten(this);
    }

}
