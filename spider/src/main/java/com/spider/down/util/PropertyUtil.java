package com.spider.down.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by rimi on 2015/2/13.
 */
public class PropertyUtil {

    private static final Logger LOG= LoggerFactory.getLogger(PropertyUtil.class);

    public static Properties getProperties(){
        Properties result=new Properties();
        InputStream is = PropertyUtil.class.getResourceAsStream("config.properties");
        try{
            result.load(is);
            is.close();
        }catch(IOException e){
           // e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return result;
    }


}
