package com.baidu.iknow.apkpatch;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        try {
            OriginLoader oloader = getOriginClassLoader(Main.class.getClassLoader());
            FixLoader floader = getFixClassLoader(Main.class.getClassLoader());
            oloader.otherClassLoder = floader;
            oloader.otherLoadClassName = "com.euler.patch.diff.DexDiffer";
            floader.otherClassLoder = oloader;
            Class mainClass = oloader.loadClass("com.euler.patch.Main");
            Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
            mainMethod.setAccessible(true);
            mainMethod.invoke(mainClass, (Object)(args));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static OriginLoader getOriginClassLoader(ClassLoader parent) throws MalformedURLException {
        URL[] urls = new URL[] {};
        OriginLoader loader = new OriginLoader(urls, parent);
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        int index = path.lastIndexOf(File.separator) + 1;
        path = path.substring(0, index);
        path = path + "apkpatch.jar";
        loader.addJar(new File(path).toURI().toURL());
        return loader;
    }
    
    private static FixLoader getFixClassLoader(ClassLoader parent) throws MalformedURLException {
        URL[] urls = new URL[] {};
        FixLoader loader = new FixLoader(urls, parent);
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        int index = path.lastIndexOf(File.separator) + 1;
        path = path.substring(0, index);
        path = path + "dexdiffer.jar";
        loader.addJar(new File(path).toURI().toURL());
        return loader;
    }
}
