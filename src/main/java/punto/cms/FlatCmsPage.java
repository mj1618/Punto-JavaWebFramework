package punto.cms;

/**
 * Created by MattUpstairs on 15/09/2014.
 */
public class FlatCmsPage {
    String name;
    public String _id;
    FlatDocument doc;

    public FlatCmsPage(){}

    public FlatCmsPage(String name, FlatDocument doc) {
        this.name = name;
        this._id=name;
        this.doc = doc;
    }

    public FlatCmsPage clear(){
        doc = new FlatDocument();
        return this;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FlatDocument getDoc() {
        return doc;
    }

    public void setDoc(FlatDocument doc) {
        this.doc = doc;
    }

    public CmsPage expand(){
        return DocumentTranslator.expand(this,CmsPage.defaultCmsPage(name));
    }
}
