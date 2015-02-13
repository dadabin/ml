package com.spider.down.html;

import org.junit.Test;

/**
 * Created by rimi on 2015/2/13.
 */
public class TestlebaziTest {

    @Test
    public void downloadTest() throws Exception {

        for (int i = 2; i < 92; i++) {
           Testlebazi.getImg("http://www.lebazi.com/hot/index_" + i + ".html");
        }


    }
}
