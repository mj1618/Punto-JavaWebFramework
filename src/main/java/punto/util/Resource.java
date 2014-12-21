package punto.util;

import org.apache.commons.io.IOUtils;
import punto.log.Log;
import punto.route.RouteManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by MattUpstairs on 24/09/2014.
 */
public class Resource {

    String relativePath;
    ResourceType type;

    private Resource(String relativePath){
        this(relativePath,ResourceType.WEB);
    }

    private Resource(String relativePath, ResourceType type){
        this.type=type;

        switch(type){
            case WEB:
                this.relativePath=relativePath;
                break;
            case CLASS_RESOURCE:
//                if(staticLinks())
//                    this.relativePath="/src/main/resources/resources/"+relativePath;
//                else
                this.relativePath="/WEB-INF/classes/"+relativePath;
                break;
        }

    }

    public static Resource web(String path){
        return new Resource(path);
    }

    public static Resource classResource(String path){
        return new Resource(path, ResourceType.CLASS_RESOURCE);
    }

    public static String absolute(String s) throws MalformedURLException {
        return new Resource(s).absolute();
    }

    public static String webPath(){
        return new Resource("/").absolute();
    }

    public Path absolutePath(){

        return Paths.get(absolute());
    }

    public boolean exists(){
        return file().exists();
    }

    public static boolean staticLinks(){
        return System.getProperty("punto.path")!=null;
    }
    public String absolute(){
        try {
            switch (type) {
                case WEB:
                    if (staticLinks())
                        return puntoPath() + relativePath;
                    else
                        try {

                            return new File(RouteManager.GetServletContext().getResource(relativePath).toURI()).getPath();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    break;
                case CLASS_RESOURCE:

                        try {
                            return new File(RouteManager.GetServletContext().getResource(relativePath).toURI()).getPath();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

            }
        }catch(Exception e){
            throw new RuntimeException("Couldn't find file:"+relativePath);
        }
        return "";
    }


    public static String puntoPath(){
        return System.getProperty("punto.path").replaceAll("\\\\", "/");
    }

    public InputStream inputStream() throws FileNotFoundException {

        if(staticLinks()){
            return new FileInputStream(new File(absolute()));
        } else {
            return RouteManager.GetServletContext().getResourceAsStream(relativePath);
        }
    }

    public byte[] bytes(){
        try {
            //Debug.test("file:"+file);
            return IOUtils.toByteArray(inputStream());
        } catch (Exception e) {
            Log.error("Could not find resource: " + relativePath);
        }
        return new byte[0];
    }

    public File file() {
        return absolutePath().toFile();
    }

}
