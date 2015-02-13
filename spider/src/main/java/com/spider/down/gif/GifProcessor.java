package com.spider.down.gif;

import com.spider.down.util.FileProcessor;

/**
 * Created by rimi on 2015/2/13.
 */
public class GifProcessor implements Runnable {

    private String imgName;
    private String imgUrl;

    public GifProcessor(String name,String url){
        this.imgName =name;
        this.imgUrl=url;
    }

    public void run(){
        FileProcessor fp=new FileProcessor(this.imgName,this.imgUrl);
        try{
            System.out.println("下载保存图片URL:"+this.imgUrl);
            fp.saveGif();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
