package com.spider.down.html;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rimi on 2015/2/13.
 */
public class Testlebazi {

    public static void getHttpURL(String indexURL) throws Exception{
        Connection connection = Jsoup.connect(indexURL);
        connection.header("Host", "img.51ads.com");
        connection.header("Accept", "image/webp,*/*;q=0.8");
        connection.header("Accept-Encoding", "gzip,deflate,sdch");
        connection.header("Accept-Language", "en-US,en;q=0.8");
        connection
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
        connection.timeout(60000);
        Document document = connection.get();
        Elements httpURLElements = document.getElementsByTag("a");
        for (Element element : httpURLElements) {
            String href = element.attr("href");
            if (!isNull(href)) {
                if (href.startsWith("/")) {
                    href = "http://www.lebazi.com" + href;
                }
                System.out.println(href);
            }
        }
    }

    /**
     * get img src with httpUrl
     */
    public static void getImg(String httpUrl) throws Exception {
        Connection connection = Jsoup.connect(httpUrl);
        connection.header("Host", "img.51ads.com");
        connection.header("Accept", "image/webp,*/*;q=0.8");
        connection.header("Accept-Encoding", "gzip,deflate,sdch");
        connection.header("Accept-Language", "en-US,en;q=0.8");
        connection
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
        connection.timeout(60000);
        Document document = connection.get();
        // System.out.println(document.html());
        Elements imgElements = document.select("div.pic_list").first().select("a[href]");
        // System.err.println(elements);
        for (Element e : imgElements) {
            Elements s1 = e.select("img");
            if (s1.size() != 0) {
                System.out.println(s1.attr("abs:src"));
                System.out.println(s1.attr("alt"));
                download(s1.attr("abs:src"), s1.attr("alt") + ".gif", "D:\\spider");
            }

        }
    }

    public static void download(String fileUrl, String filename, String savePath)
            throws Exception {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Host", "img.51ads.com");
        connection.setRequestProperty("Accept", "image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
        connection
                .setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
        InputStream is = connection.getInputStream();

        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        File sf = new File(savePath);
        if (!sf.exists()) {
            sf.mkdirs();
        }
        OutputStream os = new FileOutputStream(sf.getPath() + "\\" + filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }


    public static boolean isNull(String string) {
       return  (null == string || string.equals(""));


    }
}
