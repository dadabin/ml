package com.spider.down.gif;

import com.spider.down.util.PropertyUtil;
import org.apache.log4j.spi.LoggerFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by rimi on 2015/2/13.
 */
public class GifSpider implements Runnable {

    public static Logger log = org.slf4j.LoggerFactory.getLogger(GifSpider.class);

    volatile boolean isRunning = true;

    private ThreadPoolExecutor threadPool;
    BlockingQueue<String> queue;

    public GifSpider(BlockingQueue<String> queue){
        this.queue=queue;
        this.init();
    }

    private void init(){
        Properties pro= PropertyUtil.getProperties();
        int corePoolSize = Integer.parseInt(pro.getProperty("threadpool.corePoolSize"));
        int maxPoolSize = Integer.parseInt(pro.getProperty("threadpool.maxPoolSize"));
        int keepAliveSeconds = Integer.parseInt(pro.getProperty("threadpool.keepAliveSeconds"));
        int queueCap = Integer.parseInt(pro.getProperty("threadpool.queueCapacity"));
        BlockingQueue<Runnable> queue= new LinkedBlockingQueue<Runnable>(queueCap);

        this.threadPool = new ThreadPoolExecutor(corePoolSize,maxPoolSize,keepAliveSeconds, TimeUnit.SECONDS,queue);

    }

    public boolean isRunning(){
        return isRunning;
    }

    public void setRunning(boolean isRunning)
    {
        this.isRunning = isRunning;
    }

    public void run(){
        while (this.isRunning)
        {
            try
            {

                String url = this.queue.take();
                System.out.println("请求url：" + url);
                Document doc = Jsoup.connect(url).get();
                // 获取所有<a href>
                Elements s = doc.select("div.pic_list2").first().select("a[href]");
                for (Element e : s)
                {
                    // 有img 和 文字 两种href，指向相同德图片，只过滤图片href就行了
                    Elements s1 = e.select("img");
                    if (s1.size() != 0)
                    {
                        String imgUrl = e.absUrl("href");
                        String text = s1.attr("alt");
                        Document doc1 = Jsoup.connect(imgUrl).get();
                        Elements e1 = doc1.getElementById("endtext").select("img");
                        // 网页源码中是相对路径，要获取绝对路径
                        String realUrl = e1.attr("abs:src");
                        System.out.println("获取图片url：" + realUrl);
                        // 获取到图片url，扔给线程池处理
                        GifProcessor pro = new GifProcessor(text, realUrl);
                        this.threadPool.execute(pro);
                    }

                }
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
             //   e.printStackTrace();
                log.error(e.getMessage());
            } catch (IOException e)
            {
               // e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }

}
