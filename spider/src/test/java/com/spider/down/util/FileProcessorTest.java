package com.spider.down.util;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by rimi on 2015/2/13.
 */
public class FileProcessorTest {

    @Test
    void downloadTest() throws Exception {

        new FileProcessor("234","http://ww1.sinaimg.cn/large/60d02b59gw1edchbta2cvg208w04we82.gif").saveGif();

    }
}
