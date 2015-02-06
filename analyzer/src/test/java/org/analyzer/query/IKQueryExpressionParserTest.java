package org.analyzer.query;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by rimi on 2015/2/6.
 */
public class IKQueryExpressionParserTest {

    @Test
    public void parseExpTest() throws ParseException {
        IKQueryExpressionParser parser = new IKQueryExpressionParser();
        String ikQueryExp = "newsTitle:'的两款《魔兽世界》插件Bigfoot和月光宝盒'";
        //String ikQueryExp = "(id='ABcdRf' && date:{'20010101','20110101'} && keyword:'魔兽中国') || (content:'KSHT-KSH-A001-18'  || ulr='www.ik.com') - name:'林良益'";
        Query result = parser.parseExp(ikQueryExp, true);
        String str="+newsTitle:\"两 款\" +newsTitle:\"魔 兽\" +newsTitle:\"世 界\" +newsTitle:\"插 件\" +newsTitle:bigfoot +newsTitle:\"月 光 宝 盒\"";

        System.out.println(result);
        System.out.println(str.equals(result));
        // assertEquals("+newsTitle:\"两 款\" +newsTitle:\"魔 兽\" +newsTitle:\"世 界\" +newsTitle:\"插 件\" +newsTitle:bigfoot +newsTitle:\"月 光 宝 盒\"",result);
    }

}
