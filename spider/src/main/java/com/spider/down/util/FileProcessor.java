package com.spider.down.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rimi on 2015/2/13.
 */
public class FileProcessor {

    private String imgName;
    private String imgUrl;

    public FileProcessor(String name,String url){
        this.imgName=name;
        this.imgUrl=url;
    }

    private String makeDir(){
        String strDir=PropertyUtil.getProperties().getProperty("dif");
    }
}
