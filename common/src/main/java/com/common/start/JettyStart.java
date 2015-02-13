package com.common.start;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created by rimi on 2015/2/13.
 */
public class JettyStart {

    public static void main(String[] args) throws Exception {
        String port=null;
        String path=null;
        String[] arrayOfString=args;
        int j=args.length;
        for(int i=0;i<j;i++){
            String arg=arrayOfString[i];
            if(arg.startsWith("-Dport="))
                port=arg.substring("-Dport=".length());
            else if(arg.startsWith("-Dpath=")){
                path=arg.substring("-Dpath=".length());
            }
        }
        if(port==null){
            System.out.println("-Dport=null -Dpath=null");
            return;
        }
        Server server = new Server(Integer.valueOf(port.trim()).intValue());
        WebAppContext webapp = new WebAppContext();
        webapp.setResourceBase(path);

        server.setHandler(webapp);
        server.start();
        server.join();


    }
}
