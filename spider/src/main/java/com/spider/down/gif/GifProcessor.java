package com.spider.down.gif;

import com.spider.down.util.FileProcessor;
import org.slf4j.Logger;

/**
 * Created by rimi on 2015/2/13.
 */
public class GifProcessor implements Runnable {

    public static Logger LOG = org.slf4j.LoggerFactory.getLogger(GifProcessor.class);

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
          //  e.printStackTrace();
            LOG.error(e.getMessage());
        }
    }


}
