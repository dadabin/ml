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

    /**
     *  保存路径，不存在就创建
     * @return
     */
    private String makeDir(){
        String strDir=PropertyUtil.getProperties().getProperty("dif");
        File dir=new File(strDir);
        if(!dir.exists()){
            dir.mkdir();
        }
        return strDir;
    }

    /**
     * 保存
     * @throws Exception
     */
    public void saveGif() throws Exception{
        String dir=makeDir();
        String file=dir+this.imgName+this.imgUrl.substring(this.imgUrl.lastIndexOf("."));
        BufferedOutputStream out=null;
        byte[] bit=this.download();
        if(bit.length>0){
            try{
                out=new BufferedOutputStream(new FileOutputStream(file));
                out.write(bit);
                out.flush();
            }finally{
                if(out !=null){
                    out.close();
                }
            }
        }
    }

    /**
     * 下载
     * @return
     * @throws Exception
     */
    public byte[] download() throws Exception{
        URL url=new URL(this.imgUrl);
        HttpURLConnection httpConn=(HttpURLConnection) url.openConnection();
        httpConn.connect();
        InputStream cin=httpConn.getInputStream();
        ByteArrayOutputStream outStream=new ByteArrayOutputStream();
        byte[] buffer=new byte[1024];
        int len=0;
        while((len=cin.read(buffer))!=-1){
            outStream.write(buffer,0,len);
        }
        cin.close();
        byte[] fileData =outStream.toByteArray();
        outStream.close();
        return fileData;

    }
}
