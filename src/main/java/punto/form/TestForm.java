package punto.form;

import punto.form.items.TextItem;

/**
 * Created by MattWork on 8/10/2014.
 */
public class TestForm extends Form {
    public TestForm(){
        super();
    }

    @Override
    public void build(FormObject form) {

        form.buildItem(new TextItem("myItem","my item label","item default value"));

        form.buildObject("myObject","my object label", obj ->{
            obj.buildItem(new TextItem("mySubItem","my sub label","sub item default value"));
        });

        form.buildObjectList("myObjLs","my obj ls label",ls->{
            ls.buildReferenceObject("myObjLsObject", "my obj ls obj label", obj->{
                obj.buildItem(new TextItem("mySubItem2","my sub label2","sub item default value2"));
            });

            ls.loadObject("myObjLsObj1","asdf",obj->{

            });

        });

        form.buildItemList("myItLs","my it ls label",ls->{
            ls.buildItem(new TextItem("mySubItem3","my sub label3","sub item default value3"));

            ls.loadItem("mySubItem3","my sub label3",it->{

            });
        });

    }

    public static void main(String args[]){
        Form form = new TestForm();
        form.build();

        System.out.println(form.render());
    }

    @Override
    public String getFormName(){
        return "Test Form";
    }
}
