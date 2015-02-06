package org.analyzer.lucene;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.analyzer.conf.Configuration;
import org.analyzer.conf.DefaultConfig;
import org.analyzer.dic.Dic;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rimi on 2015/2/6.
 */
public class IKTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {
    private boolean useSmart = false;
    // private Tokenizer _IKTokenizer = null;
    private int flushtime;
    private boolean isAutoUpdate = false;
    String dicPath = null;

    /** 存放map的参数 */
    // private Map<String, String> maps=null;
    public IKTokenizerFactory(Map<String, String> args) {
        super(args);
        assureMatchVersion();
        this.useSmart = getBoolean(args, "useSmart", false);
        this.dicPath = get(args, "dicPath");
        this.isAutoUpdate = getBoolean(args, "autoupdate", false);
        this.flushtime = getInt(args, "flushtime", 10);
    }

    @Override
    public Tokenizer create(AttributeSource.AttributeFactory attributeFactory, Reader reader) {
        return new IKTokenizer(reader, this.useSmart);
    }

    public boolean useSmart() {
        return this.useSmart;
    }

    public static Logger log = LoggerFactory.getLogger(IKTokenizerFactory.class);

    static {
        Configuration cfg = DefaultConfig.getInstance();
        Dic.initial(cfg);
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        try {

            String extdic[] = null;
            String stopdic[] = null;
            if (dicPath != null && dicPath.length() > 0) {
                String[] dic = dicPath.split("#");
                extdic = dic[0].split(",");
                stopdic = dic[1].split(",");

            }
            // 是否启用自动更新
            if (isAutoUpdate) {
                UpdateTime.extdics = extdic;
                UpdateTime.stopdics = stopdic;
                UpdateTime.loader = loader;
                ScheduledExecutorService flushService = Executors
                        .newSingleThreadScheduledExecutor();
                flushService.scheduleAtFixedRate(UpdateTime.UpdateSingle.getInstance(), 5,
                        flushtime, TimeUnit.SECONDS);
            }

            // BufferedReader reader= new BufferedReader( new
            // InputStreamReader(loader.openResource("suggest.txt"), "UTF-8")) ;
            //
            // String temp=null;
            // while((temp=reader.readLine())!=null){
            // System.out.println("*****************  :  "+temp);
            //
            // }
            // reader.close();
            // if(dicPath!=null &&dicPath.length()>0){
            //
            //
            //
            //
            //
            // }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

  /*  @Override
    public Tokenizer create(AttributeFactory arg0, Reader arg1) {
        // TODO Auto-generated method stub

        return new IKTokenizer(arg1, this.useSmart);
    }*/
}
