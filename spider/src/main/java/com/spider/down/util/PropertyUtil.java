package com.spider.down.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by rimi on 2015/2/13.
 */
public class PropertyUtil {

    public static Properties getProperties(){
        Properties result=new Properties();
        InputStream is = PropertyUtil.class.getResourceAsStream("config.properties");
        try{
            result.load(is);
            is.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }


}
