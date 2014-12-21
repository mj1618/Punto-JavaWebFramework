package punto.data;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by MattWork on 24/10/2014.
 */
public class Mybatis {
    private static SqlSessionFactory sqlSessionFactory;

    SqlSession sqlSession;

    public Mybatis(){

        try{
            init();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public <T> T mapper(Class<T> m){
        return sqlSession.getMapper(m);
    }

    public void init(){
        if(sqlSessionFactory==null)initSessionFactory();
        sqlSession = sqlSessionFactory.openSession();
    }
    public void initSessionFactory(){
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SqlSession session(){
        return sqlSession;
    }

    public void close(){
        if(sqlSession!=null) {
            sqlSession.commit();
            sqlSession.close();
        }
    }
}

