package com.baidu.iknow.apkpatch;


import java.net.URL;
import java.net.URLClassLoader;

public class OriginLoader extends URLClassLoader {
    
    public FixLoader otherClassLoder;
    
    public String otherLoadClassName;
    
    public OriginLoader(URL[] urls) {
        super(urls);
    }

    public OriginLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addJar(URL url) {
        this.addURL(url);
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = null;
        if(name.equals(otherLoadClassName)){
            return otherClassLoder.loadClass(name);
        }
        clazz = super.findClass(name);
        return clazz;
    }

}