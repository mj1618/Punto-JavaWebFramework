package punto.data.mybatis.mygen;

import punto.log.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by MattUpstairs on 24/11/2014.
 */
public class PuntoGenerator {

    String domainPath,mapperPath,servicePath,extendedPath,xmlPath,configPath;
    String domainPackage="uniswim.common.model.domain";
    String mapperPackage="uniswim.common.model.mapper";
    String servicePackage="uniswim.common.model.service2";
    String extendedPackage="uniswim.common.model.extended2";
    List<FK> fks;
    List<String> tables;

    public PuntoGenerator(String config, String domain, String mapper, String service, String extended, String xml) throws SQLException, ClassNotFoundException {
        this.domainPath=domain;
        this.mapperPath=mapper;
        this.servicePath=service;
        this.extendedPath=extended;
        this.xmlPath=xml;
        this.configPath=config;
        fks = getAllFks();
        tables = getTables();
    }
    public void generate() throws Exception {
        Log.test(fks);
        for(String t:tables){
            try {
                createDomainExtended(t);
                createMapperExtended(t);
                mapperExtend(t);
                createService(t);
                if(t.equals("form"))extendXml(t);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void extendXml(String t) throws IOException, SQLException, ClassNotFoundException {

        if(findFks(t).size()==0)return;
        Path path = Paths.get(xmlPath+"\\" + toCamelCase(t) + "Mapper.xml");
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(path), charset);
Log.test(content);
        String selectByExample = content.substring(content.indexOf("<select id=\"selectByExample\""),content.indexOf("</select>",content.indexOf("<select id=\"selectByExample\""))+"</select>".length());
        String selectByPk = content.substring(content.indexOf("<select id=\"selectByPrimaryKey\""),content.indexOf("</select>",content.indexOf("<select id=\"selectByPrimaryKey\""))+"</select>".length());

        selectByExample = selectByExample.replaceAll("(?s)<!--.*?-->","");
        selectByPk = selectByPk.replaceAll("(?s)<!--.*?-->","");


        selectByExample = selectByExample.replace("BaseResultMap","ExtendedResultMap");
        selectByExample = selectByExample.replace("selectByExample","selectExtendedByExample");

        selectByPk = selectByPk.replace("BaseResultMap","ExtendedResultMap");
        selectByPk = selectByPk.replace("selectByPrimaryKey","selectExtendedByPrimaryKey");

        content = content.substring(0,content.indexOf("</mapper>"));


        content+="\n\t<resultMap extends=\"BaseResultMap\" id=\"ExtendedResultMap\" type=\""+extendedPackage+"."+toCamelCase(t)+"Extended\">";

        for(FK fk:findFks(t)){
            content+="\n\t\t<association column=\""+fk.getFkCol()+"\" javaType=\""+domainPackage+"."+toCamelCase(fk.getPkTable())+"\" property=\""+toFieldCase(fk.getFkCol())+"Object\" select=\""+mapperPackage+"."+toCamelCase(fk.getPkTable())+"Mapper.selectByPrimaryKey\" />";
        }

        content+="\n\t</resultMap>\n";

        content+=selectByExample;
        content+="\n";
        content+=selectByPk;

        content+="\n</mapper>";
        Files.write(path, content.getBytes(charset));
    }

    private void createService(String t) {
        Charset charset = Charset.forName("US-ASCII");

        Path extFile = Paths.get(servicePath + "\\" + toCamelCase(t) + "Service.java");
        boolean hasExtension=false;
        File f = new File(servicePath + "\\" + toCamelCase(t) + "ServiceExtended.java");
        if(f.exists() && !f.isDirectory()){
            hasExtension=true;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(extFile, charset)) {
            writer.write("package "+servicePackage+";\n");
            writer.write("import "+domainPackage+".*;\n");
            writer.write("import "+extendedPackage+".*;\n");
            writer.write("import "+mapperPackage+".*;\n");
            writer.write("import punto.data.Mybatis;\n");

            writer.write("import java.util.List;\n");
            writer.write("public class "+toCamelCase(t)+"Service "+(hasExtension?"extends "+toCamelCase(t)+"ServiceExtended":"")+" {\n");


            writer.write(createServiceMethod(toCamelCase(t) + "","ret","selectByPrimaryKey","Integer","id",toCamelCase(t) + "Mapper"));
            writer.write(createServiceMethod("Integer","count","deleteByPrimaryKey","Integer","id",toCamelCase(t) + "Mapper"));
            writer.write(createServiceMethod("Integer","count","updateByPrimaryKey",toCamelCase(t),"record",toCamelCase(t) + "Mapper"));

            writer.write(createServiceMethod("Integer","count","insert",toCamelCase(t),"record",toCamelCase(t) + "Mapper"));


            writer.write(createServiceMethod("Integer","count","countByExample",toCamelCase(t) + "Example","example",toCamelCase(t) + "Mapper"));
            writer.write(createServiceMethod("Integer","count","deleteByExample",toCamelCase(t) + "Example","example",toCamelCase(t) + "Mapper"));
            writer.write(createServiceMethod("List<" + toCamelCase(t) + ">","ls","selectByExample",toCamelCase(t) + "Example","example",toCamelCase(t) + "Mapper"));


            if(findFks(t).size()>0) {
                writer.write(createServiceMethod("List<" + toCamelCase(t) + "Extended>","ls","selectExtendedByExample",toCamelCase(t) + "Example","example",toCamelCase(t) + "MapperExtension"));
                writer.write(createServiceMethod(toCamelCase(t) + "Extended","ret","selectExtendedByPrimaryKey","Integer","id",toCamelCase(t) + "MapperExtension"));
            }

            writer.write("}\n");
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        Log.test(toCamelCase(t));
    }
    public String createServiceMethod(String retType,String retName, String methodName,String paramType, String paramName,String mapperName){
        String s = "    public static "+retType+" "+methodName+"("+paramType+" "+paramName+"){\n" +
                "        Mybatis mb = new Mybatis();\n";
        if(retName!=null)s+= "        "+retType+" "+retName+";\n";
        s+=        "        try{\n";
        if(retName!=null)s+=retName+" = ";
        s+=
                "mb.mapper("+mapperName+".class)."+methodName+"("+paramName+");\n" +
                        "        } finally{\n" +
                        "            mb.close();\n" +
                        "        }\n" +
                        "\n" +
                        "        return "+(retName==null?"":retName)+";\n" +
                        "    }\n";
        return s;
    }
    private void mapperExtend(String t) throws IOException {
        if(findFks(t).size()==0)return;
        Path path = Paths.get(mapperPath+"\\" + toCamelCase(t) + "Mapper.java");
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("public class "+toCamelCase(t),"import "+extendedPackage+".*;\npublic class "+toCamelCase(t)+"Mapper extends "+toCamelCase(t)+"MapperExtension");
        Files.write(path, content.getBytes(charset));
    }

    private void createMapperExtended(String t) {
        if(findFks(t).size()==0)return;

        Charset charset = Charset.forName("US-ASCII");

        Path extFile = Paths.get(extendedPath + "\\" + toCamelCase(t) + "MapperExtension.java");

        try (BufferedWriter writer = Files.newBufferedWriter(extFile, charset)) {
            writer.write("package "+extendedPackage+";\n");
            writer.write("import "+domainPackage+".*;\n");
            writer.write("import java.util.List;\n");
            writer.write("public interface "+toCamelCase(t)+"MapperExtension {\n");

            writer.write("\tList<"+toCamelCase(t)+"Extended> selectExtendedByExample("+toCamelCase(t)+"Example example);\n");
            writer.write("\t"+toCamelCase(t)+"Extended selectExtendedByPrimaryKey(Integer "+toFieldCase(t)+"Id);\n");

            writer.write("}\n");
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        Log.test(toCamelCase(t));
    }



    private void createDomainExtended(String t) {
        if(findFks(t).size()==0)return;

        Charset charset = Charset.forName("US-ASCII");

        Path extFile = Paths.get(extendedPath + "\\" + toCamelCase(t) + "Extended.java");

        try (BufferedWriter writer = Files.newBufferedWriter(extFile, charset)) {
            writer.write("package "+extendedPackage+";\n");
            writer.write("import "+domainPackage+".*;\n");
            writer.write("public class "+toCamelCase(t)+"Extended extends "+toCamelCase(t) +" {\n");

            for(FK fk : findFks(t)){
                writer.write("\t"+toCamelCase(fk.getPkTable())+" "+toFieldCase(fk.getFkCol())+"Object;\n");
            }

            for(FK fk:findFks(t)){
                writer.write("\tpublic "+toCamelCase(fk.getPkTable())+" get"+toCamelCase(fk.getFkCol())+"Object() {\n");
                writer.write("\t\treturn "+toFieldCase(fk.getFkCol())+"Object;\n");
                writer.write("\t}\n");
                writer.write("\tpublic void set"+toCamelCase(fk.getFkCol())+"Object("+toCamelCase(fk.getPkTable())+" "+toFieldCase(fk.getFkCol())+"Object) {\n");
                writer.write("\t\tthis."+toFieldCase(fk.getFkCol())+"Object = "+toFieldCase(fk.getFkCol())+"Object;\n");
                writer.write("\t}\n");

            }

            writer.write("}\n");
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        Log.test(toCamelCase(t));
    }

    public List<FK> getAllFks() throws SQLException, ClassNotFoundException {

        List<FK> fks = new ArrayList<>();
        for(String t:getTables()){
            fks.addAll(getFks(t));
        }
        return fks;
    }

    private List<FK> findFks(String t) {
        return fks.stream().filter(fk -> fk.getFkTable().equals(t)).collect(Collectors.toList());
    }

    static String toFieldCase(String s){
        s=s.toLowerCase();
        s = s.replace("_id", "");
        s = s.replace("id","");


        s=toCamelCase(s);
        return s.substring(0,1).toLowerCase()+s.substring(1);
    }


    static String toMethodCase(String s){
        s=s.toLowerCase();
        s = s.replace("_id","");
        s = s.replace("id","");


        s=toCamelCase(s);
        return s;
    }

    static String toCamelCase(String s){
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts){
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }
    public List<String> getTables() throws SQLException, ClassNotFoundException {
        Connection conn = getMySqlConnection();
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        List<String> tables = new ArrayList<>();
        while (rs.next()) {
            tables.add(rs.getString("TABLE_NAME"));
        }
        return tables;
    }

    public List<FK> getFks(String table) throws SQLException, ClassNotFoundException {
        Connection conn = getMySqlConnection();
        List<FK> fks = new ArrayList<>();

        ResultSet rs = null;
        DatabaseMetaData meta = conn.getMetaData();
        // The Oracle database stores its table names as Upper-Case,
        // if you pass a table name in lowercase characters, it will not work.
        // MySQL database does not care if table name is uppercase/lowercase.
        //
        rs = meta.getExportedKeys(conn.getCatalog(), null, table);

        while (rs.next()) {
            fks.add(new FK(rs.getString("PKTABLE_NAME"), rs.getString("PKCOLUMN_NAME"), rs.getString("FKTABLE_NAME"), rs.getString("FKCOLUMN_NAME")));
        }

        conn.close();
        return fks;
    }

    public static void main(String args[]) throws Exception {
        String basePath = "C:\\Users\\MattWork\\IdeaProjects\\uniswim\\src\\main\\java\\uniswim\\common\\model";
        new PuntoGenerator(
                "C:\\Users\\MattWork\\IdeaProjects\\uniswim\\src\\main\\resources\\generator-config.xml",
                basePath+"\\domain",
                basePath+"\\mapper",
                basePath+"\\service2",
                basePath+"\\extended2",
                "C:\\Users\\MattWork\\IdeaProjects\\uniswim\\src\\main\\resources\\uniswim\\common\\model\\mapper")
                    .generate();

    }

    public Connection getMySqlConnection() throws ClassNotFoundException, SQLException {
        String driver = "org.mariadb.jdbc.Driver";
        String url = "jdbc:mariadb://localhost:3306/uniswim_old";
        String username = "root";
        String password = "root";

        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
