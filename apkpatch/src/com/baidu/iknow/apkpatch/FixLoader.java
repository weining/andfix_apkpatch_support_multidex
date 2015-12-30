package com.baidu.iknow.apkpatch;


import java.net.URL;
import java.net.URLClassLoader;

public class FixLoader extends URLClassLoader {
    
    public OriginLoader otherClassLoder;
    
    public FixLoader(URL[] urls) {
        super(urls);
    }

    public FixLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addJar(URL url) {
        this.addURL(url);
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = null;
        try{
            clazz = super.findClass(name);
        }catch(ClassNotFoundException e){
            if(otherClassLoder!=null){
                clazz = otherClassLoder.loadClass(name);
            }
        }
        return clazz;
    }

}