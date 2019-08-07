package com.sunred.privilegesAction;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Server {

    public static void loadService(){
        URL[] urls;
        try {
            urls = new URL[] { new URL("file:E:/GitHub/JavaEE/study-code/log-sample-maven/asset/client-privileges-1.0-SNAPSHOT.jar") };
            URLClassLoader ll = new URLClassLoader(urls);
            final Class a = ll.loadClass("com.sunred.privilegesAction.Client");
            Object o = a.newInstance();
            Method m = a.getMethod("doCheck", null);
            m.invoke(o, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        loadService();
    }

}
