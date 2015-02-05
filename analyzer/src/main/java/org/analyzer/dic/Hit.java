package org.analyzer.dic;

/**
 * Created by rimi on 2015/2/5.
 * 表示一次词典匹配的命中
 *
 */
public class Hit {
    //Hit 不匹配
    private static final int UNMATCH = 0x00000000;
    //Hit完全匹配
    private static final int MATCH = 0x00000001;
    //Hit 前缀匹配
    private static final int PREFIX = 0x00000010;

    //该HIT当前状态，默认未匹配


}
