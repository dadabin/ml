package org.analyzer.db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by rimi on 2015/2/6.
 */
public class DBHelperTest {

    @Test
    public void getKeyTest() throws Exception {
        assertEquals("1,搜索",new DBHelper().getKey("ext"));
    }
}
