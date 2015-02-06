package org.analyzer;



import org.analyzer.lucene.IKSAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by rimi on 2015/2/6.
 */
public class AnalysisTest {

    @Test
    public void analysisTest() throws IOException {
        IKSAnalyzer analyzer = new IKSAnalyzer();
        String temp ="去哪儿搜索衣服搜索";
        TokenStream token=analyzer.tokenStream("", new StringReader(temp));
        CharTermAttribute term=token.addAttribute(CharTermAttribute.class);
        token.reset();
        List<String> arry=new ArrayList<String>();
        while(token.incrementToken()){
             System.out.println(term.toString());
            arry.add(term.toString());
        }
        token.end();
        token.close();
        for(int i=0;i<arry.size();i++){
            if(i==0)assertEquals("哪儿",arry.get(i));
            else if(i==1) assertEquals("搜索",arry.get(i));
            else if(i==2) assertEquals("衣服",arry.get(i));
            else assertEquals("搜索",arry.get(i));
        }

    }

    public static void main(String[] args) throws IOException {
        IKSAnalyzer analyzer = new IKSAnalyzer();
        String temp ="去哪儿<html></html>搜索衣服搜索";
        TokenStream token=analyzer.tokenStream("", new StringReader(temp));
        CharTermAttribute term=token.addAttribute(CharTermAttribute.class);
        token.reset();
      //  List<String> arry=new ArrayList<String>();
        while(token.incrementToken()){
            System.out.println(term.toString());
         //   arry.add(term.toString());
        }
        token.end();
        token.close();

    }

}
