package org.analyzer.dic;

import org.analyzer.conf.Configuration;

/**
 * Created by rimi on 2015/2/5.
 */
public class Dictionary {

    private static Dictionary singleton;

    //主词典对象
    private DictSegment _MainDict;
    //停用词词典
    private DictSegment _StopDict;
    //量词词典
    private DictSegment _QuantifierDict;


    private Configuration cfg;
    private static Dictionary inital(Configuration cfg){
        this.cfg=cfg;
    }

}
