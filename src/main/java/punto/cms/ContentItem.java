package punto.cms;

import org.mongodb.morphia.annotations.Embedded;
import punto.data.mongo.MongoId;
import punto.log.Log;

@Embedded
public class ContentItem extends Storable {

    String fileId;
    //byte[] bytes;
    ContentType type=ContentType.NONE;
    String text;
    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public ContentItem(String name,String path) {
        super(name,path);
    }

    public ContentItem(String name,String path,ContentType type) {
        this(name,path);
        this.type=type;
    }

    private ContentItem(String name,String path,ContentType type,String fileId) {
        this(name,path);
        this.type=type;
        this.fileId =fileId;
    }

    public static ContentItem createText(String name, String path,String text) {
        ContentItem item = new ContentItem(name,path,ContentType.TEXT);
        item.setText(text);
        return item;
    }

    public boolean isText(){
        return type==ContentType.TEXT;
    }
    public boolean isImage(){
        return type==ContentType.FILE;
    }

    public static ContentItem createFile(String name,String path, MongoId id) {
        ContentItem item = new ContentItem(name,path,ContentType.FILE,id.getId());
        return item;
    }

//    public static ContentItem createFile(String name, byte[] bytes,ContentManager manager) {
//        ContentItem item = new ContentItem(ContentType.FILE,name);
//        item.setId(manager.writeFile(bytes).toString());
//        return item;
//    }
//
//    public static ContentItem createFile(InputStream is, String name,ContentManager manager) throws IOException {
//        return ContentItem.createFile(name,IOUtils.toByteArray(is),manager);
//    }
    public String getFileId() {
        return fileId;
    }

    public String fileId(){
        return fileId;
    }

    public void setId(String id) {
        this.fileId = id;
    }




    public String name(){
        return super.getName();
    }

    public String type() { return type.toString(); }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }




    public String output() {

        switch(type){
            case TEXT:

                return getText();

            case FILE:
                return "cms/files/"+fileId;
            default:
                throw new RuntimeException("Content item was not text or file");
        }
    }

    @Override
    public String toString() {
        return "ContentItem{" +
                "fileId='" + fileId + '\'' +
                ", type=" + type +
                ", text='" + text + '\'' +
                '}';
    }

    public void set(String s) {
        if(type==ContentType.TEXT)
            this.setText(s);
        else if(type==ContentType.FILE)
            this.setFileId(s);
        else Log.error("content type not text or file:" + this);
    }

    public void setFileId(String id) {
        if(type==ContentType.FILE)
            fileId=id;
        else Log.error("content type not file:" + this);
    }

    public static ContentItem createFile(String item, String key, String val) {
        return createFile(item,key,new MongoId(val));
    }

    public static ContentItem createWithId(String name, String path, String id) {
        ContentItem it = new ContentItem(name,path);
        it.setId(id);
        return it;
    }
}
