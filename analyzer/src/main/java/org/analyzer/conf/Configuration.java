package org.analyzer.conf;

import java.util.List;

/**
 * Created by rimi on 2015/2/5.
 */
public interface Configuration {

    /**
     * 分词使用智能切分策略 ， false则使用细粒度切分
     * @return
     */
    boolean useSmart();

    void setUseSmart(boolean useSmart);

    String getMainDictionary();

    /**
     * 获取量词词典路径
     * @return
     */
    String getQuantifierDictionary();

    /**
     * 获取扩展字典配置路径
     *
     * @return List<String> 相对类加载器的路径
     */
    List<String> getExtDictionarys();

    /**
     * 获取扩展停止词典配置路径
     *
     * @return List<String> 相对类加载器的路径
     */
    List<String> getExtStopWordDictionarys();

    /**
     * 获取同义词典的路径
     *
     * @return String 相对类的加载器
     **/
    String getSynonymsPath();






}
