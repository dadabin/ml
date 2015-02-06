package org.analyzer.conf;

import java.util.List;

/**
 * Created by rimi on 2015/2/5.
 */
public class DefaultConfig implements Configuration {

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean useSmart() {
        return false;
    }

    @Override
    public void setUseSmart(boolean useSmart) {

    }

    @Override
    public String getMainDictionary() {
        return null;
    }

    @Override
    public String getQuantifierDictionary() {
        return null;
    }

    @Override
    public List<String> getExtDictionarys() {
        return null;
    }

    @Override
    public List<String> getExtStopWordDictionarys() {
        return null;
    }

    @Override
    public String getSynonymsPath() {
        return null;
    }
}
