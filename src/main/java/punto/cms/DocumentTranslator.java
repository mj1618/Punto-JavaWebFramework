package punto.cms;

import org.apache.commons.lang.StringUtils;
import punto.log.Log;
import punto.util.Entry;

import java.util.*;

/**
 * Created by MattUpstairs on 15/09/2014.
 */
public class DocumentTranslator {

    public static Document expand(FlatDocument flat){

        PriorityQueue<Entry<List<String>, ContentItem>> flatItems = new PriorityQueue<>((a, b) -> {

            for (int i = 0; i < a.getKey().size() && i < b.getKey().size(); i++) {
                if (a.getKey().get(i).equals(b.getKey().get(i)) == false) {
                    return a.getKey().get(i).compareTo(b.getKey().get(i));
                }
            }
            return ((Integer) a.getKey().size()).compareTo(b.getKey().size());
        });

        flat.getItems().entrySet().forEach(entry -> {
            flatItems.add(new Entry<>(Arrays.asList(StringUtils.split(entry.getKey(), "/")), entry.getValue()));
        });

        return expand(flatItems);
    }

    private static Document expand(PriorityQueue<Entry<List<String>, ContentItem>> flatItems) {
        Document doc = Document.createBase();
//        Collections.sort(flatItems, (a, b) -> {
//            for (int i = 0; i < a.getKey().size() && i < b.getKey().size(); i++) {
//                if (a.getKey().get(i).equals(b.getKey().get(i)) == false)
//                    return a.getKey().get(i).compareTo(b.getKey().get(i));
//            }
//            return ((Integer) a.getKey().size()).compareTo(b.getKey().size());
//        });

        flatItems.forEach(entry ->{
            entry.getKey().stream().forEach(x->System.out.print("/"+x));
            System.out.println();
        });

        while(flatItems.size()>0){

            Entry<List<String>, ContentItem> entry = flatItems.poll();
            expandEntry(doc, entry);
        }

        Log.test(doc);

        return doc;
    }


    private static String listName(String s){
        return StringUtils.split(s, "$")[0];
    }

    private static String listId(String s){

        return StringUtils.split(s,"$")[1];
    }
    private static String listLabel(String s){

        return StringUtils.split(s,"$")[2];
    }


    private static boolean isList(String s){
        return s.contains("$");
    }
    private static void expandEntry(Document doc, Entry<List<String>, ContentItem> entry){

        Iterator<String> it = entry.getKey().iterator();
        Document current = doc;
        while(it.hasNext()){
            String part = it.next();


            if(it.hasNext()==false){
                if(isList(part))
                    current.itemList(listName(part),entry.getValue().getName(),true).add(entry.getValue());
                else
                    current.addItem(part,entry.getValue());
            } else {
                if(isList(part)){
                    //Debug.test("documentlist:"+listName(part)+" "+ listIndex(part));
                    current = current.documentList(listName(part), listLabel(part), true).get(listId(part),true);
                } else {
                    current = doc.document(part, true);
                }
            }
        }

    }

    public static FlatDocument flatten(Document doc){
        FlatDocument flat = new FlatDocument();
        doc.items.entrySet().forEach(item -> {
            flat.putItem(item.getValue().getPath(), item.getValue());
        });
        doc.documents.entrySet().forEach(d -> {
            flat.putAll(flatten(d.getValue()));
        });

        doc.itemLists.entrySet().forEach(ls -> {
            ls.getValue().ls.values().forEach(item -> {
                flat.putItem(item.getPath(), item);
            });
        });

        doc.documentLists.entrySet().forEach(ls -> {
            ls.getValue().ls.values().forEach(d -> {
                flat.putAll(flatten(d));
            });
        });

        return flat;
    }
    public static FlatCmsPage flatten(CmsPage page) {
        return new FlatCmsPage(page.getName(), DocumentTranslator.flatten(page.document()));
    }

    public static CmsPage expand(FlatCmsPage flatCmsPage, CmsPage page) {
        page.setDocument(expand(flatCmsPage.getDoc()));
        return page;
    }

}
