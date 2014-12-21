package punto.cms;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import punto.Punto;
import punto.data.mongo.ContentStore;
import punto.data.mongo.MongoId;
import punto.http.HttpContext;
import punto.log.Log;
import punto.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ContentManager{

    static Map<String, ContentManager> instances = new HashMap<>();

	private Map<String,CmsPage> pages = new HashMap<String,CmsPage>();



	ContentStore store;
    String siteName;

    public static ContentManager instance(String siteName){
        if(instances.containsKey(siteName))
            return instances.get(siteName);
        else {
            ContentManager mgr = new ContentManager(siteName);
            instances.put(siteName,mgr);
            return mgr;
        }

    }

    private ContentManager(String siteName){


        this.siteName=siteName;
        this.store = ContentStore.create(siteName);
        pages.putAll(pageInstances(siteName,this));



        if(Punto.dropdata()){
            drop();
            load();
            writeAll();
        } else {
            load();
            if(store.pages().count()==0L)
                writeAll();
        }

    }
	
	public Map<String,CmsPage> pageInstances(String siteName,ContentManager manager){
        Log.test("getting page instances");
        Set<Class<? extends CmsPage>> cs = new Reflections("").getSubTypesOf(CmsPage.class);
        Map<String,CmsPage> pages = new HashMap<>();
        for(Class<? extends CmsPage> c : cs){
            try {
                CmsPage page = CmsPage.create(c,manager);

                if(Punto.getSiteName().equals(siteName))
                    pages.put(page.getName(),page);
            } catch (InstantiationException e) {
                Log.error("Could not instantiate CMS contents class: " + c.getName());
            } catch (IllegalAccessException e) {
                Log.error("Could not access CMS contents class: " + c.getName());
            }
        }
        return pages;
	}

	public void load(){
		getPages().values().forEach(page -> {
			page.load();
		});
	}
    public void writeAll(){
        getPages().values().forEach(page -> {
            writePage(page);
        });
    }

    public CmsPage getOriginalPageClone(String name){


//        Debug.test("flattening:");
//        Debug.json(pages.get(name).flatten());
//
//        Debug.test("expanding:");
//        Debug.json(pages.get(name).flatten().expand().doc);

        return pages.get(name).flatten().expand();
    }

	private Map<String,CmsPage> getPages() {
		return pages;
	}
	
	public static void main(String args[]){
		Log.test(Utils.toJson(new ContentStore("perthparkour").readFlatPage(CmsPage.PAGE_FIELD_NAME, "header")));
	}

//    public InputStream readFile(String id){
//        return store.readPage(id);
//    }
//    public Object PutFile(byte[] bytes){
//        return store.writeFile(bytes);
//    }

	private void drop() {
		store.drop();
	}
//
//    public CmsPage readPage(String name) {
//        return store.readPage(name);
//    }

    public List<String> getPageNames() {



        return new ArrayList(pages.keySet());
    }

    public CmsPage pageInstance(String name){
        try {
            return getPages().get(name).getClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    public CmsPage readPage(String name) {
        return DocumentTranslator.expand(store.readFlatPage(CmsPage.PAGE_FIELD_NAME, name), pageInstance(name));
    }
    public FlatCmsPage readFlatPage(String name) {
        return store.readFlatPage(CmsPage.PAGE_FIELD_NAME, name);
    }
    public void writePage(CmsPage page) {
        store.writeFlatPage(DocumentTranslator.flatten(page));
    }
    public void writeFlatPage(FlatCmsPage page) {
        store.writeFlatPage(page);
    }

    public MongoId writeFile(byte[] bytes){
        return new MongoId(store.writeFile(bytes).toString());
    }
    public InputStream readFile(String id) {
        return store.readFile(id);
    }

    public Optional<Object> writeFile(InputStream inputStream) {
        try{
            return Optional.ofNullable(writeFile(IOUtils.toByteArray(inputStream)));
        } catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }


    public Optional<Object> writeFile(FileItem fileItem) throws IOException {
        return writeFile(fileItem.getInputStream());
    }


    public FlatCmsPage pageContentSubmission(HttpContext ctx, FlatCmsPage page) {

        List<String> paths = new ArrayList<>();

        ctx.inputs().forEach((key, ls) -> {
            String val = ls.get(0);

            boolean isImage=false;
            if(key.startsWith("img-")){
                isImage=true;
                key = key.substring("img-".length());
            }

            paths.add(key);


            if (page.getDoc().containsKey(key)) {
                page.getDoc().getItem(key).set(val);
                //record.addToNote("added item:" + key + " " + val);
            } else {
                //record.addToNote("created item:" + key + " " + val);

                ContentItem ci;
                if(isImage){
                    ci = ContentItem.createFile("item", key, val);
                } else {
                    ci =ContentItem.createText("item",key,val);
                }

                page.getDoc().putItem(key, ci);
            }
        });

        ctx.inputFiles().forEach((key,fileItem)->{
            if(page.getDoc().containsKey(key)){
                try {
                    page.getDoc().getItem(key).setFileId(writeFile(fileItem).get().toString());
                    //record.addToNote("added file:" + fileItem.getName() + " " + page.getDoc().getItem(key).getFileId());
                } catch (IOException e) {
                    Log.error("couldn't write file:" + fileItem.getName());
                }
            } else {
                try {
                    page.getDoc().getOrCreateItem(key).setFileId(writeFile(fileItem).get().toString());
                    //record.addToNote("created file:" + fileItem.getName() + " " + page.getDoc().getItem(key).getFileId());
                } catch (IOException e) {
                    Log.error("couldn't write file:" + fileItem.getName());
                }
            }
        });


        List<String> torem = new ArrayList<>();
        for(String key : page.getDoc().getItems().keySet()){
            boolean good=false;
            for(String inkey:paths){
                if(inkey.contains(key)){
                    good=true;
                    break;
                }
            }
            if(!good)torem.add(key);
        }

//        for(String t:torem){
//            page.getDoc().getItems().remove(t);
//        }

        writeFlatPage(page);
        return page;
    }
}
