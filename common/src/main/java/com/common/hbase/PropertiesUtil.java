package com.common.hbase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件公共类
 *
 * @author zachary.zhang
 *
 */
public class PropertiesUtil {

    private static Properties properties = null;

    public static Properties getInstance() throws IOException {
        if (properties == null) {
            properties = new Properties();
            InputStream resourceAsStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(resourceAsStream);
        }
        return properties;
    }

    public static String getPropertyString(String propertyName) {
        String value = null;
        try {
            value = getInstance().getProperty(propertyName);
            if (value == null) {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value.toString();
    }

    public static int getPropertyInt(String propertyName) {
        int value = 0;
        try {
            value = Integer.valueOf(getInstance().getProperty(propertyName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
